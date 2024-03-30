package com.waly.kanban.repositories;

import com.waly.kanban.dto.BoardDTO;
import com.waly.kanban.entities.Board;
import com.waly.kanban.projections.BoardProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query(nativeQuery = true, value = """
            SELECT b.id, b.title, b.total_cards AS totalCards FROM tb_board b
            WHERE b.team_id = :teamId AND
            LOWER(b.title) LIKE LOWER(CONCAT('%', :title ,'%'))
            """)
    Page<BoardProjection> findAllPageable(@Param(value = "title") String title, Pageable pageable, @Param(value = "teamId") Long teamId);

}
