package com.waly.kanban.services;

import com.waly.kanban.entities.Team;
import com.waly.kanban.entities.User;
import com.waly.kanban.entities.UserTeamPK;
import com.waly.kanban.repositories.BoardRepository;
import com.waly.kanban.repositories.TeamRepository;
import com.waly.kanban.repositories.UserTeamRepository;
import com.waly.kanban.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("authAdmin")
public class CustomPermissions {

    @Autowired
    private UserService userService;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserTeamRepository userTeamRepository;
    @Autowired
    private BoardRepository boardRepository;

    @Transactional
    public boolean isAdminOfTeam(Long teamId){
        log.info(String.valueOf(teamId));
        if (!teamRepository.existsById(teamId)) return true;
        User user = userService.authenticade();
        Team team = teamRepository.getReferenceById(teamId);
        return userTeamRepository.findById(new UserTeamPK(user, team))
                .map(userTeam -> userTeam.isAdmin())
                .orElse(false);
    }

    @Transactional
    public boolean isMemberOfTeam(Long teamId){
        if(!teamRepository.existsById(teamId)){
            return true;
        }
        User user = userService.authenticade();
        Team team = teamRepository.getReferenceById(teamId);
        return userTeamRepository.existsById(new UserTeamPK(user, team));
    }
    @Transactional
    public boolean isMemberOfTeamByBoard(Long boardId){
        if(!boardRepository.existsById(boardId)){
            return true;
        }
        User user = userService.authenticade();
        Long teamId = boardRepository.getReferenceById(boardId).getTeam().getId();
        Team team = teamRepository.getReferenceById(teamId);
        return userTeamRepository.existsById(new UserTeamPK(user, team));
    }
}
