package com.chat.app.message.reaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReactionResponse {

    private String id; // ID da reação
    private ReactionType type; // Tipo de reação (LIKE, LOVE, HAHA, WOW, SAD, ANGRY, CUSTOM)
    private String messageId; // ID da mensagem à qual a reação pertence
    private String userId; // ID do usuário que reagiu

}
