package com.waly.kanban.services.validation;

import java.util.ArrayList;
import java.util.List;

import com.waly.kanban.dto.UserInsertDTO;
import com.waly.kanban.entities.User;
import com.waly.kanban.exceptions.FieldMessage;
import com.waly.kanban.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;


public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

    @Autowired
    private UserRepository userRepository;
    @Override
    public void initialize(UserInsertValid ann) {
    }

    @Override
    public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();

        if(userRepository.existsByEmail(dto.getEmail())){
            list.add(new FieldMessage("Email", "Email ja cadastrado"));
        }
        if(userRepository.existsByNickname(dto.getNickname())){
            list.add(new FieldMessage("Nickname", "Nickname ja cadastrado"));
        }
        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return list.isEmpty();
    }
}