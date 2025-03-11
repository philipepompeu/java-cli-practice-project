package com.philipe.app.classes;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class Iso8583MessageBuilder {
    private final int PAN_POSITION = 2;
    private final int PROCESSING_CODE_POSITION = 3;

    private String messageType;
    private String transactionTypeDescription;
    private String bitmap;
    private String rawBitmap;
    private String rawMessage;
    private Map<Integer, String> fields = new HashMap<>();
    private String pan;
    private String processingCode;
    private String cardBrand;

    public Iso8583MessageBuilder parse(String rawMessage){

        this.rawMessage = rawMessage;
        
        this.messageType = this.rawMessage.substring(0, 4); // Exemplo: "0200"
        this.setBitMap(this.rawMessage.substring(4, 20));               
        
        this.parseFields();

        this.setPan(this.fields.get(PAN_POSITION));
        
        this.processingCode = this.fields.get(PROCESSING_CODE_POSITION);        

        this.transactionTypeDescription = this.getTransactionTypeDescription();    

        return this;
    }


    private void parseFields(){             

        int currentIndex = this.messageType.length() + this.rawBitmap.length(); // Começamos logo após o bitmap

        for (int i = 0; i < this.bitmap.length(); i++) {
            if (this.bitmap.charAt(i) == '1') { // Se o bit for 1, o campo está presente
                int fieldNumber = i + 1;
                int fieldLength = getFieldLength(fieldNumber, currentIndex); // Obtém o tamanho do campo

                if (fieldLength > 0 && currentIndex + fieldLength <= this.rawMessage.length()) {
                    String fieldValue;
                    int beginIndex = 0;
                    int endIndex = 0;
                    
                    if (this.isVariableLengthField(fieldNumber)) {                                                
                        beginIndex = currentIndex + 2;
                        endIndex = Math.max((this.rawMessage.length()-1),  currentIndex + (fieldLength - 2));                        
                    }else{
                        beginIndex = currentIndex;
                        endIndex = currentIndex + fieldLength;
                    }
                    
                    fieldValue = this.rawMessage.substring(beginIndex, endIndex);
                    this.fields.put(fieldNumber, fieldValue);
                    currentIndex += fieldLength; // Avança para o próximo campo
                }
            }
        }
    }

    private int getFieldLength(int fieldNumber, int currentIndex){
        
        switch (fieldNumber) {                
            case 2: return 16; //Inicialmente o PAN fixo de 12 dígitos
            case 3: return 6; // Código de Processamento
            case 4: return 12; // Valor da Transação
            case 7: return 10; // Data e Hora da Transação
            case 11: return 6; // Número de Sequência
            case 12: return 6; // Hora Local da Transação
            case 13: return 4; // Data Local da Transação
            case 22: return 3; // Modo de Entrada do Cartão
            case 37: return 12; // Número de Referência
            case 41: return 8; // Código do Terminal
            case 42: return 15; // Código do Estabelecimento
            case 49: return 3; // Código da Moeda
            case 52: return 16; // PIN (criptografado)
            default: 
                if (this.isVariableLengthField(fieldNumber)) { // Campos variáveis
                    int length = Integer.parseInt(this.rawMessage.substring(currentIndex-2, currentIndex)); // Primeiro 2 dígitos são o tamanho
                    return 2 + length; // 2 dígitos do tamanho + conteúdo real
                }
                return 0; // Campo desconhecido
        }
    }
        
      
    private boolean isVariableLengthField(int fieldNumber){

        int [] variableLengthFields = { 32, 35, 48, 55};

        return IntStream.of(variableLengthFields).anyMatch(v -> v==fieldNumber); // Verifica se o campo está na lista de campos variáveis       
        
    }
    private void setBitMap(String rawBitMap) {
        this.rawBitmap = rawBitMap;
        this.bitmap = String.format("%64s", new BigInteger(this.rawBitmap, 16).toString(2)).replace(' ', '0');
    }
        

    private void setPan(String pan){
        this.pan = pan;
        this.cardBrand = this.getCardBrand();
    }

    private String getCardBrand(){        
        if (pan.startsWith("4")) return "Visa";
        if (pan.matches("5[1-5].*") || pan.matches("222[1-9].*|22[3-9][0-9].*|2[3-6][0-9]{2}.*|27[01][0-9].*|2720.*")) return "Mastercard";
        if (pan.startsWith("34") || pan.startsWith("37")) return "American Express";
        if (pan.matches("5067.*|4576.*|4011.*|4312.*")) return "Elo";
        if (pan.matches("3841.*|6062.*")) return "Hipercard";
        if (pan.startsWith("36") || pan.startsWith("38")) return "Diners Club";
        
        return "Desconhecida";
    }

    private String getTransactionTypeDescription(){

        switch (messageType) {
            case "0100": return "Consulta de Saldo";
            case "0200":
                if (processingCode == null || processingCode.length() < 2) {
                    return "Transação Desconhecida";
                }

                switch (processingCode.substring(0, 2)) {
                    case "00": return "Compra à Vista";
                    case "01": return "Saque";
                    case "20": return "Parcelado Sem Juros";
                    case "26": return "Parcelado Com Juros";
                    default: return "Transação Desconhecida";
                }
            case "0400": return "Estorno";
            case "0800": return "Teste de Comunicação";
            default: return "MTI Desconhecido";
        }
    }
    public Iso8583Message build(){

        Iso8583Message message = new Iso8583Message(messageType, bitmap, fields);

        message.setCardBrand(cardBrand);
        message.setPan(pan);
        message.setProcessingCode(processingCode);
        message.setTransactionTypeDescription(transactionTypeDescription);        

        return message;
    }
}
