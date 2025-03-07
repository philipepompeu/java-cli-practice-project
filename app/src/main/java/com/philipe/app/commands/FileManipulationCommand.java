package com.philipe.app.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

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
        try(Stream<String> fileLines = Files.lines(outPutFile)) {            

            StringBuilder builder = new StringBuilder();
            int i = 1;
            for (String line : fileLines.toList()) {
                
                builder.append(String.format("%d: %s",i, line) + System.lineSeparator());
                
                i++;
            }
            
            return builder.toString();
        } catch (IOException e) {
            String error = String.format("Erro ao ler o arquivo '%s': %s", fileName, e.getMessage());
            
            AppLogger.log(error);            
            return error;
        }
    }
    
    @ShellMethod(key = "search-text", value = "Busca um texto dentro de um arquivo.")
    public String findTextInFile(String fileName, String textToBeFound) {
        Path outPutFile = this.outputPath.resolve(fileName);
        try(Stream<String> fileLines = Files.lines(outPutFile)) {            

            StringBuilder builder = new StringBuilder();
            int i = 1;
            for (String line : fileLines.toList()) {
                
                if (line.toUpperCase().contains(textToBeFound.toUpperCase())) {                    
                    builder.append(String.format("%d: %s",i, line) + System.lineSeparator());
                }                
                
                i++;
            }            

            if (builder.isEmpty()) {                
                return String.format("Não há ocorrências de '%s' no arquivo %s",textToBeFound, fileName);
            }
            
            return String.format("Linhas que contêm '%s':", textToBeFound) + System.lineSeparator() + builder.toString();
        } catch (IOException e) {
            String error = String.format("Erro ao ler o arquivo '%s': %s", fileName, e.getMessage());
            
            AppLogger.log(error);            
            return error;
        }
    }

    @ShellMethod(key = "count-words", value = "Conta o número de palavras em um arquivo texto.")
    public String countWords(String fileName) {
        Path outPutFile = this.outputPath.resolve(fileName);
        long count = 0;           
        
        try (Stream<String> fileLines = Files.lines(outPutFile)) {

            fileLines.flatMap(line -> Stream.of( line.split("\\s+") ) ).count();            
            
        } catch (Exception e) {
            String error = String.format("Erro ao ler o arquivo '%s': %s", fileName, e.getMessage());
            
            AppLogger.log(error);            
            return error;
        }
        
        return String.format("O arquivo '%s' contém %d palavras.", fileName, count);        
        
    }

    @ShellMethod(key = "delete-file", value = "Deleta arquivo")
    public String deleteFile(String fileName) {
        Path outPutFile = this.outputPath.resolve(fileName);
        
        try {

            if (Files.exists(outPutFile)) { 
                AppLogger.log("Arquivo '{}' encontrado e será deletado.", fileName);
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

    @ShellMethod(key = "async-save", value = "Salva o texto em um arquivo no diretório de saída.")
    public String asyncSaveTextInFile(String fileName, String text) {

        CompletableFuture.runAsync(() -> saveTextInFile(fileName, text), executor);        

        return String.format("Salvamento iniciado para '%s' em segundo plano.", fileName);
    }
    
    @ShellMethod(key = "merge-files", value = "Junta N arquivos num único arquivo.")
    public String mergeFiles(String fileName, String... filesToBeMerged) {

        for(String file : filesToBeMerged){                        
            
            CompletableFuture.runAsync(() ->{ 
                Path outPutFile = this.outputPath.resolve(file) ;

                try (Stream<String> fileLines = Files.lines(outPutFile)) {

                    String text = fileLines.collect(Collectors.joining( System.lineSeparator() ));
                    
                    saveTextInFile(fileName, text);
                    
                } catch (Exception e) {
                    String error = String.format("Erro ao ler o arquivo '%s': %s", fileName, e.getMessage());
            
                    AppLogger.log(error);                                
                }
                
            
            }, executor);           
            

        }

        return String.format("Arquivo %s gerado.", fileName);        
       
    }


   

}
