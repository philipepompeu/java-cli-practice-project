package com.philipe.app.classes;


import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Getter;

@Getter
public class QueueMessage {
    
    private String message;
    private OffsetDateTime timestamp;
    private UUID id;

    public QueueMessage(String message){
        this.message = message;        
        this.timestamp = OffsetDateTime.now();
        this.id = UUID.randomUUID();
    }

    public String toString(){
        return String.format("{ id: \"%s\", message: \"%s\", timestamp: \"%s\" }", this.id, this.message, this.timestamp);
    }
}
