package com.kolip.findiksepeti.image;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class ImageController {
    private StorageService storageService;

    public ImageController(StorageService storageService) {
        this.storageService = storageService;
    }

    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        return null;
    }

    @PostMapping("/imageUpload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile imageFile) {
        String imageUrl = storageService.storeFile(imageFile);

        if (imageUrl == null) {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        } else {
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(imageUrl);
        }
    }

    @GetMapping(
            value = "/get-image-with-media-type",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public @ResponseBody
    byte[] getImageWithMediaType(@PathVariable String path) throws IOException {
        return null;
    }


}
