package com.kolip.findiksepeti.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {StorageServiceImpl.class, ResourceLoader.class})
class StorageServiceTest {

    @Autowired
    private ResourceLoader resourceLoader;
    private String storageLocation = "classpath:/images/";
    private StorageService storageService;

    @BeforeEach
    public void setUp() throws IOException {
        storageService = new StorageServiceImpl(resourceLoader, storageLocation);
    }

    @Test
    public void storeFile_WithMultiPart_ReturnStoredFilePath() throws Exception {

        // Prepare test data
        String storedFileName = "testFile.txt"; // Expected stored file name
        MockMultipartFile mockFile = new MockMultipartFile(storedFileName, "This is a test file".getBytes());

        Resource destinationResource = resourceLoader.getResource(storageLocation);

        // Invoke the method under test
        String result = storageService.storeFile(mockFile);

        // Verify the result
        assertNotNull(result, "Stored file name should not be null");
        assertTrue(result.endsWith(storedFileName),
                   "Stored file name should end textFile.txt but result " + result);

        // Verify that the file is stored in the correct location
        Path storedFilePath = destinationResource.getFile().toPath().resolve(result);
        assertTrue(Files.exists(storedFilePath), "Stored file should exist");

        // Clean up (delete the stored file)
        Files.delete(storedFilePath);
    }

    @Test
    public void storeFile_WithNullValue_ReturnNull() {
        MockMultipartFile imageFile = null;

        String result = storageService.storeFile(imageFile);

        assertNull(result);
    }

    @Test
    public void readFile_WithExistFileName_ReturnStoredFile() throws Exception {
        //Prepare test
        String requestFileName = "testFile.png";

        //store file
        byte[] imageFile = "imageFile".getBytes();
        Resource destinationResource = resourceLoader.getResource(storageLocation);
        Files.write(destinationResource.getFile().toPath().resolve(requestFileName), imageFile,
                    StandardOpenOption.CREATE);

        //Run test
        byte[] requestedFile = storageService.readFile(requestFileName);

        //Verify the Result
        assertNotNull(requestedFile);
        assertArrayEquals(imageFile, requestedFile, "Returned file and stored file should be equal");


        //Clean
        Files.delete(destinationResource.getFile().toPath().resolve(requestFileName));
    }

    @Test
    public void readFile_WithNotExistFileName_ReturnNull() throws Exception {
        //Prepare Test
        String requestedFilename = "notExistFileName.png";
        String existFilename = "existFileName.png";

        byte[] existFile = "exist file data".getBytes();
        Resource destinationResource = resourceLoader.getResource(storageLocation);
        Files.write(destinationResource.getFile().toPath().resolve(existFilename), existFile,
                    StandardOpenOption.CREATE);

        //Run test
        byte[] requestedFile = storageService.readFile(requestedFilename);

        //Verify Result
        assertNull(requestedFile);

        //Clean
        Files.delete(destinationResource.getFile().toPath().resolve(existFilename));
    }

    @Test
    public void deleteFile_WithValidUrl_ShouldDeleteTheFile() throws Exception {
        //Initialize Test
        String fileName = "toBeDeletedFileName.png";
        byte[] fileData = "to be deleted file data".getBytes();
        Resource destinationResource = resourceLoader.getResource(storageLocation);
        Files.write(destinationResource.getFile().toPath().resolve(fileName), fileData, StandardOpenOption.CREATE);

        //Run Test
        boolean result = storageService.delete(fileName);

        //Verify Result
        assertTrue(result, "Should be delete the file and should return true");

        boolean fileExist = Files.exists(destinationResource.getFile().toPath().resolve(fileName));
        assertFalse(fileExist, "The file should be removed");

        //Clean
        if (fileExist) {
            Files.delete(destinationResource.getFile().toPath().resolve(fileName));
        }
    }

    @Test
    public void deleteFile_WithUrlIsNull_ShouldReturnFalse() throws Exception {
        //Initialize

        //Run Test
        boolean result = storageService.delete(null);

        //Verify Result
        assertFalse(result, "If url is null, should return null");
    }

    @Test
    public void deleteFile_WithNotExistUrl_ShouldReturnFalse() {
        //Initialize
        String fileName = "notExistFile.png";

        //Run Test
        boolean result = storageService.delete(fileName);

        //Verify Result
        assertFalse(result, "If It could not find the file, should return false");
    }
}