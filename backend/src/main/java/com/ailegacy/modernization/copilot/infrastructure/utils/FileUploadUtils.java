package com.ailegacy.modernization.copilot.infrastructure.utils;

import com.ailegacy.modernization.copilot.domain.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Utility for handling file uploads and storage operations.
 * 
 * Responsibilities:
 * - Validate file size and type
 * - Store uploaded files
 * - Manage file paths
 */
@Slf4j
@Component
public class FileUploadUtils {

    private static final long MAX_FILE_SIZE_BYTES = 1000 * 1024 * 1024; // 1GB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jar", "war", "ear", "xml", "properties", "gradle", "maven", "java", 
            "class", "jsp", "jspx", "html", "conf", "sql", "yml", "yaml", "json"
    );

    /**
     * Validate uploaded file
     */
    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File is required");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ValidationException("File size exceeds maximum allowed size of 1GB");
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new ValidationException("Invalid filename");
        }

        String extension = getFileExtension(filename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new ValidationException(
                    "File type '" + extension + "' is not allowed. Allowed types: " + ALLOWED_EXTENSIONS
            );
        }
    }

    /**
     * Store uploaded file
     */
    public String storeFile(MultipartFile file, String uploadDir) {
        validateFile(file);

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath);
            }

            String filename = System.nanoTime() + "-" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            
            Files.write(filePath, file.getBytes());
            log.info("File uploaded successfully: {} -> {}", file.getOriginalFilename(), filePath);
            
            return filePath.toString();
        } catch (IOException ex) {
            log.error("Failed to store file: {}", file.getOriginalFilename(), ex);
            throw new ValidationException("Failed to store file: " + ex.getMessage(), ex);
        }
    }

    /**
     * Delete file
     */
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("File deleted: {}", filePath);
            }
        } catch (IOException ex) {
            log.error("Failed to delete file: {}", filePath, ex);
        }
    }

    /**
     * Get file extension
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot + 1);
        }
        return "";
    }

}
