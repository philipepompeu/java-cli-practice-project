package com.philipe.app.commands;




import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.philipe.app.classes.QueueMessage;
import com.philipe.app.config.RabbitMQConfig;
import com.philipe.app.services.RabbitMQConsumer;
import com.philipe.app.services.RabbitMQProducer;



@ShellComponent
public class QueueManagerCommand {

    private final RabbitMQProducer rabbitProducer;
    private final RabbitMQConsumer consumer;
    

    public QueueManagerCommand(RabbitMQProducer rabbitProducer, RabbitMQConsumer consumer){
        this.rabbitProducer = rabbitProducer;
        this.consumer = consumer;
    }
    
    @ShellMethod(key = "enqueue", value = "Enfileira uma mensagem.")
    public String enqueue(String mensagem){        
        
        rabbitProducer.sendMessage(RabbitMQConfig.ROUTING_KEY, new QueueMessage(mensagem).toString());        

        return "Mensagem enfileirada: " + mensagem;
    }
    
    @ShellMethod(key = "process-queue", value = "Processa todas as mensagens na fila.")
    public String processQueue() {
        String msg = "";
        while (msg != "Nenhuma mensagem na fila.") {
            msg = consumer.receive(RabbitMQConfig.QUEUE_NAME);
            System.out.println(msg);
        }

        return "";
    }
}
