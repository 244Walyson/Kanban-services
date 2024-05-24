package com.waly.kanban.repositories;

import com.waly.kanban.entities.Team;
import com.waly.kanban.entities.TeamOutbox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamOutboxRepository extends JpaRepository<TeamOutbox, Long> {

}
