package it.pietro.salvatore.medicuore.dto.validation;

import it.pietro.salvatore.medicuore.dto.validation.annotation.ValidEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

  Set<String> allowedValues;

  @Override
  public void initialize(ValidEnum constraintAnnotation) {
    allowedValues = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants()).map(Enum::name).collect(Collectors.toSet());
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value == null || allowedValues.contains(value);
  }
}
