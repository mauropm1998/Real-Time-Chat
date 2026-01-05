package com.chat.app.notification;

import com.chat.app.chat.ChatResponse;
import com.chat.app.chat.members.MemberResponse;
import com.chat.app.message.MessageResponse;
import com.chat.app.message.MessageState;
import com.chat.app.message.MessageStatus;
import com.chat.app.message.MessageType;
import com.chat.app.user.UserResponse;
import com.chat.app.utils.TimeState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    private String chatId;
    private String chatName;
    private String id;
    private String content;
    private MessageType type;
    private MessageState state;
    private MessageStatus status;
    private String senderId;
    private String senderName;
    private String receiverId;
    private String senderEmail;
    private String receiverEmail;
    private String deleteById;
    private String createdAt;
    private TimeState timeState;
    private String mediaUrl;
    private String mediaExtension;
    private Long mediaSize;
    private String mediaName;
    private Boolean isNotificationGroup;
    private MessageResponse messageReplied;
    private ChatResponse chatResponse;
    private ChatAction action;
    private String memberId;
    private MemberResponse member;
}
