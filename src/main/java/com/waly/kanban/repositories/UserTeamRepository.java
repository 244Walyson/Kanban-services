package com.waly.kanban.repositories;

import com.waly.kanban.entities.UserTeam;
import com.waly.kanban.entities.UserTeamPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserTeamRepository extends JpaRepository<UserTeam, UserTeamPK> {

    @Query(nativeQuery = true, value = """
            SELECT user_id, is_admin, team_id FROM tb_user_team
            WHERE team_id = :id
            AND is_admin = true
            """)
    List<UserTeam> findAllAdmins(Long id);
}
