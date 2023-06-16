package com.kolip.findiksepeti.image;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping(value = "/images/{path}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody ResponseEntity<byte[]> getImageWithMediaType(@PathVariable(name = "path") String path) {
        byte[] imageFile = storageService.readFile(path);
        if (imageFile == null) {
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).build();
        } else {
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(imageFile);
        }
    }
}
