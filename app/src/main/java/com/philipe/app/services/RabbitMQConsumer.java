package com.philipe.app.services;

import java.util.Optional;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {
    
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange exchange;
    
    public RabbitMQConsumer(RabbitTemplate rabbitTemplate, DirectExchange exchange){
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public String receive(String queueName){ 

        Optional<Message> message = Optional.ofNullable(rabbitTemplate.receive(queueName));
        
        return message.isPresent() ? new String(message.get().getBody()) : "";        
    }
}
