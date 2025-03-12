package com.philipe.app.classes;


import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QueueMessage {
    
    private String message;
    private OffsetDateTime timestamp;
    private UUID id;

    public QueueMessage(String message){
        this.message = message;        
        this.timestamp = OffsetDateTime.now(ZoneId.of("America/Sao_Paulo"));
        this.id = UUID.randomUUID();
    }

    public String toString(){

        String message  =   "--------------------------------------\n"+
                            "ID........: " + this.id + "\n" +
                            "Message...: " + this.message + "\n" +
                            "Data/Hora.: " + this.timestamp.toString() + "\n" +
                            "--------------------------------------\n";                            
        return message;
    }
}
