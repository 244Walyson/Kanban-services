package com.waly.kanban.controllers;

import com.waly.kanban.dto.TeamDTO;
import com.waly.kanban.dto.TeamInsertDTO;
import com.waly.kanban.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/teams")
public class TeamController {

    @Autowired
    private TeamService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<TeamDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<TeamDTO>> findById(@RequestParam(value = "query", defaultValue = "") String query, Pageable pageable){
        return ResponseEntity.ok().body(service.findAll(query, pageable));
    }

    @PostMapping
    public ResponseEntity<TeamDTO> insert(@RequestBody TeamInsertDTO dto){
        TeamDTO teamDTO = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(teamDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(teamDTO);
    }
}
