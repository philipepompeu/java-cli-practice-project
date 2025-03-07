package com.philipe.app.commands;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        assertTrue(content.contains(content));
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
}


