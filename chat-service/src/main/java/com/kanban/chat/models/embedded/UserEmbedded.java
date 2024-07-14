package com.kanban.chat.models.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserEmbedded {

    private String id;
    private String name;
    private String nickname;
    private String imgUrl;
}

