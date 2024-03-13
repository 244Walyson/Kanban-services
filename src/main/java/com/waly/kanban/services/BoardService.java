package com.waly.kanban.services;

import com.waly.kanban.dto.BoardDTO;
import com.waly.kanban.entities.Board;
import com.waly.kanban.repositories.BoardRepository;
import com.waly.kanban.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
            throw new NotFoundException("Board não encontrado para o id: " + id);
        }));
    }

    public void delete(Long id){
        if(!repository.existsById(id)){
            throw new NotFoundException("Board não encontrado para o id: " + id);
        }
        try {
            repository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public BoardDTO insert(BoardDTO dto){
        Board board = new Board();
        copyDtoToEntity(board, dto);
        board = repository.save(board);
        return new BoardDTO(board);
    }

    public BoardDTO update(Long id, BoardDTO dto){
        Board board = repository.getReferenceById(id);
        copyDtoToEntity(board, dto);
        board = repository.save(board);
        return new BoardDTO(board);
    }

    private void copyDtoToEntity(Board board, BoardDTO dto) {
        board.setTotalCards(0);
        board.setTitle(dto.getTitle());
    }
}
