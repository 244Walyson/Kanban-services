package com.waly.auth_service.services.validation;


import com.waly.auth_service.dtos.UserUpdateDTO;
import com.waly.auth_service.entities.User;
import com.waly.auth_service.exceptions.FieldMessage;
import com.waly.auth_service.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {


    private final UserRepository userRepository;
    private final HttpServletRequest request;

    public UserUpdateValidator(UserRepository userRepository, HttpServletRequest request) {
        this.userRepository = userRepository;
        this.request = request;
    }

    @Override
    public void initialize(UserUpdateValid ann) {
        // method not implemented
    }

    @Override
    public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {

        @SuppressWarnings("unchecked")
        var uriVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String idString = uriVars.get("id");
        Long userId = Long.parseLong(idString);

        List<FieldMessage> list = new ArrayList<>();

        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);
        if(user != null && !userId.equals(user.getId())){
            list.add(new FieldMessage("Email", "Email ja cadastrado"));
        }
        if (user != null
                && !userId.equals(user.getId())
                && !user.getNickname().equals(dto.getNickname())
                && userRepository.existsByNickname(dto.getNickname())) {
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