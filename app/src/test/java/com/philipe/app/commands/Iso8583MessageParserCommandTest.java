package com.philipe.app.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Iso8583MessageParserCommandTest {

    private Iso8583MessageParserCommand command;
    
    @BeforeEach
    void setUp() {
        command = new Iso8583MessageParserCommand();
    }

    @Test
    void shouldParseSimpleMessage(){


        String rawMessage = "02006038000000A180125412345678901234560000001000002301011234561234560840001234567890";

        String result = command.parseIsoMessage(rawMessage);

        System.out.println(result);

        assertTrue(result.contains("MTI: 0200"));

    }
}
