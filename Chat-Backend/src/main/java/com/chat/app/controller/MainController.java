package com.chat.app.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chat.app.utils.FileUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainController {

    /*@GetMapping("/")
    public String root() {
        return "redirect:/login";
    }*/

    @RequestMapping(value = { "/", "/chat/**" })
    public String redirect() {
        return "forward:/index.html";
    }

    @GetMapping("/public/media/videos")
    public ResponseEntity<byte[]> getVideo(@RequestParam("video") String video,
            @RequestParam("contentType") String contentType) {
        byte[] videoBytes = FileUtils.readFileFromLocation(video);

        return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .body(videoBytes);

    }

    @GetMapping("/public/media/images")
    public ResponseEntity<byte[]> getImage(@RequestParam("image") String image,
            @RequestParam("contentType") String contentType) {
        byte[] imageBytes = FileUtils.readFileFromLocation(image);

        return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .body(imageBytes);

    }

    @GetMapping("/public/media/files")
    public ResponseEntity<byte[]> getFile(@RequestParam("file") String file,
            @RequestParam("contentType") String contentType) {
        byte[] fileBytes = FileUtils.readFileFromLocation(file);

        return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .body(fileBytes);

    }

    @MessageMapping("/test")
    public void testUser(Principal principal) {
        log.info("Usuário conectado via WS: {}", principal.getName());
    }

}
