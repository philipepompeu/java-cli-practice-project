package com.philipe.app.services;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange exchange;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate, DirectExchange exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public void sendMessage(String routingKey, String message) {
        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, message);
        System.out.println("Mensagem enviada: " + message);
    }
    
}
