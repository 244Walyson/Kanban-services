package com.waly.kanban.services;

import com.waly.kanban.dto.BoardDTO;
import com.waly.kanban.dto.BoardInsertDTO;
import com.waly.kanban.dto.BoardUpdateDTO;
import com.waly.kanban.entities.Board;
import com.waly.kanban.repositories.BoardRepository;
import com.waly.kanban.exceptions.NotFoundException;
import com.waly.kanban.repositories.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class BoardService {

    @Autowired
    private BoardRepository repository;
    @Autowired
    private TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public Page<BoardDTO> findAll(String title, Pageable pageable) {
        Page<Board> boards = repository.findAllPageable(title, pageable);
        log.info(boards.getContent().get(0).getCards().toString());
        return boards.map(x -> new BoardDTO(x));
    }

    @Transactional(readOnly = true)
    public BoardDTO findById(Long id) {
        return new BoardDTO(repository.findByIdWithCards(id).orElseThrow(() -> {
            throw new NotFoundException("Board não encontrado para o id: " + id);
        }));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Board não encontrado para o id: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional(readOnly = false)
    public BoardDTO insert(BoardInsertDTO dto) {
        Board board = new Board();
        copyDtoToEntity(board, dto);
        board = repository.save(board);
        return new BoardDTO(board);
    }

    @Transactional(readOnly = false)
    public BoardDTO update(Long id, BoardUpdateDTO dto) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Board não encontrado para o id: " + id);
        }
        Board board = repository.getReferenceById(id);
        board.setTitle(dto.getTitle());
        board = repository.save(board);
        return new BoardDTO(board);
    }

    private void copyDtoToEntity(Board board, BoardInsertDTO dto) {
        Long teamId = dto.getTeamId();
        if (!teamRepository.existsById(teamId)) {
            throw new NotFoundException("Team não encontrado para o id: " + teamId);
        }
        board.setTitle(dto.getTitle());
        board.setTeam(teamRepository.getReferenceById(teamId));
    }
}
