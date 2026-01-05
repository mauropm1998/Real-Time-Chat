package com.chat.app.chat;

import java.util.List;

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
public class ChatResponse {
    private String id;
    private String name;
    private long unreadCount;
    private long messagesCount;
    private String lastMessage;
    private String lastMessageId;
    private String lastMessageTime;
    private String lastMessageSenderId;
    private String lastMessageSenderName;
    private String lastMessageDeletedById;
    private Boolean isNotificationGroup;
    private MessageState lastMessageState;
    private MessageStatus lastMessageStatus;
    private MessageType lastMessageType;
    private TimeState lastMessageTimeState;
    private TimeState lastSeenState;
    private boolean isUserOnline;
    private String lastSeen;
    private ChatType type;
    private ImageGroupType imageGroupType;

    private String description;
    private UserResponse createdBy;
    private String groupImageUrl;
    private List<MemberResponse> members;
    private String createdAt;

    private String senderId;
    private String senderEmail;
    private String receiverEmail;
    private String receiverId;

    private List<MessageResponse> messages;
}
