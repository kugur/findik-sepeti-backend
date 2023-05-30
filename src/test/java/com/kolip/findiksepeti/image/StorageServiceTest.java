package com.kolip.findiksepeti.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {StorageServiceImpl.class, ResourceLoader.class})
class StorageServiceTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private StorageService storageService;

    @BeforeEach
    public void setUp() {
        storageService = new StorageServiceImpl(resourceLoader, "classpath:images/");
    }

    @Test
    public void storeFile_WithMultiPart_ReturnStoredFilePath() throws Exception {

        // Prepare test data
        String storedFileName = "testFile.txt"; // Expected stored file name
        MockMultipartFile mockFile = new MockMultipartFile(storedFileName, "This is a test file".getBytes());


        Resource destinationResource = resourceLoader.getResource("classpath:images/");

        // Invoke the method under test
        String result = storageService.storeFile(mockFile);

        // Verify the result
        assertNotNull(result, "Stored file name should not be null");
        assertTrue(result.endsWith(storedFileName), "Stored file name should end textFile.txt but result " + storedFileName);

        // Verify that the file is stored in the correct location
        Path storedFilePath = destinationResource.getFile().toPath().resolve(result);
        assertTrue(Files.exists(storedFilePath), "Stored file should exist");

        // Clean up (delete the stored file)
        Files.delete(storedFilePath);
    }
}