package com.chat.app.ws;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.chat.app.chat.ChatRepository;
import com.chat.app.chat.ChatType;
import com.chat.app.exceptions.EntityNotFoundException;
import com.chat.app.message.MessageService;
import com.chat.app.message.MessageState;
import com.chat.app.message.MessageStatusPayload;
import com.chat.app.notification.NotificationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WSController {

    private final NotificationService service;
    private final MessageService messageService;
    private final ChatRepository chatRepository;

    // Método para receber notificações de leitura/recebimento de mensagens via
    // WebSocket
    @MessageMapping("/message/received")
    public void handleMessageReceived(@Payload MessageStatusPayload payload) {
        messageService.updateMessageToReceived(payload.getMessageId(), payload.getChatId(), payload.getState());

        var chat = chatRepository.findById(payload.getChatId())
                .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada"));
        if (chat.getType() == ChatType.GROUP) {
            // Envia o status de recebido para todos os membros do grupo, exceto o usuário
            // que enviou
            // a mensagem
            chat.getMembers().stream()
                    .filter(member -> !member.getUser().getId().equals(payload.getUserId()))
                    .map(member -> member.getUser().getEmail())
                    .distinct()
                    .forEach(email -> service.sendMessageStatus(email, payload));
        } else {
            service.sendMessageStatus(payload.getReceiverEmail(), payload);
        }
    }

    @MessageMapping("/message/typing")
    public void handleMessageTyping(@Payload MessageStatusPayload payload) {
        var chat = chatRepository.findById(payload.getChatId())
                .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada"));
        if (chat.getType() == ChatType.GROUP) {
            // Envia o status de digitação para todos os membros do grupo, exceto o usuário
            // que está digitando
            // a mensagem
            chat.getMembers().stream()
                    .filter(member -> !member.getUser().getId().equals(payload.getUserId()))
                    .map(member -> member.getUser().getEmail())
                    .distinct()
                    .forEach(email -> service.sendMessageTyping(email, payload));
        } else {
            service.sendMessageTyping(payload.getReceiverEmail(), payload);
        }
    }

    @MessageMapping("/message/seen")
    public void handleMessageSeen(@Payload MessageStatusPayload payload) {
        messageService.updateMessageToSeen(payload.getChatId(), payload.getMessageIds());

        var chat = chatRepository.findById(payload.getChatId())
                .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada"));
        if (chat.getType() == ChatType.GROUP) {
            // Envia o status de visto para todos os membros do grupo, exceto o usuário que
            // enviou
            // a mensagem
            chat.getMembers().stream()
                    .filter(member -> !member.getUser().getId().equals(payload.getUserId()))
                    .map(member -> member.getUser().getEmail())
                    .distinct()
                    .forEach(email -> {
                        for (String messageId : payload.getMessageIds()) {
                            payload.setMessageId(messageId);
                            payload.setState(MessageState.SEEN);
                            service.sendMessageStatus(email, payload);
                        }
                    });
        } else {
            for (String messageId : payload.getMessageIds()) {
                payload.setMessageId(messageId);
                payload.setState(MessageState.SEEN);
                service.sendMessageStatus(payload.getReceiverEmail(), payload);
            }
        }
    }

}