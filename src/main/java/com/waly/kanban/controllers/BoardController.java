package com.waly.kanban.controllers;

import com.waly.kanban.dto.BoardDTO;
import com.waly.kanban.dto.BoardInsertDTO;
import com.waly.kanban.dto.BoardUpdateDTO;
import com.waly.kanban.dto.CardDTO;
import com.waly.kanban.entities.Board;
import com.waly.kanban.services.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/boards")
public class BoardController {

    @Autowired
    private BoardService service;

    @GetMapping
    public ResponseEntity<Page<BoardDTO>> findAll(@RequestParam(name = "title", defaultValue = "") String title, Pageable pageable){
        return ResponseEntity.ok().body(service.findAll(title, pageable));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<BoardDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<BoardDTO> insert(@RequestBody BoardInsertDTO dto){
        BoardDTO boardDTO = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(boardDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(boardDTO);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<BoardDTO> update(@PathVariable Long id, @RequestBody BoardUpdateDTO dto){
        return ResponseEntity.ok().body(service.update(id, dto));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
