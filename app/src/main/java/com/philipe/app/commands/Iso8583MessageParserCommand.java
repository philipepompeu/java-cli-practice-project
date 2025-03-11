package com.philipe.app.commands;

import org.springframework.shell.standard.ShellMethod;

import com.philipe.app.classes.Iso8583Message;
import com.philipe.app.classes.Iso8583MessageBuilder;

public class Iso8583MessageParserCommand {
    
    @ShellMethod(key = "parse-iso8583", value = "Interpreta uma mensagem ISO 8583.")
    public String parseIsoMessage(String mensagem) {

        Iso8583Message message = new Iso8583MessageBuilder().parse(mensagem).build();

        return message.toString();
    }
}
