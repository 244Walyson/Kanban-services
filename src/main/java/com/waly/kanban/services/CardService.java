package com.waly.kanban.services;

import com.waly.kanban.dto.CardDTO;
import com.waly.kanban.dto.CardInsertDTO;
import com.waly.kanban.dto.SetCollaboratorDTO;
import com.waly.kanban.entities.Board;
import com.waly.kanban.entities.Card;
import com.waly.kanban.entities.User;
import com.waly.kanban.repositories.BoardRepository;
import com.waly.kanban.repositories.CardRepository;
import com.waly.kanban.exceptions.NotFoundException;
import com.waly.kanban.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CardService {

    @Autowired
    private CardRepository repository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = false)
    public CardDTO insert(CardInsertDTO dto) {
        Card card = new Card();
        copyDtoToEntity(card, dto);
        card = repository.save(card);
        return new CardDTO(card);
    }

    @Transactional(readOnly = true)
    public CardDTO findById(Long id) {
        return new CardDTO(repository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Card não encontrado para o id: " + id);
        }));
    }

    @Transactional(readOnly = false)
    public void replacePosition(int sourceIndex, int destinationIndex, Long boardId){
        List<Card> cards = repository.findAllByBoard(boardId);

        Card card = cards.remove(sourceIndex);
        cards.add(destinationIndex, card);

        int min = sourceIndex < destinationIndex ? sourceIndex : destinationIndex;
        int max = sourceIndex < destinationIndex ? destinationIndex : sourceIndex;

        for (int i = min; i <= max; i++){
            repository.updateCardPosition(cards.get(i).getId(), i);
        }
        repository.saveAll(cards);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if(!repository.existsById(id)){
            throw new NotFoundException("Card não encontrado para o id: " + id);
        }
        try {
            repository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional(readOnly = false)
    public CardDTO update(Long id, CardInsertDTO dto){
        if(!repository.existsById(id)){
            throw new NotFoundException("Card não encontrado para o id: " + id);
        }
        Card card = repository.getReferenceById(id);
        copyDtoToEntity(card, dto);
        card = repository.save(card);
        return new CardDTO(card);
    }

    @Transactional(readOnly = false)
    public CardDTO updateCollaborators(Long cardId, SetCollaboratorDTO dto) {
        validateReq(cardId, dto);
        Card card = repository.getReferenceById(cardId);
        User user = userRepository.getReferenceById(dto.getCollaboratorId());
        card.addCollaborator(user);
        card = repository.save(card);
        return new CardDTO(card);
    }

    private Card copyDtoToEntity(Card card, CardInsertDTO dto){
        Long boardId = dto.getBoardId();
        validateBoard(boardId);
        card.setTitle(dto.getTitle());
        card.setDescription(dto.getDescription());
        Board board = boardRepository.getReferenceById(boardId);
        card.setCardPosition(board.getTotalCards());
        board.setTotalCards(board.getTotalCards() + 1);
        board = boardRepository.save(board);
        card.setBoard(board);
        return card;
    }

    private void validateBoard(Long boardId) {
        if(!boardRepository.existsById(boardId)){
            throw new NotFoundException("Board não encontrado para o id: " + boardId);
        }
    }

    private void validateReq(Long cardId, SetCollaboratorDTO dto) {
        if(!repository.existsById(cardId)){
            throw new NotFoundException("Card não encontrado para o id: " + cardId);
        }
        if(!userRepository.existsById(dto.getCollaboratorId())){
            throw new NotFoundException("Colaborador não encontrado para o id: " + dto.getCollaboratorId());
        }
    }
}
