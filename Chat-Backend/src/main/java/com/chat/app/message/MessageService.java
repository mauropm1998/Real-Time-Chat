package com.chat.app.message;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chat.app.chat.Chat;
import com.chat.app.chat.ChatMapper;
import com.chat.app.chat.ChatRepository;
import com.chat.app.chat.ChatType;
import com.chat.app.chat.members.MemberMapper;
import com.chat.app.exceptions.EntityNotFoundException;
import com.chat.app.message.reaction.ReactMessageRequest;
import com.chat.app.message.reaction.Reaction;
import com.chat.app.message.reaction.ReactionMapper;
import com.chat.app.message.reaction.ReactionRepository;
import com.chat.app.notification.Notification;
import com.chat.app.notification.NotificationMapper;
import com.chat.app.notification.NotificationService;
import com.chat.app.service.FileService;
import com.chat.app.user.User;
import com.chat.app.user.UserMapper;
import com.chat.app.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

        private final MessageRepository messageRepository;
        private final ChatRepository chatRepository;
        private final MessageMapper messageMapper;
        private final ChatMapper chatMapper;
        private final FileService fileService;
        private final NotificationService notificationService;
        private final NotificationMapper notificationMapper;
        private final ReactionMapper reactionMapper;
        private final ReactionRepository reactionRepository;
        private final UserMapper userMapper;
        private final UserRepository userRepository;
        private final MemberMapper memberMapper;

        @Transactional
        public String saveMessage(MessageRequest request, User user) {
                Chat chat = chatRepository.findById(request.getChatId())
                                .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada"));
                Message messageReplied = null;

                if (request.getIsReplying() != null && request.getIsReplying()) {
                        messageReplied = messageRepository.findById(request.getMessageRepliedId())
                                        .orElse(null);
                }

                Message message = new Message();
                message.setChat(chat);
                message.setContent(request.getContent());
                message.setSenderId(request.getSenderId());
                message.setReceiverId(request.getReceiverId());
                message.setType(request.getType());
                message.setState(MessageState.SENT);
                message.setMessageReplied(messageReplied);
                message.setIsNotificationGroup(false);

                messageRepository.saveAndFlush(message);
                chat.getMessages().add(message);

                chatRepository.saveAndFlush(chat);

                chat = chatRepository.findById(request.getChatId())
                                .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada"));

                message.setCreatedAt(LocalDateTime.now());

                Notification notification = notificationMapper.toNotification(message, chat.getId(),
                                chat.getChatName(message.getReceiverId()), chat, chatMapper, messageMapper,
                                reactionMapper, memberMapper, userMapper, userRepository);

                if (chat.getType() == ChatType.GROUP) {
                        chat.getMembers().stream()
                                        .filter(member -> !member.getUser().getId().equals(user.getId()))
                                        .map(member -> member.getUser().getEmail())
                                        .distinct()
                                        .forEach(email -> notificationService.sendNotification(email, notification));
                } else {
                        notification.setSenderEmail(request.getSenderEmail());
                        notification.setReceiverEmail(request.getReceiverEmail());

                        notificationService.sendNotification(request.getReceiverEmail(), notification);
                }

                return message.getId();
        }

        public List<MessageResponse> findChatMessages(String chatId) {
                return messageRepository.findMessagesByChatId(chatId)
                                .stream()
                                .map(message -> messageMapper.toMessageResponse(message, reactionMapper,
                                                userRepository))
                                .toList();
        }

        @Transactional
        public String uploadMediaMessage(String chatId, MessageRequest request, MultipartFile file, User user) {
                Chat chat = chatRepository.findById(chatId)
                                .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada"));
                Message messageReplied = null;

                if (request.getIsReplying() != null && request.getIsReplying()) {
                        messageReplied = messageRepository.findById(request.getMessageRepliedId())
                                        .orElse(null);
                }

                final String filePath = fileService.saveFile(file, request.getSenderId());

                Message message = new Message();
                message.setChat(chat);
                message.setContent(request.getContent());
                message.setSenderId(request.getSenderId());
                message.setReceiverId(request.getReceiverId());
                message.setType(request.getType());
                message.setContentType(file.getContentType());
                message.setMediaExtension(file != null && !file.isEmpty()
                                ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
                                : null);
                message.setMediaSize(file.getSize());
                message.setMediaName(file.getOriginalFilename());
                message.setState(MessageState.SENT);
                message.setMediaFilePath(filePath);
                message.setMessageReplied(messageReplied);
                message.setIsNotificationGroup(false);

                messageRepository.saveAndFlush(message);

                chat.getMessages().add(message);

                chatRepository.saveAndFlush(chat);

                chat = chatRepository.findById(request.getChatId())
                                .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada"));

                message.setCreatedAt(LocalDateTime.now());

                Notification notification = notificationMapper.toNotification(message, chat.getId(),
                                chat.getChatName(message.getSenderId()), chat, chatMapper, messageMapper,
                                reactionMapper, memberMapper, userMapper, userRepository);

                if (chat.getType() == ChatType.GROUP) {
                        chat.getMembers().stream()
                                        .filter(member -> !member.getUser().getId().equals(user.getId()))
                                        .map(member -> member.getUser().getEmail())
                                        .distinct()
                                        .forEach(email -> notificationService.sendNotification(email, notification));
                } else {
                        notification.setSenderEmail(request.getSenderEmail());
                        notification.setReceiverEmail(request.getReceiverEmail());

                        notificationService.sendNotification(request.getReceiverEmail(), notification);
                }

                return message.getId();
        }

        @Transactional
        public void updateMessageToReceived(String messageId, String chatId, MessageState state) {
                int updatedCount = messageRepository.updateMessageState(messageId, chatId, state);
                if (updatedCount <= 0) {
                        throw new EntityNotFoundException("Mensagem não encontrada");
                }
        }

        @Transactional
        public void updateMessageToSeen(String chatId, List<String> messagesIds) {
                if (messagesIds != null && !messagesIds.isEmpty()) {
                        for (String messageId : messagesIds) {
                                int updatedCount = messageRepository.updateMessageState(messageId, chatId,
                                                MessageState.SEEN);
                                if (updatedCount <= 0) {
                                        throw new EntityNotFoundException("Mensagem não encontrada");
                                }
                        }
                }
        }

        @Transactional
        public void deleteMessage(DeleteMessageRequest request, User user) {
                Message message = messageRepository.findById(request.getMessageId())
                                .orElseThrow(() -> new EntityNotFoundException("Mensagem não encontrada"));

                if (message.getChat() == null || !message.getChat().getId().equals(request.getChatId())) {
                        throw new EntityNotFoundException("Mensagem não pertence à conversa especificada");
                }

                message.setDeleteById(user.getId());

                // Buscar o chat atualizado do banco de dados para garantir membros corretos
                Chat chatAtualizado = chatRepository.findById(message.getChat().getId())
                                .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada"));

                if (request.getStatus() == MessageStatus.DELETED_FOR_ALL) {
                        message.setStatus(MessageStatus.DELETED_FOR_ALL);
                        messageRepository.save(message);

                        if (chatAtualizado.getType() == ChatType.GROUP) {
                                // Usar um Set para garantir que cada membro receba apenas uma notificação
                                chatAtualizado.getMembers().stream()
                                                .filter(member -> !member.getUser().getId().equals(user.getId()))
                                                .map(member -> member.getUser().getEmail())
                                                .distinct()
                                                .forEach(email -> {
                                                        MessageStatusPayload notification = new MessageStatusPayload();
                                                        notification.setMessageId(request.getMessageId());
                                                        notification.setChatId(request.getChatId());
                                                        notification.setStatus(MessageStatus.DELETED_FOR_ALL);
                                                        notification.setDeleteById(user.getId());
                                                        notificationService.sendMessageDeleteStatus(email,
                                                                        notification);
                                                });
                        } else {
                                MessageStatusPayload notification = new MessageStatusPayload();
                                notification.setMessageId(request.getMessageId());
                                notification.setChatId(request.getChatId());
                                notification.setStatus(MessageStatus.DELETED_FOR_ALL);
                                notification.setDeleteById(user.getId());
                                notificationService.sendMessageDeleteStatus(request.getReceiverEmail(), notification);
                        }

                } else if (request.getStatus() == MessageStatus.DELETED_FOR_ME) {
                        message.setStatus(MessageStatus.DELETED_FOR_ME);
                        messageRepository.save(message);
                }
        }

        public void reactMessage(ReactMessageRequest request, User user) {
                Message message = messageRepository.findById(request.getMessageId())
                                .orElseThrow(() -> new EntityNotFoundException("Mensagem não encontrada"));

                boolean updated = false;
                if (message.getReactions() != null && !message.getReactions().isEmpty()) {
                        for (Reaction reaction : message.getReactions()) {
                                if (reaction.getUser() != null &&
                                                reaction.getUser().getId().equals(user.getId())) {
                                        if (reaction.getType() == request.getReactionType()) {
                                                // Se a reação já existe, remove-a
                                                reactionRepository.delete(reaction);
                                                message.getReactions().remove(reaction);
                                                updated = true;
                                                break;
                                        }
                                        // Se a reação já existe, atualiza-a
                                        reaction.setType(request.getReactionType());
                                        updated = true;
                                        break;
                                }
                        }
                }

                if (!updated) {
                        Reaction reaction = new Reaction();
                        reaction.setType(request.getReactionType());
                        reaction.setMessage(message);
                        reaction.setUser(user);
                        message.getReactions().add(reaction);
                }

                messageRepository.save(message);

                MessageStatusPayload notification = new MessageStatusPayload();
                notification.setMessageId(request.getMessageId());
                notification.setChatId(message.getChat().getId());
                notification.setUserReactedId(user.getId());
                notification.setReactionType(request.getReactionType());

                // Buscar o chat atualizado do banco de dados para garantir membros corretos
                Chat chatAtualizado = chatRepository.findById(message.getChat().getId())
                                .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada"));

                if (chatAtualizado.getType() == ChatType.GROUP) {
                        // Usar um Set para garantir que cada membro receba apenas uma notificação
                        chatAtualizado.getMembers().stream()
                                        .filter(member -> !member.getUser().getId().equals(user.getId()))
                                        .map(member -> member.getUser().getEmail())
                                        .distinct()
                                        .forEach(email -> notificationService.sendMessageReactionNotification(email,
                                                        notification));
                } else {
                        notificationService.sendMessageReactionNotification(
                                        request.getReceiverEmail(), notification);
                }

        }
}
