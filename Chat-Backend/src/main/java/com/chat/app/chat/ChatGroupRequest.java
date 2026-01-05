package com.chat.app.chat;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatGroupRequest {
    private String id;
    private String name;
    private String description;
    private List<String> memberIds;
}
