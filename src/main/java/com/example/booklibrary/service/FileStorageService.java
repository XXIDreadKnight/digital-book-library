package com.example.booklibrary.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Stores a file on the local filesystem and returns the unique generated filename.
     */
    String storeFile(MultipartFile file);

    /**
     * Loads a file from disk as a Spring Resource for download or browser display.
     */
    Resource loadFileAsResource(String fileName);

    /**
     * Deletes a file from the filesystem.
     */
    void deleteFile(String fileName);
}
