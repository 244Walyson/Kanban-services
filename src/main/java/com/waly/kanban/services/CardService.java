package com.waly.kanban.services;

import com.waly.kanban.dto.CardDTO;
import com.waly.kanban.dto.CardInsertDTO;
import com.waly.kanban.dto.ReplacementDTO;
import com.waly.kanban.entities.Board;
import com.waly.kanban.entities.Card;
import com.waly.kanban.repositories.BoardRepository;
import com.waly.kanban.repositories.CardRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class CardService {

    @Autowired
    private CardRepository repository;
    @Autowired
    private BoardRepository boardRepository;

    public CardDTO insert(CardInsertDTO dto) {
        Card card = copyDtoToEntity(dto);
        card = repository.save(card);
        return new CardDTO(card);
    }

    private Card copyDtoToEntity(CardInsertDTO dto){
        Card card = new Card();
        card.setTitle(dto.getTitle());
        card.setDescription(dto.getDescription());
        Board board = boardRepository.getReferenceById(dto.getBoard());
        card.setCardPosition(board.getTotalCards());
        board.setTotalCards(board.getTotalCards() + 1);
        board = boardRepository.save(board);
        card.setBoard(board);
        return card;
    }

    public CardDTO findById(Long id) {
        return new CardDTO(repository.findById(id).orElseThrow(() -> {
            throw new RuntimeException("Card não encontrado");
        }));
    }

    @Transactional
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
}
