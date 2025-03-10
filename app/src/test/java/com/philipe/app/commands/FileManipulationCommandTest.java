package com.philipe.app.commands;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileManipulationCommandTest {

    private FileManipulationCommand command;

    @BeforeEach
    void setUp() {
        command = new FileManipulationCommand();
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.list(Path.of("output"))
            .forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }

    /**
     * Método auxiliar para aguardar a criação de um arquivo assíncrono.
     */
    private void waitForFileCreation(Path filePath) throws InterruptedException {
        int retries = 10; // Tenta por no máximo 1 segundo (10x100ms)
        while (!Files.exists(filePath) && retries > 0) {
            Thread.sleep(100);
            retries--;
        }
    }

    @Test
    @Order(1)
    void saveTextInFile_ShouldSaveSuccessfully() throws Exception {
        String fileName = "testFile.txt";
        String content = "Hello, Test!";
        command.saveTextInFile(fileName, content);

        Path path = Path.of("output/" + fileName);
        assertTrue(Files.exists(path));
        assertEquals(content + System.lineSeparator(), Files.readString(path));
    }

    @Test
    void readTextFile_ShouldReturnFileContent() {
         // 1. Preparar: Criar um arquivo temporário para testar a exclusão
         String fileName = "testFile.txt";
         String contentToWrite = "Hello, Test!";
         command.saveTextInFile(fileName, contentToWrite);

        String content = command.readTextFile(fileName);
        assertTrue(content.contains(contentToWrite));
    }
    
    @Test
    void countWords_ShouldReturnTheNumberOfWords() {
         
        String fileName = "countWords_ShouldReturnTheNumberOfWords.txt";
        String text = "This method should count the number of words in this file.";

        command.saveTextInFile(fileName, text);

        String content = command.countWords(fileName);
        assertTrue(content.contains("11 palavras"));
        assertTrue(content.matches(".*\\d+ palavras.*"));      
        
    }
    
    @Test
    void splitFile_ShouldSplitOneFileIntoMultiplesFilesWithTheNumberOfLines() throws InterruptedException {      
        
        StringBuilder builder = new StringBuilder();
        builder.append("Lines 1" + System.lineSeparator());
        builder.append("Lines 2"+ System.lineSeparator());
        builder.append("Lines 3" + System.lineSeparator());
        builder.append("Lines 4"+ System.lineSeparator());
        builder.append("End of File"+ System.lineSeparator());

        String text = builder.toString();
        String fileName = "fileToBeSplit.txt";        
        
        command.saveTextInFile(fileName, text);

        String fileOne = "fileToBeSplit_1.txt";
        String fileTwo = "fileToBeSplit_2.txt";        

        String content = command.splitFile(fileName, 2);

         // Aguarda até que os arquivos sejam realmente criados
        Path outputDir = Path.of("output");
        waitForFileCreation(outputDir.resolve(fileOne));
        waitForFileCreation(outputDir.resolve(fileTwo));
        
        assertTrue(Files.exists(outputDir.resolve(fileOne)));
        assertTrue(Files.exists(outputDir.resolve(fileTwo)));
        assertTrue(content.contains(fileOne));        
        assertTrue(content.contains(fileTwo));        
        
    }

    @Test
    void mergeFiles_ShouldMergeTheContentOfTwoFilesIntoOne() {
         
        String fileOne = "mergeFiles_ShouldMergeTheContentOfTwoFilesIntoOne_1.txt";
        String fileTwo = "mergeFiles_ShouldMergeTheContentOfTwoFilesIntoOne_2.txt";
        String text = "Two words";

        command.saveTextInFile(fileOne, "First file");
        command.saveTextInFile(fileTwo, "Second file");

        String fileName = "resultOfMerge.txt";        

        String content = command.mergeFiles(fileName,  fileOne, fileTwo);
        String contentOfCount = command.countWords(fileName);
        assertTrue(content.contains(fileName));
        assertTrue(contentOfCount.contains("4 palavras"));
                
        String contentOfMergedFile = command.readTextFile(fileName);
        assertTrue(contentOfMergedFile.contains("First"));     
        assertTrue(contentOfMergedFile.contains("Second"));     
        
    }

    @Test
    void compressFile_ShouldCompressFileWithGZIP() {        

        String text = "Content to be Compressed";
        String fileToBeCompressed = "fileToBeCompressed.txt";        
        
        command.saveTextInFile(fileToBeCompressed, text);
          
        String compressedFile = "fileToBeCompressed.gz";

        String content = command.compressFile(compressedFile, fileToBeCompressed );        
        
        assertTrue(Files.exists(Path.of("output").resolve(compressedFile)));
        assertTrue(content.contains(compressedFile));        
    }
    
    @Test
    void decompressFile_ShouldDecompressFileWithGZIP() {        

        String text = "Content to be Compressed";
        String fileToBeCompressed = "fileToBeCompressed.txt";        

        command.saveTextInFile(fileToBeCompressed, text);

        String compressedFile = "fileToBeCompressed.gz";

        command.compressFile(compressedFile, fileToBeCompressed );        
        String fileToBeDescompressed = "fileToBeDescompressed.txt";

        String content = command.decompressFile(compressedFile, fileToBeDescompressed );

        assertTrue(Files.exists(Path.of("output").resolve(fileToBeDescompressed)));
        assertTrue(content.contains(compressedFile));        
    }

    @Test
    void deleteFile_ShouldRemoveFile() {
        // 1. Preparar: Criar um arquivo temporário para testar a exclusão
        String fileName = "deleteTest.txt";
        command.saveTextInFile(fileName, "Conteúdo para deletar");

        Path filePath = Path.of("output/" + fileName);
        assertTrue(Files.exists(filePath), "O arquivo deveria existir antes da exclusão.");

        // 2. Executar: Tentar excluir o arquivo
        String result = command.deleteFile(fileName);

        // 3. Verificar: Validar que o arquivo foi excluído com sucesso
        assertTrue(result.contains("excluído"), "A mensagem de sucesso deveria ser exibida.");
        assertFalse(Files.exists(filePath), "O arquivo deveria ter sido removido.");
    }

    @Test
    void readTextFile_ShouldReturnErrorWhenFileNotFound() {
        String content = command.readTextFile("not_exist.txt");
        assertTrue(content.contains("Erro ao ler"));
    }

    @Test
    void searchText_ShouldReturnLinesWithText() {
         // 1. Preparar: Criar um arquivo temporário para testar a exclusão
         String fileName = "searchText.txt";
         String textToBeFound = "Content to be Found";
         
         StringBuilder builder = new StringBuilder();
         builder.append("No content" + System.lineSeparator());
         builder.append(textToBeFound+ System.lineSeparator());
         builder.append("End of File"+ System.lineSeparator());

         String contentToWrite = builder.toString();

         command.saveTextInFile(fileName, contentToWrite);

        String content = command.findTextInFile(fileName, "Found");
        assertTrue(content.contains("2: " + textToBeFound));
        
        content = command.findTextInFile(fileName, "Found".toLowerCase());        
        assertTrue(content.contains("2: " + textToBeFound));
        
        content = command.findTextInFile(fileName, "Found".toUpperCase());
        assertTrue(content.contains("2: " + textToBeFound));
    }


    @Test
    void sortFile_ShouldSortFileLinesInAlphabeticallyOrder() {
        
        String fileName = "testFile.txt";
        String contentToWrite = "Zebra \n Baleia \n Cobra \n Anta \n Elefante";
        command.saveTextInFile(fileName, contentToWrite);

        String expectedContent = "1:Anta2:Baleia3:Cobra4:Elefante5:Zebra";

        String content = command.sortFile(fileName);

        String contentAfterSort = command.readTextFile(fileName).trim().replaceAll("\\s+", "");

        assertTrue(content.contains(fileName));        
        assertEquals(expectedContent, contentAfterSort);
    }

    @Test
    void benchmark_ShouldCalculateHowLongItTakesToReadAFileWithParallelStream() {
        
        String fileName = "benchmark.txt";        
        String contentToWrite = IntStream.rangeClosed(1, 1200).mapToObj(number -> String.format("Linha %d \n", number) ).collect(Collectors.joining());
        
        command.saveTextInFile(fileName, contentToWrite);
        

        String content = command.benchmark(fileName);
        

        assertTrue(content.contains("Tempo com parallelStream"));        
        
    }
}


