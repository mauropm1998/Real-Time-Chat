package com.chat.app.chat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.chat.app.chat.members.Member;
import com.chat.app.chat.members.MemberMapper;
import com.chat.app.chat.members.MemberRole;
import com.chat.app.chat.members.MemberRequest;
import com.chat.app.chat.members.MemberResponse;
import com.chat.app.exceptions.EntityNotFoundException;
import com.chat.app.exceptions.OperationNotPermittedException;
import com.chat.app.message.Message;
import com.chat.app.message.MessageMapper;
import com.chat.app.message.MessageRepository;
import com.chat.app.message.MessageResponse;
import com.chat.app.message.MessageState;
import com.chat.app.message.MessageStatusPayload;
import com.chat.app.message.MessageType;
import com.chat.app.message.reaction.ReactionMapper;
import com.chat.app.notification.ChatAction;
import com.chat.app.notification.Notification;
import com.chat.app.notification.NotificationMapper;
import com.chat.app.notification.NotificationService;
import com.chat.app.service.FileService;
import com.chat.app.user.User;
import com.chat.app.user.UserMapper;
import com.chat.app.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

        private final ChatRepository chatRepository;
        private final ChatMapper chatMapper;
        private final UserRepository userRepository;
        private final MessageRepository messageRepository;
        private final MessageMapper messageMapper;
        private final NotificationService service;
        private final ReactionMapper reactionMapper;
        private final FileService fileService;
        private final UserMapper userMapper;
        private final NotificationMapper notificationMapper;
        private final NotificationService notificationService;
        private final MemberMapper memberMapper;

        @Transactional(readOnly = true)
        public List<ChatResponse> getChatsByReceiverId(User user) {

                List<Chat> chats = chatRepository.findChatsBySenderId(user.getId());

                for (Chat chat : chats) {
                        List<Message> last10Messages = messageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId());
                        chat.setMessages(last10Messages);
                        List<Message> messages = chat.getMessages().stream()
                                        .filter(message -> message.getState() == MessageState.SENT
                                                        && (message.getReceiverId() != null && message.getReceiverId()
                                                                        .equals(user.getId())))
                                        .toList();

                        for (Message message : messages) {
                                message.setState(MessageState.RECEIVED);
                                messageRepository.save(message);

                                MessageStatusPayload payload = new MessageStatusPayload();
                                payload.setMessageId(message.getId());
                                payload.setState(MessageState.RECEIVED);
                                payload.setChatId(chat.getId());
                                payload.setReceiverEmail(chat.getRecipient().getEmail().equals(user.getEmail())
                                                ? chat.getSender().getEmail()
                                                : chat.getRecipient().getEmail());
                                service.sendMessageStatus(payload.getReceiverEmail(), payload);

                        }
                }

                return chats.stream()
                                .filter(chat -> chat.getMessages().size() > 0)
                                .map(chat -> chatMapper.toChatResponse(chat, user.getId(), messageMapper,
                                                reactionMapper, memberMapper,
                                                userMapper, userRepository))
                                .toList();
        }

        public String createChat(String senderId, String receiverId) {
                Optional<Chat> existingChat = chatRepository.findChatByReceiverAndSender(senderId, receiverId);

                if (existingChat.isPresent()) {
                        return existingChat.get().getId();
                }

                User sender = userRepository.findById(senderId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "User " + senderId + " not found in the database"));

                User receiver = userRepository.findById(receiverId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "User " + receiverId + " not found in the database"));

                Chat chat = new Chat();
                chat.setType(ChatType.PRIVATE);
                chat.setSender(sender);
                chat.setRecipient(receiver);

                Chat savedChat = chatRepository.save(chat);

                return savedChat.getId();
        }

        public ChatResponse getChatById(String chatId, User user) {
                Chat chat = chatRepository.findById(chatId)
                                .orElseThrow(() -> new EntityNotFoundException("Chat não encontado: " + chatId));

                List<Message> messages = messageRepository.findByChatIdOrderByCreatedAtAsc(chatId);
                chat.setMessages(messages);

                return chatMapper.toChatResponse(chat, user.getId(), messageMapper, reactionMapper, memberMapper,
                                userMapper,
                                userRepository);
        }

        @Transactional
        public String createGroup(ChatGroupRequest request, User user, MultipartFile photo) {
                if (request.getMemberIds() == null || request.getMemberIds().isEmpty()) {
                        throw new OperationNotPermittedException("A lista de membros não pode estar vazia.");
                }

                Chat chat = new Chat();
                chat.setMessages(new ArrayList<>());
                chat.setType(ChatType.GROUP);
                chat.setName(request.getName());
                chat.setDescription(request.getDescription());
                chat.setCreatedBy(user);
                chat.setMembers(request.getMemberIds().stream()
                                .map(memberId -> Member.builder().role(MemberRole.MEMBER).createdAt(LocalDateTime.now())
                                                .user(userRepository.findById(memberId)
                                                                .orElseThrow(
                                                                                () -> new EntityNotFoundException(
                                                                                                "Colaborador não encontrado: "
                                                                                                                + memberId)))
                                                .build())
                                .collect(Collectors.toList()));
                chat.getMembers().add(Member.builder().createdAt(LocalDateTime.now()).role(MemberRole.ADMIN).user(user)
                                .build()); // Adiciona o criador do grupo como membro

                chatRepository.save(chat);

                if (photo != null && !photo.isEmpty()) {
                        chat.setImageGroupType(ImageGroupType.CUSTOM);
                        chat.setGroupImagePath(fileService.saveGroupImage(photo, chat.getId()));
                        chat.setContentType(photo.getContentType());
                        chat.setMediaExtension(photo != null && !photo.isEmpty()
                                        ? photo.getOriginalFilename()
                                                        .substring(photo.getOriginalFilename().lastIndexOf(".") + 1)
                                        : null);
                } else {
                        chat.setImageGroupType(ImageGroupType.DEFAULT);
                }

                chatRepository.save(chat);

                Message message = new Message();
                message.setChat(chat);
                message.setSenderId(user.getId());
                message.setContent(user.getFullname() + " criou este grupo.");
                message.setState(MessageState.SEEN);
                message.setIsNotificationGroup(true);
                message.setCreatedAt(LocalDateTime.now());
                message.setReactions(new ArrayList<>());
                message.setType(MessageType.TEXT);

                Message message2 = new Message();
                message2.setChat(chat);
                message2.setSenderId(user.getId());
                message2.setContent("Grupo \"" + request.getName() + "\" foi criado.");
                message2.setState(MessageState.SEEN);
                message2.setIsNotificationGroup(true);
                message2.setCreatedAt(LocalDateTime.now());
                message2.setReactions(new ArrayList<>());
                message2.setType(MessageType.TEXT);

                messageRepository.save(message);
                messageRepository.save(message2);

                chat.getMessages().add(message);
                chat.getMessages().add(message2);

                chatRepository.save(chat);

                chat = chatRepository.findById(chat.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada"));

                Notification notification = notificationMapper.toNotification(chat.getId(),
                                chat.getChatName(message.getReceiverId()), chat, chatMapper, messageMapper,
                                reactionMapper, memberMapper, userMapper, userRepository);
                notification.setSenderEmail(user.getEmail());

                chat.getMembers().stream()
                                .filter(member -> !member.getUser().getId().equals(user.getId()))
                                .map(member -> member.getUser().getEmail())
                                .distinct()
                                .forEach(email -> {
                                        notificationService.sendNotification(email,
                                                        notification);
                                });

                return chat.getId();
        }

        @Transactional
        public MessageResponse updatePicture(ChatGroupRequest request, User user, MultipartFile photo) {
                var chat = chatRepository.findById(request.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

                if (photo != null && !photo.isEmpty()) {
                        chat.setImageGroupType(ImageGroupType.CUSTOM);
                        chat.setGroupImagePath(fileService.saveGroupImage(photo, chat.getId()));
                        chat.setContentType(photo.getContentType());
                        chat.setMediaExtension(photo != null && !photo.isEmpty()
                                        ? photo.getOriginalFilename()
                                                        .substring(photo.getOriginalFilename().lastIndexOf(".") + 1)
                                        : null);
                }

                chatRepository.save(chat);

                Message message = new Message();
                message.setChat(chat);
                message.setSenderId(user.getId());
                message.setContent(user.getFullname() + " alterou a imagem do grupo.");
                message.setState(MessageState.SEEN);
                message.setIsNotificationGroup(true);
                message.setCreatedAt(LocalDateTime.now());
                message.setReactions(new ArrayList<>());
                message.setType(MessageType.TEXT);

                messageRepository.save(message);

                chat.getMessages().add(message);

                chatRepository.save(chat);

                chat = chatRepository.findById(chat.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada"));

                Notification notification = notificationMapper.toNotification(message, chat.getId(),
                                chat.getChatName(message.getReceiverId()), chat, chatMapper, messageMapper,
                                reactionMapper, memberMapper, userMapper, userRepository);
                notification.setSenderEmail(user.getEmail());

                chat.getMembers().stream()
                                .filter(member -> !member.getUser().getId().equals(user.getId()))
                                .map(member -> member.getUser().getEmail())
                                .distinct()
                                .forEach(email -> {
                                        notificationService.sendNotificationGroup(email,
                                                        notification);
                                });

                return messageMapper.toMessageResponse(message, reactionMapper, userRepository);
        }

        @Transactional
        public MessageResponse updateName(ChatGroupRequest request, User user) {
                var chat = chatRepository.findById(request.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

                chat.setName(request.getName());

                chatRepository.save(chat);

                Message message = new Message();
                message.setChat(chat);
                message.setSenderId(user.getId());
                message.setContent(user.getFullname() + " alterou o nome do grupo.");
                message.setState(MessageState.SEEN);
                message.setIsNotificationGroup(true);
                message.setCreatedAt(LocalDateTime.now());
                message.setReactions(new ArrayList<>());
                message.setType(MessageType.TEXT);

                messageRepository.save(message);

                chat.getMessages().add(message);

                chatRepository.save(chat);

                chat = chatRepository.findById(chat.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada"));

                Notification notification = notificationMapper.toNotification(message, chat.getId(),
                                chat.getChatName(message.getReceiverId()), chat, chatMapper, messageMapper,
                                reactionMapper, memberMapper, userMapper, userRepository);
                notification.setSenderEmail(user.getEmail());

                chat.getMembers().stream()
                                .filter(member -> !member.getUser().getId().equals(user.getId()))
                                .map(member -> member.getUser().getEmail())
                                .distinct()
                                .forEach(email -> {
                                        notificationService.sendNotificationGroup(email,
                                                        notification);
                                });

                return messageMapper.toMessageResponse(message, reactionMapper, userRepository);
        }

        @Transactional
        public MessageResponse updateDescription(ChatGroupRequest request, User user) {
                var chat = chatRepository.findById(request.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

                chat.setDescription(request.getDescription());

                chatRepository.save(chat);

                Message message = new Message();
                message.setChat(chat);
                message.setSenderId(user.getId());
                message.setContent(user.getFullname() + " alterou a descrição do grupo.");
                message.setState(MessageState.SEEN);
                message.setIsNotificationGroup(true);
                message.setCreatedAt(LocalDateTime.now());
                message.setReactions(new ArrayList<>());
                message.setType(MessageType.TEXT);

                messageRepository.save(message);

                chat.getMessages().add(message);

                chatRepository.save(chat);

                chat = chatRepository.findById(chat.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada"));

                Notification notification = notificationMapper.toNotification(message, chat.getId(),
                                chat.getChatName(message.getReceiverId()), chat, chatMapper, messageMapper,
                                reactionMapper, memberMapper, userMapper, userRepository);
                notification.setSenderEmail(user.getEmail());

                chat.getMembers().stream()
                                .filter(member -> !member.getUser().getId().equals(user.getId()))
                                .map(member -> member.getUser().getEmail())
                                .distinct()
                                .forEach(email -> {
                                        notificationService.sendNotificationGroup(email,
                                                        notification);
                                });

                return messageMapper.toMessageResponse(message, reactionMapper, userRepository);
        }

        @Transactional
        public MessageResponse removeGroupMember(MemberRequest request, User user) {
                var chat = chatRepository.findById(request.getChatId())
                                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
                var userToRemove = userRepository.findById(request.getUserId())
                                .orElseThrow(() -> new EntityNotFoundException("Colaborador não encontrado"));

                if (chat.getMembers().stream().filter(member -> member.getUser().getId().equals(request.getUserId()))
                                .count() == 0) {
                        throw new OperationNotPermittedException("Esse colaborador não é membro desse grupo");
                }

                chat.getMembers().removeIf(member -> member.getUser().getId().equals(request.getUserId()));

                chatRepository.save(chat);

                Message message = new Message();
                message.setChat(chat);
                message.setSenderId(user.getId());
                message.setContent(userToRemove.getFullname() + " foi removido do grupo.");
                message.setState(MessageState.SEEN);
                message.setIsNotificationGroup(true);
                message.setCreatedAt(LocalDateTime.now());
                message.setReactions(new ArrayList<>());
                message.setType(MessageType.TEXT);

                messageRepository.save(message);

                chat.getMessages().add(message);

                chatRepository.save(chat);

                chat = chatRepository.findById(chat.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada"));

                Notification notification = notificationMapper.toNotification(message, chat.getId(),
                                chat.getChatName(message.getReceiverId()), chat, chatMapper, messageMapper,
                                reactionMapper, memberMapper, userMapper, userRepository);
                notification.setSenderEmail(user.getEmail());
                notification.setAction(ChatAction.REM);
                notification.setMemberId(request.getUserId());

                chat.getMembers().stream()
                                .filter(member -> !member.getUser().getId().equals(user.getId()))
                                .map(member -> member.getUser().getEmail())
                                .distinct()
                                .forEach(email -> {
                                        notificationService.sendNotificationGroupStatus(email,
                                                        notification);
                                });

                notificationService.sendNotificationGroupStatus(userToRemove.getEmail(),
                                notification);

                return messageMapper.toMessageResponse(message, reactionMapper, userRepository);
        }

        @Transactional
        public MessageResponse addGroupMember(MemberRequest request, User user) {
                var chat = chatRepository.findById(request.getChatId())
                                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
                var userToAdd = userRepository.findById(request.getUserId())
                                .orElseThrow(() -> new EntityNotFoundException("Colaborador não encontrado"));

                if (chat.getMembers().stream().filter(member -> member.getUser().getId().equals(request.getUserId()))
                                .count() > 0) {
                        throw new OperationNotPermittedException("Esse colaborador já é membro desse grupo");
                }

                chat.getMembers().add(Member.builder().role(MemberRole.MEMBER).createdAt(LocalDateTime.now())
                                .user(userToAdd).build());

                chatRepository.save(chat);

                Message message = new Message();
                message.setChat(chat);
                message.setSenderId(user.getId());
                message.setContent(userToAdd.getFullname() + " foi adicionado ao grupo.");
                message.setState(MessageState.SEEN);
                message.setIsNotificationGroup(true);
                message.setCreatedAt(LocalDateTime.now());
                message.setReactions(new ArrayList<>());
                message.setType(MessageType.TEXT);

                messageRepository.save(message);

                chat.getMessages().add(message);

                chatRepository.save(chat);

                chat = chatRepository.findById(chat.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada"));

                Notification notification = notificationMapper.toNotification(message, chat.getId(),
                                chat.getChatName(message.getReceiverId()), chat, chatMapper, messageMapper,
                                reactionMapper, memberMapper, userMapper, userRepository);
                notification.setSenderEmail(user.getEmail());
                notification.setAction(ChatAction.ADD);
                notification.setMemberId(request.getUserId());
                notification.setMember(MemberResponse.builder().role(MemberRole.MEMBER)
                                .enterDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                .user(userMapper.toUserResponse(userToAdd)).build());

                chat.getMembers().stream()
                                .filter(member -> !member.getUser().getId().equals(user.getId()))
                                .map(member -> member.getUser().getEmail())
                                .distinct()
                                .forEach(email -> {
                                        notificationService.sendNotificationGroupStatus(email,
                                                        notification);
                                });

                notificationService.sendNotificationGroup(userToAdd.getEmail(),
                                notification);

                return messageMapper.toMessageResponse(message, reactionMapper, userRepository);
        }

}
