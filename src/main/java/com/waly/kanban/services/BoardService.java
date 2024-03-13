package com.waly.kanban.services;

import com.waly.kanban.dto.BoardDTO;
import com.waly.kanban.entities.Board;
import com.waly.kanban.repositories.BoardRepository;
import com.waly.kanban.repositories.CardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BoardService {

    @Autowired
    private BoardRepository repository;

    public Page<BoardDTO> findAll(String title, Pageable pageable) {
        Page<Board> boards = repository.findAllPageable(title, pageable);
        log.info(boards.getContent().get(0).getCards().toString());
        return boards.map(x -> new BoardDTO(x));
    }

    public BoardDTO findById(Long id) {
        return new BoardDTO(repository.findByIdWithCards(id).orElseThrow(() -> {
            throw new RuntimeException("Board não encontrado");
        }));
    }
}
