package com.waly.kanban.repositories;

import com.waly.kanban.entities.User;
import com.waly.kanban.entities.UserOutbox;
import com.waly.kanban.projections.UserDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserOutboxRepository extends JpaRepository<UserOutbox, Long> {

}
