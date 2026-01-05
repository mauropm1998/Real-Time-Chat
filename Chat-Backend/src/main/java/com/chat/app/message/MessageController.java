package com.chat.app.message;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chat.app.message.reaction.ReactMessageRequest;
import com.chat.app.user.User;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Message")
public class MessageController {

    private final MessageService service;

    @PostMapping
    public ResponseEntity<String> saveMessage(@RequestBody MessageRequest messageRequest,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.saveMessage(messageRequest, user));
    }

    @PostMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteMessage (@RequestBody DeleteMessageRequest request,
            @AuthenticationPrincipal User user) {
        service.deleteMessage(request, user);
    }

    @PostMapping("/react")
    @ResponseStatus(HttpStatus.OK)
    public void reactMessage (@RequestBody ReactMessageRequest request,
            @AuthenticationPrincipal User user) {
        service.reactMessage(request, user);
    }

    @PostMapping("/upload-media/{chatId}")
    public ResponseEntity<String> saveMediaMessage(@ModelAttribute MessageRequest messageRequest,
            @PathVariable("chatId") String chatId,
            @AuthenticationPrincipal User user, @RequestParam("mediaFile") MultipartFile mediaFile) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.uploadMediaMessage(chatId, messageRequest, mediaFile, user));
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @RequestParam("chatId") String chatId) {
        return ResponseEntity.ok(service.findChatMessages(chatId));
    }
}
