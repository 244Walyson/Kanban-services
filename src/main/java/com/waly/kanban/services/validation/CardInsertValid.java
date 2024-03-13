//package com.waly.kanban.services.validation;
//
//import jakarta.validation.Constraint;
//import jakarta.validation.Payload;
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
//
//@Constraint(validatedBy = UserInsertValidator.class)
//@Target({ ElementType.TYPE })
//@Retention(RetentionPolicy.RUNTIME)
//
//public @interface CardInsertValid {
//    String message() default "Validation error";
//
//    Class<?>[] groups() default {};
//
//    Class<? extends Payload>[] payload() default {};
//}