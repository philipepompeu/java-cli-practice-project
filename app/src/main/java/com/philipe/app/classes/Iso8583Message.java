package com.philipe.app.classes;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Iso8583Message {
    
    private final String messageType;
    private final String bitmap;
    private final Map<Integer, String> fields;
    private String pan;
    private String processingCode;
    private String cardBrand;
    private String transactionTypeDescription;

    public Iso8583Message(String messageType, String bitmap, Map<Integer, String> fields) {
        this.messageType = messageType;
        this.bitmap = bitmap;
        this.fields = fields;
    }

    @Override
    public String toString() {
        return String.format(   "Mensagem ISO 8583\n"
                                +"MTI: %s\n"
                                +"Bitmap: %s\n"
                                +"Bandeira: %s\n"
                                +"Operação: %s\n"
                                +"Campos: %s"
                                , messageType, bitmap, cardBrand, transactionTypeDescription, fields);
    }
}
