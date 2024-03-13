package com.waly.kanban.repositories;

import com.waly.kanban.dto.BoardDTO;
import com.waly.kanban.entities.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("""
            SELECT obj FROM Board obj JOIN FETCH obj.cards
            """)
    Page<Board> findAllPageable(String title, Pageable pageable);

    @Query("SELECT obj FROM Board obj JOIN FETCH obj.cards WHERE obj.id = :boardId")
    Optional<Board> findByIdWithCards(@Param("boardId") Long boardId);


}
