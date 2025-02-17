package com.philipe.app.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class FileManipulationCommand {
    
    private final String OUTPUT_DIR = "output/";
    private final Path outputPath = getOutputPath();

    private Path getOutputPath(){

        Path outputPath = Paths.get(OUTPUT_DIR);
        if (Files.notExists(outputPath)) {
            try {
                Files.createDirectories(outputPath);                
            } catch (IOException e) {                
                e.printStackTrace();
            }
        }

        return outputPath;
    }

    @ShellMethod(key = "save-text", value = "Salva o texto")
    public String saveTextInFile(String fileName, String text){        
        

        Path outPutFile = this.outputPath.resolve(fileName);
        try {
           
            Files.writeString(outPutFile,  (text + System.lineSeparator()), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            return e.getMessage();
            
        }        
        return String.format("Texto salvo no arquivo %s", outPutFile.toString());
    }

    @ShellMethod(key = "list-files", value = "Lista arquivos do diretório")
    public String listFilesOfDirectory(){ 
        String listOfFiles = "";        

        Path outPutPath = this.outputPath;

        listOfFiles = Arrays.stream(outPutPath.toFile().list())
                        .map( (x) -> x + (x.lastIndexOf('.') > 0 ? "": "\\") )     
                        .collect(Collectors.joining( System.lineSeparator() ));        

        return listOfFiles;

    }
    
    @ShellMethod(key = "read-file", value = "Ler o arquivo")
    public String readTextFile(String fileName){        
        String fileContent = "";        

        Path outPutFile = this.outputPath.resolve(fileName);
        try {
            

            
            
            /*File arquivo = new File(outPutFile.toUri());

            StringBuilder builder = new StringBuilder();
            Scanner myReader = new Scanner(arquivo);
            while (myReader.hasNextLine()) {

                builder.append(myReader.nextLine());
                
            }
            myReader.close();           
            fileContent = builder.toString();      
            */
            
            System.out.println(String.format("outPutFile.toString()=%s", outPutFile.toString() ));
            System.out.println(String.format("outPutFile.toAbsolutePath()=%s", outPutFile.toAbsolutePath().toString() ));
            System.out.println(String.format("Files.readString(outPutFile)=%s", Files.readString(outPutFile) ));

            
            fileContent = Files.readString(outPutFile);    
            
            

        } catch (IOException e) {
            fileContent = e.getMessage();
            
        }        
        return fileContent;
    }
}
