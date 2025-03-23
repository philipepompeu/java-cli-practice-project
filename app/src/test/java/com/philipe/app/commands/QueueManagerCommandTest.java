package com.philipe.app.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.philipe.app.classes.QueueMessage;
import com.philipe.app.config.RabbitMQConfig;
import com.philipe.app.services.RabbitMQConsumer;
import com.philipe.app.services.RabbitMQProducer;

import org.springframework.context.annotation.Import;

import com.philipe.app.config.TestConfig;

@Import(TestConfig.class) 
@SpringBootTest
public class QueueManagerCommandTest {

    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Mock
    private RabbitMQProducer producer;

    @Mock
    private RabbitMQConsumer consumer;

    private QueueManagerCommand command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper.registerModule(new JavaTimeModule());

        command = new QueueManagerCommand(producer, consumer);
    }

    @Test
    void enqueue_ShouldQueueAMessage() throws JsonProcessingException{
        String message = "Text of the Message";
        
        doNothing().when(producer).sendMessage(anyString(),anyString());

        String result = command.enqueue(message);

        assertTrue(result.contains(message));

        // Verificando se o método foi chamado corretamente
        verify(producer, times(1)).sendMessage(anyString(),anyString());
        
    }

    @Test
    void processQueue_ShouldConsumeAllMessages() throws JsonMappingException, JsonProcessingException{
        String message = "Text of the Message(processQueue)";
        doNothing().when(producer).sendMessage(anyString(),anyString());
        
        command.enqueue(message);      
        

        when(consumer.receive(anyString()))
            .thenReturn(objectMapper.writeValueAsString(new QueueMessage(message)))
            .thenReturn("");
        
        String result = command.processQueue();
        
        assertTrue(result.contains("Message...: "+message));
        
        verify(consumer, times(2)).receive(anyString());
    }
    
}
