package it.pietro.salvatore.medicuore.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public record EmployeeRequestDto(String name,
                                 String surname,
                                 String fiscalCode,
                                 String profession,
                                 Long departmentID,
                                 String beActive,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd")
                                 Date birthday) {
}
