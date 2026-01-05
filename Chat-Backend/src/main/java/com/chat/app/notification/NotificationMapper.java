package com.chat.app.notification;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chat.app.chat.Chat;
import com.chat.app.chat.ChatMapper;
import com.chat.app.chat.members.MemberMapper;
import com.chat.app.message.Message;
import com.chat.app.message.MessageMapper;
import com.chat.app.message.MessageType;
import com.chat.app.message.reaction.ReactionMapper;
import com.chat.app.user.UserMapper;
import com.chat.app.user.UserRepository;
import com.chat.app.utils.TimeState;

@Service
public class NotificationMapper {

        @Value("${application.backend.url}")
        private String serverUrl;

        // Cache para nomes de usuários já buscados
        private final Map<String, String> userNameCache = new ConcurrentHashMap<>();

        public Notification toNotification(Message message, String chatId, String chatName, Chat chat,
                        ChatMapper chatMapper, MessageMapper messageMapper, ReactionMapper reactionMapper,
                        MemberMapper memberMapper, UserMapper userMapper, UserRepository userRepository) {
                TimeState timeState = message.getCreatedAt().toLocalDate().equals(LocalDateTime.now().toLocalDate())
                                ? TimeState.TODAY
                                : message.getCreatedAt().toLocalDate()
                                                .equals(LocalDateTime.now().toLocalDate().minusDays(1))
                                                                ? TimeState.YESTERDAY
                                                                : TimeState.OLD;

                // Busca nome do usuário usando cache
                String senderName = userNameCache.computeIfAbsent(
                                message.getSenderId(),
                                id -> userRepository.findById(id)
                                                .map(user -> user.getFullname())
                                                .orElse("Unknown User"));

                return Notification.builder()
                                .chatId(chatId)
                                .chatName(chatName)
                                .id(message.getId())
                                .content(message.getContent())
                                .type(message.getType())
                                .state(message.getState())
                                .status(message.getStatus())
                                .senderId(message.getSenderId())
                                .senderName(senderName)
                                .isNotificationGroup(message.getIsNotificationGroup() != null
                                                ? message.getIsNotificationGroup()
                                                : false)
                                .receiverId(message.getReceiverId() != null ? message.getReceiverId() : null)
                                .deleteById(message.getDeleteById())
                                .createdAt(message.getCreatedAt().toString())
                                .timeState(timeState)
                                .mediaUrl(message.getType() != MessageType.TEXT ? serverUrl + message.getMediaUrl()
                                                : null)
                                .mediaExtension(message.getMediaExtension())
                                .mediaSize(message.getMediaSize())
                                .mediaName(message.getMediaName())
                                .messageReplied(message.getMessageReplied() != null
                                                ? messageMapper.toMessageResponse(message.getMessageReplied(),
                                                                reactionMapper, userRepository)
                                                : null)
                                .chatResponse(chatMapper.toChatResponse(chat, message.getReceiverId(), messageMapper,
                                                reactionMapper, memberMapper, userMapper, userRepository))
                                .build();
        }

        public Notification toNotification(String chatId, String chatName, Chat chat,
                        ChatMapper chatMapper, MessageMapper messageMapper, ReactionMapper reactionMapper,
                        MemberMapper memberMapper,
                        UserMapper userMapper, UserRepository userRepository) {

                return Notification.builder()
                                .chatId(chatId)
                                .chatName(chatName)
                                .chatResponse(chatMapper.toChatResponse(chat, chat.getCreatedBy().getId(),
                                                messageMapper, reactionMapper, memberMapper, userMapper,
                                                userRepository))
                                .build();
        }

}
