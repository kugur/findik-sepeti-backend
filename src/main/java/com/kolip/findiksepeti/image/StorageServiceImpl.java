package com.kolip.findiksepeti.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class StorageServiceImpl implements StorageService {

    private final String fileStorageLocationUrl;
    private final ResourceLoader resourceLoader;

    public StorageServiceImpl(ResourceLoader resourceLoader,
                              @Value("${file.storage.location}") String fileStorageLocationUrl) {
        this.resourceLoader = resourceLoader;
        this.fileStorageLocationUrl = fileStorageLocationUrl;
    }

    @Override
    public String storeFile(MultipartFile file) {
        // Generate a unique file name to avoid conflicts
        String fileName = StringUtils.cleanPath(file.getName());
        String uniqueFileName = createUniqueValue(fileName);

        // Get the resource representing the storage location
        Resource destination = resourceLoader.getResource(fileStorageLocationUrl);

        // Copy the file to the storage location
        try {
            Files.copy(file.getInputStream(), destination.getFile().toPath().resolve(uniqueFileName),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return null;
        }

        return uniqueFileName;
    }

    @Override
    public byte[] readFile(String url) {
        return null;
    }

    /**
     * @param originalFilename
     * @return file name that be appended with on unique value depend on date
     */
    private String createUniqueValue(String originalFilename) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());
        String uniqueFilename = timestamp + "_" + originalFilename;

        return uniqueFilename;
    }
}
