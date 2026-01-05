package com.chat.app.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMessageRequest {
    private String messageId;
    private String chatId;
    private String receiverId;
    private String receiverEmail;
    private MessageStatus status;
}
