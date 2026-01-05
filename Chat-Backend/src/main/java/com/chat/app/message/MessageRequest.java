package com.chat.app.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {

    private String content;
    private String senderId;
    private String receiverId;
    private String receiverEmail; 
    private String senderEmail;
    private MessageType type;
    private String chatId;
    private Boolean isReplying;
    private String messageRepliedId;
}
