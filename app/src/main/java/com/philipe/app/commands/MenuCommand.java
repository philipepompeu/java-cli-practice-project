package com.philipe.app.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class MenuCommand {
    
    @ShellMethod(key = "menu", value = "Exibe o menu de opções disponíveis.")
    public String menu() {
        return """
        Escolha uma opção:
        1 - Dizer Olá (hello <nome>)
        2 - Somar dois números (soma <num1> <num2>)
        3 - Sair
        """;
    }
}
