package com.waly.kanban.repositories;

import com.waly.kanban.entities.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("""
            SELECT obj FROM Team obj
            JOIN FETCH obj.collaborators c
            JOIN FETCH obj.boards b
            WHERE LOWER(obj.name) LIKE LOWER(CONCAT('%', :query ,'%')) OR
            LOWER(obj.description) LIKE LOWER(CONCAT('%', :query ,'%')) OR
            LOWER(obj.occupationArea) LIKE LOWER(CONCAT('%', :query ,'%'))
            """)
    Page<Team> findAllByAttributes(String query, Pageable pageable);


}
