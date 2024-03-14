package com.waly.kanban.services;

import com.waly.kanban.dto.CardInsertDTO;
import com.waly.kanban.dto.TeamDTO;
import com.waly.kanban.dto.TeamInsertDTO;
import com.waly.kanban.entities.Team;
import com.waly.kanban.exceptions.DatabaseException;
import com.waly.kanban.exceptions.NotFoundException;
import com.waly.kanban.repositories.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamService {

    @Autowired
    private TeamRepository repository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public Page<TeamDTO> findAll(String query, Pageable pageable){
        Page<Team> team = repository.findAllByAttributes(query, pageable);
        return team.map(TeamDTO::new);
    }

    @Transactional(readOnly = true)
    public TeamDTO findById(Long id){
        Team team = repository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Equipe não encontrada para o id: " + id);
        });
        return new TeamDTO(team);
    }

    @Transactional(readOnly = false)
    public TeamDTO insert(TeamInsertDTO dto){
        Team team = new Team();
        modelMapper.map(dto, team);
        team = repository.save(team);
        return new TeamDTO(team);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if(!repository.existsById(id)){
            throw new NotFoundException("Team não encontrada para o id: " + id);
        }
        try {
            repository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException("Falha de integridade referencial team de id: " + id);
        }
    }

    @Transactional(readOnly = false)
    public TeamDTO update(Long id, TeamInsertDTO dto) {
        Team team = repository.getReferenceById(id);
        copyDtoToEntity(dto, team);
        team = repository.save(team);
        return new TeamDTO(team);

    }

    private void copyDtoToEntity(TeamInsertDTO dto, Team team) {
        team.setDescription(dto.getDescription());
        team.setName(dto.getName());
        team.setOccupationArea(dto.getOccupationArea());
    }
}
