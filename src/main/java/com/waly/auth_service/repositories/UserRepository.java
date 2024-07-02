package com.waly.auth_service.repositories;

import com.waly.auth_service.entities.User;
import com.waly.auth_service.projections.UserDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    Boolean existsByNickname(String nickname);
    Boolean existsByEmail(String email);

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

    @Query(value = """
            SELECT u FROM User u
            WHERE UPPER(u.email) LIKE UPPER(CONCAT('%', :query, '%%'))
            OR UPPER(u.nickname) LIKE UPPER(CONCAT('%', :query, '%%'))
            OR UPPER(u.name) LIKE UPPER(CONCAT('%', :query, '%%'))
            """)
    List<User> findByQuery(String query);

}

