package com.kolip.findiksepeti.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * Service for input/output files.
 */
public interface StorageService {

    /**
     * @param file that will be stored.
     * @return string url if it will be stroed sucessfully else return null
     */
    String storeFile(MultipartFile file);

    /**
     * @param url of the  file to want to read
     * @return the file that be read.
     */
    byte[] readFile(String url);
}
