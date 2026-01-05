package com.chat.app.service;

import static java.io.File.*;
import static java.lang.System.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    @Value("${application.uploads.path}")
    private String fileUploadPath;

    public String saveFile(@NotNull MultipartFile file, @NotNull String senderId) {
        final String fileUploadSubPath = "users/" + senderId;
        return uploadFile(file, fileUploadSubPath);
    }

    public String saveGroupImage(@NotNull MultipartFile file, @NotNull String chatId) {
        final String fileUploadSubPath = "chats/" + chatId;
        return uploadFile(file, fileUploadSubPath);
    }

    private String uploadFile(MultipartFile file, String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + "/" + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                log.error("Failed to create directory: {}", targetFolder.getAbsolutePath());
                return null;
            }
        }

        String fileExtension = getFileExtension(file.getOriginalFilename());
        String targetFilePath = finalUploadPath + "/" + currentTimeMillis() + "." + fileExtension;

        try {
            Files.write(Paths.get(targetFilePath), file.getBytes());
            log.info("File uploaded successfully: {}", targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("Failed to upload file: {}", e.getMessage());
            return null;
        }


    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return "";
        }
        return fileName.substring(lastIndexOfDot + 1);
    }

    

}
