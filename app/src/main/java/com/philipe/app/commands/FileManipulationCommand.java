package com.philipe.app.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.philipe.app.classes.ReportItem;
import com.philipe.app.utilities.AppLogger;

import jakarta.annotation.PreDestroy;


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

    @PreDestroy
    public void shutdownExecutor() {
        executor.shutdown();
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
            for (String line : (Iterable<String>) fileLines::iterator) {
                
                builder.append(String.format("%d: %s%n",i, line));
                
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
        String searchUpper  = textToBeFound.toUpperCase();
        try(Stream<String> fileLines = Files.lines(outPutFile)) {            

            StringBuilder builder = new StringBuilder();
            int i = 1;
            for (String line : (Iterable<String>) fileLines::iterator ) {
                
                if (line.toUpperCase().contains(searchUpper)) {                    
                    builder.append(String.format("%d: %s%n",i, line));
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

            count = fileLines
            .flatMap(line -> Stream.of( line.split("\\s+") ) )
            .filter(word -> !(word.isBlank() || word.isEmpty()) )
            .count();            
            
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

            AppLogger.log("Arquivo '{}' encontrado e será deletado.", fileName);
            Files.delete(outPutFile);            
            
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
    public String mergeFiles(String outPutFileName, String... filesToBeMerged) {

        for(String file : filesToBeMerged){            
            
            try {
                Path inputFile = this.outputPath.resolve(file) ;
                
                if (Files.notExists(inputFile)) {                    
                    throw new Exception( String.format("Arquivo %s não encontrado.", file) );
                }

                String text = Files.readString(inputFile);
                
                saveTextInFile(outPutFileName, text);
                
            } catch (Exception e) {
                String error = String.format("Erro ao gerar o arquivo '%s': %s", outPutFileName, e.getMessage());
        
                AppLogger.log(error);                                
            }           

        }

        return String.format("Arquivo %s gerado.", outPutFileName);        
       
    }

    @ShellMethod(key = "split-file", value = "Divide o arquivo em N partes")
    public String splitFile(String fileName, int numberOfLines) {
        Path outPutFile = this.outputPath.resolve(fileName) ;

        boolean hasExension = fileName.contains(".");
        String nameWithoutExtension = hasExension ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
        String extension = hasExension ? fileName.substring(fileName.lastIndexOf('.')) : "";
                        
        ArrayList<String> generatedFiles = new ArrayList<String>();

        try (Stream<String> fileLines = Files.lines(outPutFile)) {

            int fileCount = 1;
            int lineCount = 0;
            StringBuilder builder = new StringBuilder();

            for (String line : (Iterable<String>) fileLines::iterator) {
                lineCount++;
                
                builder.append(line+ System.lineSeparator());                
                if (lineCount == numberOfLines) {
                    String text = builder.toString();
                    builder.setLength(0);
                    
                    String newFileName = String.format("%s_%d%s", nameWithoutExtension, fileCount, extension);
                    asyncSaveTextInFile(newFileName, text);

                    generatedFiles.add(newFileName);
                    
                    fileCount++;
                    lineCount = 0;
                }                
                
            }
            
            if (builder.length() > 0) {
                String text = builder.toString();
                builder.setLength(0);
                String newFileName = String.format("%s_%d%s", nameWithoutExtension, fileCount, extension);
                asyncSaveTextInFile(newFileName, text);        
                generatedFiles.add(newFileName);        
            }          
            
            
            
        } catch (Exception e) {
            String error = String.format("Erro ao ler o arquivo '%s': %s", fileName, e.getMessage());
    
            AppLogger.log(error);                                
        }        

        return generatedFiles.stream()
                                .map(file -> String.format("Arquivo %s gerado.", file))
                                .collect(Collectors.joining("\n"));       
       
    }


    @ShellMethod(key = "compress-file", value = "Compacta um arquivo.")
    public String compressFile(String outPutFileName, String fileToBeCompressed) {

        
        try (GZIPOutputStream gzipOut = new GZIPOutputStream(new FileOutputStream(this.outputPath.resolve(outPutFileName).toString()))) {            
            
            Path inputFile = this.outputPath.resolve(fileToBeCompressed) ;
            
            if (Files.notExists(inputFile)) {                    
                throw new Exception( String.format("Arquivo %s não encontrado.", fileToBeCompressed) );
            }

            gzipOut.write(Files.readAllBytes(inputFile));                    
            gzipOut.close();            

        } catch (Exception e) {
            
            String error = String.format("Erro ao gerar o arquivo '%s': %s", outPutFileName, e.getMessage());
        
            AppLogger.log(error);   
        }

        return String.format("Arquivo compactado %s gerado.", outPutFileName);        
       
    }

    @ShellMethod(key = "decompress-file", value = "Descompacta um arquivo.")
    public String decompressFile(String inputFileName, String fileToBeDescompressed) {

        
        try (GZIPInputStream gzipOut = new GZIPInputStream(new FileInputStream(this.outputPath.resolve(inputFileName).toFile()))) {            
            
            Path outPutFile = this.outputPath.resolve(fileToBeDescompressed) ;
            
            Files.write(outPutFile, gzipOut.readAllBytes());      

            gzipOut.close();            

        } catch (Exception e) {
            
            String error = String.format("Erro ao gerar o arquivo '%s': %s", inputFileName, e.getMessage());
        
            AppLogger.log(error);   
        }

        return String.format("Arquivo compactado %s gerado.", inputFileName);        
       
    }

    @ShellMethod(key = "compress-files", value = "Junta N arquivos num único arquivo.")
    public String compressFiles(String outputFileName, String... filesToBeCompressed) {
       
        Path tarPath = this.outputPath.resolve(outputFileName + ".tar");
        Path tarGzPath = this.outputPath.resolve(outputFileName + ".tar.gz");

        try {

            try (TarArchiveOutputStream tarOut = new TarArchiveOutputStream(new FileOutputStream(tarPath.toFile()))) {
                // Adiciona cada arquivo ao TAR
                for (String file : filesToBeCompressed) {
                    Path inputFile = this.outputPath.resolve(file);
                    
                    if (Files.notExists(inputFile)) {
                        throw new Exception(String.format("Erro: Arquivo '%s' não encontrado.", file));                        
                    }
    
                    addFileToTar(tarOut, inputFile.toFile());
                }
            }

            this.compressFile(tarGzPath.toString(), tarPath.toString());// Agora compacta o TAR para TAR.GZ            
            this.deleteFile(tarPath.toString());// Exclui o TAR intermediário para manter apenas o .tar.gz            
            
        } catch (Exception e) {
            String error = String.format("Erro ao gerar compactar arquivos: %s", e.getMessage());        
            AppLogger.log(error);   
        }        

        return String.format("Arquivo compactado %s gerado com sucesso.", tarGzPath.toAbsolutePath());
    }
    
    private void addFileToTar(TarArchiveOutputStream tarOut, File file) throws IOException {
        TarArchiveEntry entry = new TarArchiveEntry(file, file.getName());
        entry.setSize(file.length());
        tarOut.putArchiveEntry(entry);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                tarOut.write(buffer, 0, length);
            }
        }

        tarOut.closeArchiveEntry();
    }


    @ShellMethod(key = "sort-file", value = "Ordenar o conteúdo de um arquivo")
    public String sortFile(String inputFileName) {

        Path inputFile = this.outputPath.resolve(inputFileName) ;
        
        try(Stream<String> fileLines = Files.lines(inputFile)) {

            StringBuilder builder = new StringBuilder();        

            fileLines.sorted()
                    .map(line -> Normalizer.normalize(line, Normalizer.Form.NFKD))
                    .collect(Collectors.toList())
                    .forEach(line -> builder.append(line + System.lineSeparator()));

            String text = builder.toString();            

            Files.writeString(inputFile, text, StandardOpenOption.CREATE);           
            
        } catch (Exception e) {
            String error = String.format("Erro ao gerar o arquivo '%s': %s", inputFileName, e.getMessage());
    
            AppLogger.log(error);                                
        }       

        return String.format("Arquivo %s ordenado.", inputFileName);        
       
    }

    @ShellMethod(key = "generate-report", value = "Criar um relatório baseado em um arquivo CSV.")
    public String generateReport(String inputFileName) {

        Path inputFile = this.outputPath.resolve(inputFileName) ;
        
        try(Stream<String> fileLines = Files.lines(inputFile)) {

            StringBuilder builder = new StringBuilder(); 
            HashMap<String, ReportItem> report = new HashMap<String, ReportItem>();
            
            for (String line : (Iterable<String>) fileLines::iterator) {

                
                String[] parts = line.split(";");

                if (parts.length < 3) {
                    AppLogger.log("Linha inválida no arquivo CSV: " + line);
                    continue;
                }

                String produto = parts[0];

                ReportItem item =  report.get(produto);
                long quantidade = Long.parseLong(parts[1]);
                long valorUnitario = Long.parseLong(parts[2]);
                long total = quantidade * valorUnitario;

                if (item == null) {
                    item = new ReportItem(produto, quantidade, total);
                    report.put(produto, item);
                }else{

                    item.quantidade += quantidade;
                    item.total += total;
                }               
                
            }

            builder.append("Relatorio de Vendas:\n");

            report.forEach((k,v) -> {                          
                builder.append( String.format("Produto %s - %d unidades - R$ %d \n", k, v.quantidade, v.total) );
            });                              
            

            String text = builder.toString();            

            return text;            
            
        } catch (Exception e) {
            String error = String.format("Erro ao gerar o arquivo '%s': %s", inputFileName, e.getMessage());
    
            AppLogger.log(error);                                
        }       

        return "";       
       
    }

    @ShellMethod(key = "benchmark", value = "Medir tempo de execução")
    public String benchmark(String fileName) {
        Path outPutFile = this.outputPath.resolve(fileName) ;                          
        

        try {            


            long startTime = System.nanoTime();                        
            
            try (Stream<String> parallelStream  = Files.lines(outPutFile).parallel()) {
                
                parallelStream.forEach(line -> {});
            }
           
            long endTime = System.nanoTime();

            long durationParallel = (endTime - startTime);

            startTime = System.nanoTime();
            
            try (Stream<String> sequentialStream  = Files.lines(outPutFile)) {
                
                sequentialStream.forEach(line -> {});
            }

            endTime = System.nanoTime();
            
            long duration = (endTime - startTime);

            
            return String.format(
                                "Benchmark de Leitura \n" +
                                "----------------------------------\n" +
                                "Tempo com parallelStream: %d ms\n" +
                                "Tempo sem parallelStream: %d ms\n" +
                                "----------------------------------",
                                durationParallel / 1_000_000, duration / 1_000_000
                            );           
            
        } catch (Exception e) {
            String error = String.format("Erro ao ler o arquivo '%s': %s", fileName, e.getMessage());            
            
            AppLogger.log(error);                                
        }         

        return "";
       
    }

    
    @ShellMethod(key = "parallel-search", value = "Busca paralela de palavras em múltiplos arquivos ao mesmo tempo.")
    public String parallelSearchInDirectory(String textToBeFound, String directory) {
        Path outputDir = this.outputPath.resolve(directory);

        
        String searchUpper  = textToBeFound.toUpperCase();
        try {        
            System.out.println(outputDir.toAbsolutePath().toString())    ;
            if (!Files.isDirectory(outputDir)) {
                throw new Exception("Não é um diretório válido.");
            }
            
            return Files.list(outputDir)
                    .parallel()
                    .filter(path -> (!Files.isDirectory(path)))
                    .map(path -> this.findTextInFile(path.toAbsolutePath().toString(), searchUpper))
                    .collect(Collectors.joining(System.lineSeparator()));               

            
        } catch (Exception e) {
            String error = String.format("Erro ao ler o arquivo '%s': %s", directory, e.getMessage());
            
            AppLogger.log(error);            
            return error;
        }
    }


    @ShellMethod(key = "uppercase", value = "Conteúdo do arquivo para maiúsculo.")
    public String upperCaseFile(String inputFileName){
        String newFileName = upperOrLowerCaseFile(inputFileName, true);
        return String.format("Arquivo %s teve seu conteúdo convertido para maiúsculo salvo no arquivo %s", inputFileName, newFileName);
    }
    
    @ShellMethod(key = "lowercase", value = "Conteúdo do arquivo para minusculo.")
    public String lowerCaseFile(String inputFileName){

        String newFileName = upperOrLowerCaseFile(inputFileName, false);
        return String.format("Arquivo %s teve seu conteúdo convertido para minusculo salvo no arquivo %s", inputFileName, newFileName);
    }

    public String upperOrLowerCaseFile(String inputFileName, boolean upperCase) {

        Path inputFile = this.outputPath.resolve(inputFileName);

        Function<String, String> toUpperCaseFunction = line -> line.toUpperCase();
        Function<String, String> toLowerCaseFunction = line -> line.toLowerCase();
        Function<String, String> mapper = upperCase ? toUpperCaseFunction : toLowerCaseFunction;

        boolean hasExension = inputFileName.contains(".");
        String nameWithoutExtension = hasExension ? inputFileName.substring(0, inputFileName.lastIndexOf('.')) : inputFileName;
        String extension = hasExension ? inputFileName.substring(inputFileName.lastIndexOf('.')) : "";

        
        try(Stream<String> fileLines = Files.lines(inputFile)) {
            

            String text = fileLines.map(mapper).collect(Collectors.joining("\n"));            
            
            String newFileName = String.format("%s_converted%s", nameWithoutExtension, extension);
            
            Files.writeString(this.outputPath.resolve(newFileName), text, StandardOpenOption.CREATE);

            return newFileName;
            
        } catch (Exception e) {
            String error = String.format("Erro ao gerar o arquivo '%s': %s", inputFileName, e.getMessage());
    
            AppLogger.log(error);                                
        }       

        return "";
       
    }
}
