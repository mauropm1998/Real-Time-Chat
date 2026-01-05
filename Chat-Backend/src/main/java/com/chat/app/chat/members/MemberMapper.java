package com.chat.app.chat.members;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.chat.app.user.UserMapper;

@Service
public class MemberMapper {

    public MemberResponse toResponse(Member member, UserMapper mapper) {
        return MemberResponse.builder()
        .role(member.getRole())
        .enterDate(member.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        .user(mapper.toUserResponse(member.getUser()))
        .build();
    }

}
