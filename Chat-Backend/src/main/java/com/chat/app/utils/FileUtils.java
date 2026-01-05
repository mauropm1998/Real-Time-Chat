package com.chat.app.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

    private FileUtils() {
        // Prevent instantiation
    }

    public static byte[] readFileFromLocation (String filePath) {
        if(StringUtils.isBlank(filePath)) {
            return new byte[0];
        }

        try {
            Path path = Paths.get(filePath);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("Failed to read file from location: {}", filePath, e);
        }
        
        return new byte[0];
    }

}
