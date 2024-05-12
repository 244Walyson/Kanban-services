package com.waly.kanban.controllers;

import com.waly.kanban.dto.AddUserDTO;
import com.waly.kanban.dto.TeamDTO;
import com.waly.kanban.dto.TeamInsertDTO;
import com.waly.kanban.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<Page<TeamDTO>> findAll(@RequestParam(value = "query", defaultValue = "") String query, Pageable pageable){
        return ResponseEntity.ok().body(service.findAll(query, pageable));
    }

    @PostMapping
    public ResponseEntity<TeamDTO> insert(@RequestBody TeamInsertDTO dto){
        TeamDTO teamDTO = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(teamDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(teamDTO);
    }

    @PreAuthorize("@authAdmin.isAdminOfTeam(#id)")
    @PutMapping(value = "/{id}")
    public ResponseEntity<TeamDTO> update(@PathVariable Long id, @RequestBody TeamInsertDTO dto){
        return ResponseEntity.ok().body(service.update(id, dto));
    }

    @PreAuthorize("@authAdmin.isAdminOfTeam(#id)")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@authAdmin.isAdminOfTeam(#teamId)")
    @PostMapping(value = "add/{teamId}")
    public ResponseEntity<TeamDTO> addUserToTeam(@PathVariable Long teamId, @RequestBody AddUserDTO addUserDTO){
        return ResponseEntity.ok().body(service.addUserToTeam(teamId, addUserDTO));
    }

}
