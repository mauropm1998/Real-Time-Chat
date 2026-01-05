package com.chat.app.message;

import java.util.List;

import com.chat.app.message.reaction.ReactionType;

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
public class MessageStatusPayload {
    private Boolean isTyping;
    private String messageId;
    private String chatId;
    private String receiverEmail;
    private String deleteById;
    private List<String> messageIds;
    private MessageState state;
    private MessageStatus status;
    private ReactionType reactionType;
    private String userReactedId;
    private String userId;
}
