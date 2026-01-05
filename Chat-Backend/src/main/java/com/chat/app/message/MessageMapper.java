package com.chat.app.message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chat.app.message.reaction.ReactionMapper;
import com.chat.app.user.UserRepository;
import com.chat.app.utils.TimeState;

@Service
public class MessageMapper {

    @Value("${application.backend.url}")
    private String serverUrl;

    // Cache para nomes de usuários já buscados
    private final Map<String, String> userNameCache = new ConcurrentHashMap<>();

    public MessageResponse toMessageResponse(Message message, ReactionMapper reactionMapper, UserRepository userRepository) {
        TimeState timeState = message.getCreatedAt().toLocalDate().equals(LocalDateTime.now().toLocalDate())
                ? TimeState.TODAY
                : message.getCreatedAt().toLocalDate().equals(LocalDateTime.now().toLocalDate().minusDays(1))
                        ? TimeState.YESTERDAY
                        : TimeState.OLD;

        // Busca nome do usuário usando cache
        String senderName = userNameCache.computeIfAbsent(
            message.getSenderId(),
            id -> userRepository.findById(id)
                    .map(user -> user.getFullname())
                    .orElse("Unknown User")
        );

        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId() != null ? message.getReceiverId() : null)
                .senderName(senderName)
                .deleteById(message.getDeleteById())
                .type(message.getType())
                .state(message.getState())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .timeState(timeState)
                .mediaUrl(message.getType() != MessageType.TEXT ? serverUrl + message.getMediaUrl() : null)
                .mediaExtension(message.getMediaExtension())
                .mediaSize(message.getMediaSize())
                .mediaName(message.getMediaName())
                .isNotificationGroup(message.getIsNotificationGroup() != null ? message.getIsNotificationGroup() : false)
                .messageReplied(message.getMessageReplied() != null
                        ? toMessageResponse(message.getMessageReplied(), reactionMapper, userRepository)
                        : null)
                .reactions(message.getReactions() != null ? message.getReactions().stream()
                        .map(reactionMapper::toReactionResponse)
                        .toList() : null)
                .build();
    }
}
