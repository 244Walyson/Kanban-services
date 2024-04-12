package com.kanban.chat.dtos;

import org.apache.catalina.User;

import java.util.HashSet;
import java.util.Set;

public class TeamDTO {

    private Long id;
    private String name;
    private String occupationArea;
    private String description;
    private Set<User> collaborators = new HashSet<>();
    private Integer totalCollaborators;
    private Integer totalBoards;
    private String imgUrl;
}
