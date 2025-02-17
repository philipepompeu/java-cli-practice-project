package com.philipe.app.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class HelloCommand {

    @ShellMethod(key = "hello", value = "Exibe uma saudação personalizada.")
    public String hello(String nome) {
        return "Olá, " + nome + "!";
    }
    
}
