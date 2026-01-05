package com.chat.app.message.reaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReactMessageRequest {
    private String messageId; // ID da mensagem a ser reagida
    private ReactionType reactionType; // Tipo de reação (LIKE, LOVE, HAHA, WOW, SAD, ANGRY, CUSTOM)
    private String receiverEmail; // Email do destinatário da notificação
}
