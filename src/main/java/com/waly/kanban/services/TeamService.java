package com.waly.kanban.services;

import com.waly.kanban.dto.TeamDTO;
import com.waly.kanban.entities.Team;
import com.waly.kanban.exceptions.NotFoundException;
import com.waly.kanban.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    @Autowired
    private TeamRepository repository;

    public TeamDTO findById(Long id){
        Team team = repository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Equipe não encontrada para o id: " + id);
        });
        return new TeamDTO(team);
    }
}
