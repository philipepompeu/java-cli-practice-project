package com.philipe.app.commands;




import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.philipe.app.classes.QueueMessage;
import com.philipe.app.config.RabbitMQConfig;
import com.philipe.app.services.RabbitMQConsumer;
import com.philipe.app.services.RabbitMQProducer;



@ShellComponent
public class QueueManagerCommand {

    private final RabbitMQProducer rabbitProducer;
    private final RabbitMQConsumer consumer;
    private ObjectMapper objectMapper = new ObjectMapper();

    public QueueManagerCommand(RabbitMQProducer rabbitProducer, RabbitMQConsumer consumer){
        this.rabbitProducer = rabbitProducer;
        this.consumer = consumer;        
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    @ShellMethod(key = "enqueue", value = "Enfileira uma mensagem.")
    public String enqueue(String mensagem) throws JsonProcessingException{
        
        rabbitProducer.sendMessage(RabbitMQConfig.ROUTING_KEY,
                                    objectMapper.writeValueAsString(new QueueMessage(mensagem)) );
        return "Mensagem enfileirada: " + mensagem;
    }
    
    @ShellMethod(key = "process-queue", value = "Processa todas as mensagens na fila.")
    public String processQueue() throws JsonMappingException, JsonProcessingException {
        String result = "";
        String msg;
        boolean validMsg  = true;
        do{
            msg = consumer.receive(RabbitMQConfig.QUEUE_NAME);
            validMsg = !(msg.isEmpty() || msg.isBlank());
            if (validMsg) {
                
                QueueMessage queueMessage = objectMapper.readValue(msg, QueueMessage.class);           
                
                result += queueMessage.toString() + "\n";
            }
        }while(validMsg);        
        
        
        return result + "\nProcessamento da fila concluído.";
    }
}
