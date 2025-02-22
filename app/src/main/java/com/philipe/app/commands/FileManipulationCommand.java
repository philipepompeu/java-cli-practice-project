package com.philipe.app.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.philipe.app.utilities.AppLogger;


@ShellComponent
public class FileManipulationCommand {

    
    private static final String OUTPUT_DIR = "output/";
    private final Path outputPath = getOutputPath();

    private Path getOutputPath() {
        Path outputPath = Paths.get(OUTPUT_DIR);
        if (Files.notExists(outputPath)) {
            try {
                Files.createDirectories(outputPath);
            } catch (IOException e) {
                AppLogger.log("Erro ao criar diretório de saída: %s", e.getMessage());
            }
        }
        return outputPath;
    }

    @ShellMethod(key = "save-text", value = "Salva o texto em um arquivo no diretório de saída.")
    public String saveTextInFile(String fileName, String text) {
        Path outPutFile = this.outputPath.resolve(fileName);
        try {
            Files.writeString(outPutFile, text + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return String.format("Texto salvo no arquivo: %s", outPutFile.toAbsolutePath());
        } catch (IOException e) {

            String error = String.format("Erro ao salvar o arquivo '%s': %s", fileName, e.getMessage());
            
            AppLogger.log(error);
            return error;
        }
    }

    @ShellMethod(key = "list-files", value = "Lista os arquivos do diretório de saída.")
    public String listFilesOfDirectory() {
        try {
            return Files.list(this.outputPath)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {            
            AppLogger.log("Erro ao listar arquivos: %s ", e.getMessage());
            return "Erro ao listar os arquivos no diretório.";
        }
    }

    @ShellMethod(key = "read-file", value = "Lê o conteúdo de um arquivo no diretório de saída.")
    public String readTextFile(String fileName) {
        Path outPutFile = this.outputPath.resolve(fileName);
        try {

            Stream<String> fileLines = Files.lines(outPutFile);

            StringBuilder builder = new StringBuilder();
            int i = 1;
            for (String line : fileLines.toList()) {
                
                builder.append(String.format("%d: %s",i, line) + System.lineSeparator());
                
                i++;
            }
            fileLines.close();
            return builder.toString();
        } catch (IOException e) {
            String error = String.format("Erro ao ler o arquivo '%s': %s", fileName, e.getMessage());
            
            AppLogger.log(error);            
            return error;
        }
    }

    @ShellMethod(key = "delete-file", value = "Deleta arquivo")
    public String deleteFile(String fileName) {
        Path outPutFile = this.outputPath.resolve(fileName);

        
        try {

            if (Files.exists(outPutFile)) { 
                AppLogger.log("Arquivo %s encontrado e será deletado.", fileName);
                Files.delete(outPutFile);
            }else{
                throw new FileNotFoundException(String.format("Arquivo %s não existe", outPutFile.toAbsolutePath().toString() ));                
            }
            
            return String.format("Arquivo %s excluído.", fileName);
        } catch (Exception e) {
            String error = String.format("Erro ao ler o arquivo '%s': %s ", fileName, e.getMessage() );
            
            AppLogger.log(error);            
            return error;
        }
    }
}
