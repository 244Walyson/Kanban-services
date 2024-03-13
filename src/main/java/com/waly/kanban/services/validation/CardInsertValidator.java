//package com.waly.kanban.services.validation;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//import com.waly.kanban.dto.CardInsertDTO;
//import com.waly.kanban.exceptions.FieldMessage;
//import com.waly.kanban.repositories.CardRepository;
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import org.springframework.beans.factory.annotation.Autowired;
//
//
//public class CardInsertValidator implements ConstraintValidator<CardInsertValid, CardInsertDTO> {
//
//    @Autowired
//    private CardRepository userRepository;
//    @Override
//    public void initialize(CardInsertValid ann) {
//    }
//
//    @Override
//    public boolean isValid(CardInsertDTO dto, ConstraintValidatorContext context) {
//
//        List<FieldMessage> list = new ArrayList<>();
//
//        Client client = userRepository.findByEmail(dto.getEmail());
//        if(client != null){
//            list.add(new FieldMessage("Email", "Email ja cadastrado"));
//        }
//        for (FieldMessage e : list) {
//            context.disableDefaultConstraintViolation();
//            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
//                    .addConstraintViolation();
//        }
//        return list.isEmpty();
//    }
//}