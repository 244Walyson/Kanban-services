package com.waly.kanban.services;

import com.waly.kanban.dto.*;
import com.waly.kanban.entities.*;
import com.waly.kanban.exceptions.DatabaseException;
import com.waly.kanban.exceptions.NotFoundException;
import com.waly.kanban.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service("TeamService")
public class TeamService {

    @Autowired
    private TeamRepository repository;
    @Autowired
    private UserTeamRepository userTeamRepository;
    @Autowired
    private TeamOutboxRepository teamOutboxRepository;
    @Autowired
    private UserRepository  userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserOutboxRepository userOutboxRepository;

    @Transactional(readOnly = true)
    public Page<TeamDTO> findAll(String query, Pageable pageable) {
        Page<Team> team = repository.findAllByAttributes(query, pageable);
        return team.map(TeamDTO::new);
    }

    @Transactional(readOnly = true)
    public TeamDTO findById(Long id) {
        Team team = repository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Equipe não encontrada para o id: " + id);
        });
        return new TeamDTO(team);
        }

    @Transactional(readOnly = false)
    public TeamDTO insert(TeamInsertDTO dto) {
        Team team = new Team();
        modelMapper.map(dto, team);
        team = repository.save(team);
        UserTeam userTeam = saveUserTeam(team);
        team.addUserTeam(userTeam);
        TeamOutbox teamOutbox = new TeamOutbox(team);
        UserOutbox userOutbox = new UserOutbox(userService.authenticade());
        userOutbox.setTeamId(team.getId());
        userOutboxRepository.save(userOutbox);
        teamOutboxRepository.save(teamOutbox);
        return new TeamDTO(team);
    }

    private UserTeam saveUserTeam(Team team) {
        UserTeamPK pk = new UserTeamPK(userService.authenticade(), team);
        UserTeam userTeam = new UserTeam(pk, true);
        return userTeamRepository.save(userTeam);
    }

    @Transactional
    public TeamDTO addUserToTeam(Long teamId, AddUserDTO addUserDTO) {
        Team team = repository.findById(teamId).orElseThrow(() -> {
            throw new NotFoundException("Equipe não encontrada para o id: " + teamId);
        });
        User user = userRepository.findById(addUserDTO.getUserId()).orElseThrow(() -> {
            throw new NotFoundException("Usuário não encontrado para o id: " + addUserDTO.getUserId());
        });
        UserTeamPK pk = new UserTeamPK(user, team);
        if(userTeamRepository.existsById(pk)){
            throw new DatabaseException("Usuário já pertence a equipe");
        }
        UserTeam userTeam = new UserTeam(pk, addUserDTO.getIsAdmin());
        userTeam = userTeamRepository.save(userTeam);
        team.addUserTeam(userTeam);
        team = repository.save(team);
        UserOutbox userOutbox = new UserOutbox(user);
        userOutbox.setTeamId(teamId);
        userOutboxRepository.save(userOutbox);
        return new TeamDTO(team);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Team não encontrada para o id: " + id);
        }
        try {
            User user = userService.authenticade();
            Team team = repository.getReferenceById(id);
            userTeamRepository.deleteById(new UserTeamPK(user, team));
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial team de id: " + id);
        }
    }

    @Transactional(readOnly = false)
    public TeamDTO update(Long id, TeamInsertDTO dto) {
        if (!repository.existsById(id)){
            throw new NotFoundException("Team não encontrado para o id: " + id);
        }
        Team team = repository.getReferenceById(id);
        copyDtoToEntity(dto, team);
        team = repository.save(team);
        teamOutboxRepository.save(new TeamOutbox(team));
        return new TeamDTO(team);

    }

    private void copyDtoToEntity(TeamInsertDTO dto, Team team) {
        team.setDescription(dto.getDescription());
        team.setName(dto.getName());
        team.setOccupationArea(dto.getOccupationArea());
        team.setGithubLink(dto.getGithubLink());
    }
}
