package com.chat.app.message;

import java.util.List;

import com.chat.app.message.reaction.ReactionResponse;
import com.chat.app.utils.TimeState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {
    private String id;
    private String content;
    private MessageType type;
    private MessageState state;
    private MessageStatus status;
    private String senderId;
    private String senderName;
    private String receiverId;
    private String deleteById;
    private String createdAt;
    private TimeState timeState;
    private String mediaUrl;
    private String mediaExtension;
    private Long mediaSize;
    private String mediaName;
    private Boolean isNotificationGroup;

    private List<ReactionResponse> reactions; // Lista de reações à mensagem
    private MessageResponse messageReplied; // Mensagem que está sendo respondida, se houver
}
