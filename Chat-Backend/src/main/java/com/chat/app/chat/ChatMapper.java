package com.chat.app.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chat.app.chat.members.MemberMapper;
import com.chat.app.message.MessageMapper;
import com.chat.app.message.reaction.ReactionMapper;
import com.chat.app.user.UserMapper;
import com.chat.app.user.UserRepository;
import com.chat.app.utils.TimeState;

@Service
public class ChatMapper {

        @Value("${application.backend.url}")
        private String serverUrl;

        public ChatResponse toChatResponse(Chat chat, String senderId, MessageMapper messageMapper,
                        ReactionMapper reactionMapper, MemberMapper memberMapper, UserMapper userMapper, UserRepository userRepository) {
                TimeState timeStateLastMessage = chat.getLastMessageTime() != null
                                ? chat.getLastMessageTime().toLocalDate().equals(LocalDateTime.now().toLocalDate())
                                                ? TimeState.TODAY
                                                : chat.getLastMessageTime().toLocalDate()
                                                                .equals(LocalDateTime.now().toLocalDate().minusDays(1))
                                                                                ? TimeState.YESTERDAY
                                                                                : TimeState.OLD
                                : TimeState.OLD;

                TimeState timeStateLastSeen = chat.getRecipient() != null && chat.getRecipient().getLastSeen() != null
                                ? chat.getRecipient().getLastSeen().toLocalDate()
                                                .equals(LocalDateTime.now().toLocalDate())
                                                                ? TimeState.TODAY
                                                                : chat.getRecipient().getLastSeen().toLocalDate()
                                                                                .equals(LocalDateTime.now()
                                                                                                .toLocalDate()
                                                                                                .minusDays(1))
                                                                                                                ? TimeState.YESTERDAY
                                                                                                                : TimeState.OLD
                                : TimeState.OLD;

                return ChatResponse.builder()
                                .id(chat.getId())
                                .name(chat.getChatName(senderId))
                                .unreadCount(chat.getUnreadMessages(senderId))
                                .messagesCount(chat.getMessages().size())
                                .lastMessage(chat.getLastMessage())
                                .lastMessageId(chat.getLastMessageId())
                                .lastSeen(chat.getRecipient() != null && chat.getRecipient().getLastSeen() != null
                                                ? chat.getRecipient().getLastSeen().format(
                                                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                                : null)
                                .lastMessageTime(chat.getLastMessageTime() != null
                                                ? chat.getLastMessageTime().format(
                                                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                                : null)
                                .lastMessageTimeState(timeStateLastMessage)
                                .lastSeenState(timeStateLastSeen)
                                .lastMessageSenderId(chat.getLastMessageSenderId())
                                .lastMessageSenderName(chat.getLastMessageSenderName())
                                .lastMessageDeletedById(chat.getLastMessageDeletedById())
                                .lastMessageState(chat.getLastMessageState())
                                .lastMessageStatus(chat.getLastMessageStatus())
                                .lastMessageType(chat.getLastMessageType())
                                .isUserOnline(chat.getRecipient() != null && chat.getRecipient().isUserOnline())
                                .senderId(chat.getSender() != null ? chat.getSender().getId() : null)
                                .receiverId(chat.getRecipient() != null ? chat.getRecipient().getId() : null)
                                .senderEmail(chat.getSender() != null ? chat.getSender().getEmail() : null)
                                .receiverEmail(chat.getRecipient() != null ? chat.getRecipient().getEmail() : null)
                                .type(chat.getType())
                                .isNotificationGroup(chat.getLastMessageNotificationGroup() != null
                                                ? chat.getLastMessageNotificationGroup()
                                                : false)
                                .imageGroupType(chat.getImageGroupType())
                                .messages(chat.getMessages().stream()
                                                .map(message -> messageMapper.toMessageResponse(message,
                                                                reactionMapper, userRepository))
                                                .toList())

                                .description(chat.getDescription())
                                .createdBy(chat.getCreatedBy() != null ? userMapper.toUserResponse(chat.getCreatedBy()) : null)
                                .createdAt(chat.getCreatedAt() != null
                                                ? chat.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                                : null)
                                .groupImageUrl(chat.getImageGroupType() == ImageGroupType.CUSTOM
                                                ? serverUrl + chat.getImageUrl()
                                                : null)
                                .members(chat.getMembers().stream()
                                                .map((member) -> memberMapper.toResponse(member, userMapper))
                                                .toList())
                                .build();
        }

}
