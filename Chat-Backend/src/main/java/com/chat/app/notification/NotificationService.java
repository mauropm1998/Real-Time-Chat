package com.chat.app.notification;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.chat.app.message.MessageStatusPayload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String username, Notification notification) {
        messagingTemplate.convertAndSendToUser(username, "/chat", notification);
    }

    public void sendNotificationGroupStatus(String username, Notification notification) {
        messagingTemplate.convertAndSendToUser(username, "/group/status", notification);
    }

    public void sendNotificationGroup(String username, Notification notification) {
        messagingTemplate.convertAndSendToUser(username, "/group", notification);
    }

    public void sendMessageStatus (String username, MessageStatusPayload messageStatusPayload) {
        messagingTemplate.convertAndSendToUser(username, "/message/state", messageStatusPayload);
    }

    public void sendMessageDeleteStatus (String username, MessageStatusPayload messageStatusPayload) {
        messagingTemplate.convertAndSendToUser(username, "/message/status", messageStatusPayload);
    }

    public void sendMessageTyping (String username, MessageStatusPayload messageStatusPayload) {
        messagingTemplate.convertAndSendToUser(username, "/message/typing", messageStatusPayload);
    }

    public void sendMessageReactionNotification(String receiverEmail, MessageStatusPayload messageStatusPayload) {
        messagingTemplate.convertAndSendToUser(receiverEmail, "/message/reaction", messageStatusPayload);
    }
}
