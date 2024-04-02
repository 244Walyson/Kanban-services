package com.waly.kanban.controllers;

import com.waly.kanban.dto.BoardDTO;
import com.waly.kanban.dto.BoardInsertDTO;
import com.waly.kanban.dto.BoardUpdateDTO;
import com.waly.kanban.projections.BoardProjection;
import com.waly.kanban.services.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping(value = "/boards")
public class BoardController {

    @Autowired
    private BoardService service;

    @PreAuthorize("@authAdmin.isMemberOfTeam(#teamId)")
    @GetMapping(value = "/team/{teamId}")
    public ResponseEntity<Page<BoardProjection>> findAllByTeam(@PathVariable Long teamId, @RequestParam(name = "title", defaultValue = "") String title, Pageable pageable){
        return ResponseEntity.ok().body(service.findAll(title, pageable, teamId));
    }

    @GetMapping("/ok")
    public String ok() {
        log.info(String.valueOf(SecurityContextHolder.getContext().getAuthentication()));
        return "ok";
    }

    @PreAuthorize("@authAdmin.isMemberOfTeamByBoard(#id)")
    @GetMapping(value = "/{id}")
    public ResponseEntity<BoardDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PreAuthorize("@authAdmin.isAdminOfTeam(#dto.teamId)")
    @PostMapping
    public ResponseEntity<BoardDTO> insert(@RequestBody BoardInsertDTO dto){
        BoardDTO boardDTO = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(boardDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(boardDTO);
    }

    @PreAuthorize("@authAdmin.isAdminOfTeam(#id)")
    @PutMapping(value = "/{id}")
    public ResponseEntity<BoardDTO> update(@PathVariable Long id, @RequestBody BoardUpdateDTO dto){
        return ResponseEntity.ok().body(service.update(id, dto));
    }

    @PreAuthorize("@authAdmin.isAdminOfTeam(#id)")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
