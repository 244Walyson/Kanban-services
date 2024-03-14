package com.waly.kanban.controllers;

import com.waly.kanban.dto.TeamDTO;
import com.waly.kanban.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/teams")
public class TeamController {

    @Autowired
    private TeamService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<TeamDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(service.findById(id));
    }
}
