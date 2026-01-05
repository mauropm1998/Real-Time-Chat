package com.chat.app.chat;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chat.app.chat.members.MemberRequest;
import com.chat.app.message.MessageResponse;
import com.chat.app.user.User;
import com.chat.app.utils.StringResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Tag(name = "Chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<StringResponse> createChat(
            @RequestParam("senderId") String senderId,
            @RequestParam("receiverId") String receiverId) {

        final String chatId = chatService.createChat(senderId, receiverId);

        StringResponse stringResponse = new StringResponse(chatId);

        return ResponseEntity.ok(stringResponse);
    }

    @PostMapping("/group")
    public ResponseEntity<String> createGroup(
            @ModelAttribute ChatGroupRequest request,
            @AuthenticationPrincipal User user,
            @RequestParam(required = false, name = "photo") MultipartFile photo) {
        return ResponseEntity.ok(chatService.createGroup(request, user, photo));
    }

    @PostMapping("/group/update-picture")
    public ResponseEntity<MessageResponse> updatePicture(
            @ModelAttribute ChatGroupRequest request,
            @AuthenticationPrincipal User user,
            @RequestParam(required = false, name = "photo") MultipartFile photo) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(chatService.updatePicture(request, user, photo));
    }

    @PostMapping("/group/update-name")
    public ResponseEntity<MessageResponse> updateName(
            @RequestBody ChatGroupRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(chatService.updateName(request, user));
    }

    @PostMapping("/group/members/remove")
    public ResponseEntity<MessageResponse> removeGroupMember(
            @RequestBody MemberRequest request,
            @AuthenticationPrincipal User user) {
       return ResponseEntity.status(HttpStatus.ACCEPTED).body(chatService.removeGroupMember(request, user));
    }

    @PostMapping("/group/members/add")
    public ResponseEntity<MessageResponse> addGroupMember(
            @RequestBody MemberRequest request,
            @AuthenticationPrincipal User user) {
       return ResponseEntity.status(HttpStatus.ACCEPTED).body(chatService.addGroupMember(request, user));
    }

    @PostMapping("/group/update-description")
    public ResponseEntity<MessageResponse> updateDescription(
            @RequestBody ChatGroupRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(chatService.updateDescription(request, user));
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponse> getChatById(
            @PathVariable("chatId") String chatId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatService.getChatById(chatId, user));
    }

    @GetMapping
    public ResponseEntity<List<ChatResponse>> getChatsByReceiver(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatService.getChatsByReceiverId(user));
    }

}
