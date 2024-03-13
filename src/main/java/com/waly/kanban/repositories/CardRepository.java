package com.waly.kanban.repositories;

import com.waly.kanban.entities.Board;
import com.waly.kanban.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM tb_card c WHERE c.board_id = :boardId ORDER BY c.card_position
            """)
    List<Card> findAllByBoard(Long boardId);

    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE tb_card SET card_position = :newPosition WHERE id = :cardId
            """)
    void updateCardPosition(Long cardId, int newPosition);

}
