package com.chat.app.message.reaction;

import org.springframework.stereotype.Service;

@Service
public class ReactionMapper {

    public ReactionResponse toReactionResponse(Reaction reaction) {
        return ReactionResponse.builder()
                .id(reaction.getId())
                .type(reaction.getType())
                .messageId(reaction.getMessage().getId())
                .userId(reaction.getUser() != null ? reaction.getUser().getId() : null)
                .build();
    }

}
