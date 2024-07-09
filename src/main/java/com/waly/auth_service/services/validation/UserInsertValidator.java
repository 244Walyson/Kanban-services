package com.waly.auth_service.services.validation;


import com.waly.auth_service.dtos.UserInsertDTO;
import com.waly.auth_service.exceptions.FieldMessage;
import com.waly.auth_service.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;


public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

    private final UserRepository userRepository;

    public UserInsertValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(UserInsertValid ann) {
        // method not implemented
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
            context.buildConstraintViolationWithTemplate(e.message()).addPropertyNode(e.fieldName())
                    .addConstraintViolation();
        }
        return list.isEmpty();
    }
}