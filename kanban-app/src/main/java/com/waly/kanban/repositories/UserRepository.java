package com.waly.kanban.repositories;

import com.waly.kanban.entities.Card;
import com.waly.kanban.entities.Team;
import com.waly.kanban.entities.User;
import com.waly.kanban.projections.UserDetailsProjection;
import org.apache.commons.lang3.text.translate.NumericEntityUnescaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = """
                SELECT tb_user.email AS username, tb_user.password, tb_role.id AS roleId, tb_role.authority
                FROM tb_user
                LEFT JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
                LEFT JOIN tb_role ON tb_role.id = tb_user_role.role_id
                WHERE tb_user.email = :email
            """)
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);

    @Query(nativeQuery = true, value = """
                SELECT tb_user.email AS username, tb_user.password, tb_role.id AS roleId, tb_role.authority
                FROM tb_user
                LEFT JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
                LEFT JOIN tb_role ON tb_role.id = tb_user_role.role_id
                WHERE tb_user.nickname = :username
            """)
    List<UserDetailsProjection> searchUserAndRolesByNickname(String username);

    Optional<User> findByEmail(String username);

    Optional<User> findByNickname(String nickname);

    Boolean existsByEmail(String email);

    Boolean existsByNickname(String nickname);

    @Query(value = """
            SELECT u FROM User u
            WHERE UPPER(u.email) LIKE UPPER(CONCAT('%', :query, '%%'))
            OR UPPER(u.nickname) LIKE UPPER(CONCAT('%', :query, '%%'))
            OR UPPER(u.name) LIKE UPPER(CONCAT('%', :query, '%%'))
            """)
    List<User> findByQuery(String query);

    @Query(value = """
            SELECT u.id AS id, u.name, u.nickname FROM User u
            WHERE u.id = :id
            """)
    Optional<User> findMinById(Long id);


    @Query(value = """
            SELECT u FROM User u
            LEFT JOIN FETCH u.cards
            LEFT JOIN FETCH u.teams
            WHERE u.id = :id
            """)
    Optional<User> findByIdWithCardsAndTeams(Long id);

}
