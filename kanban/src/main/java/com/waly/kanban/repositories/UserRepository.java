package com.waly.kanban.repositories;

import com.waly.kanban.entities.Team;
import com.waly.kanban.entities.User;
import com.waly.kanban.projections.UserDetailsProjection;
import org.apache.commons.lang3.text.translate.NumericEntityUnescaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

}
