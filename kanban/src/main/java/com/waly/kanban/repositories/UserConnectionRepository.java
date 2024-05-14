package com.waly.kanban.repositories;

import com.waly.kanban.entities.User;
import com.waly.kanban.entities.UserConnection;
import com.waly.kanban.entities.UserConnectionPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserConnectionRepository extends JpaRepository<UserConnection, UserConnectionPK> {

    @Query(nativeQuery = true,
            value = """
                    SELECT * FROM tb_user_connection
                    WHERE user_id1 = :id AND user_id2 = :friendId
                    """)
    Optional<UserConnection> findByUserIdAndFriendId(Long id, Long friendId);

    @Query(nativeQuery = true,
            value = """
                    SELECT * FROM tb_user_connection
                    WHERE user_id1 = :friendId OR user_id2 = :id
                    AND status = 'true'
                    """)
    List<UserConnection> findAllByUserId(Long id);
}
