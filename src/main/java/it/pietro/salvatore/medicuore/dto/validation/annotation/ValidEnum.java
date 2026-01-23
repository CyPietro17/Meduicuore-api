package it.pietro.salvatore.medicuore.dto.validation.annotation;

import it.pietro.salvatore.medicuore.dto.validation.EnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = EnumValidator.class)
public @interface ValidEnum {

  Class<? extends Enum<?>> enumClass();

  String message() default "invalid value";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
