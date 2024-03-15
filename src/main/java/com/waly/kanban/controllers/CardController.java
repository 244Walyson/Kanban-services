package com.waly.kanban.controllers;

import com.waly.kanban.dto.CardDTO;
import com.waly.kanban.dto.CardInsertDTO;
import com.waly.kanban.dto.ReplacementDTO;
import com.waly.kanban.dto.SetCollaboratorDTO;
import com.waly.kanban.services.CardService;
import jakarta.persistence.PostRemove;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/cards")
public class CardController {

    @Autowired
    private CardService service;

    @PostMapping
    public ResponseEntity<CardDTO> insert(@Valid @RequestBody CardInsertDTO dto){
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(service.insert(dto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<CardDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(service.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardDTO> update(@PathVariable Long id, @Valid @RequestBody CardInsertDTO dto){
        return ResponseEntity.ok().body(service.update(id, dto));
    }

    @PutMapping("/{id}/collaborator")
    public ResponseEntity<CardDTO> setCollaborator(@PathVariable Long id, @Valid @RequestBody SetCollaboratorDTO dto){
        return ResponseEntity.ok().body(service.updateCollaborators(id, dto));
    }

    @PostMapping(value = "/{boardId}/replacement")
    public ResponseEntity<Void> replacement(@PathVariable Long boardId, @RequestBody ReplacementDTO dto) {
        service.replacePosition(dto.getSourceIndex(), dto.getDestinationIndex(), boardId);
        return ResponseEntity.ok().build();
    }
}
