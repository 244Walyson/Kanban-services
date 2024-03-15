package com.waly.kanban.services;

import com.waly.kanban.entities.Team;
import com.waly.kanban.entities.User;
import com.waly.kanban.entities.UserTeamPK;
import com.waly.kanban.repositories.TeamRepository;
import com.waly.kanban.repositories.UserTeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("authAdmin")
public class CustomPermission {

    @Autowired
    private UserService userService;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserTeamRepository userTeamRepository;

    @Transactional
    public boolean isAdminOfTeam(Long teamId){
        if (!teamRepository.existsById(teamId)) return true;
        User user = userService.authenticade();
        Team team = teamRepository.getReferenceById(teamId);
        return userTeamRepository.findById(new UserTeamPK(user, team))
                .map(userTeam -> userTeam.isAdmin())
                .orElse(false);
    }

}
