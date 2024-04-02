package com.kanban.chat.services;

import com.kanban.chat.models.entities.ChatMessageEntity;
import com.kanban.chat.models.entities.UserEntity;
import com.kanban.chat.repositories.ChatRepository;
import com.kanban.chat.repositories.UserRepository;
import com.kanban.chat.utils.CustomUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private CustomUserUtil customUserUtil;
    @Autowired
    private UserRepository repository;

    public ChatMessageEntity saveChatMessage(ChatMessageEntity chatMessageEntity) {
        return chatRepository.save(chatMessageEntity);
    }

    @Transactional
    protected UserEntity authenticade(){
        try{
            String username = customUserUtil.getLoggedUsername();
            return null;
        }
        catch (Exception e){
            throw new UsernameNotFoundException("User not found");
        }
    }

    public List<ChatMessageEntity> findAllByReceiver() {
        String nickname = customUserUtil.getLoggedNickname();
        return chatRepository.findAllByReceiverUsername(nickname);
    }


}
