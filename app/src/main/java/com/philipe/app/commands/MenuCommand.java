package com.philipe.app.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import jakarta.annotation.PostConstruct;

@ShellComponent
public class MenuCommand {

    @PostConstruct
    public void showMenu(){

        //System.out.println(menu());
    }
    
    @ShellMethod(key = "menu", value = "Exibe o menu de opções disponíveis.")
    public String menu() {
        return """
        Escolha uma opção:
        1 - Dizer Olá (hello <nome>)
        2 - save-text <arquivo.txt> <texto>
        3 - read-file <arquivo.txt>
        4 - list-files
        """;
    }
}
