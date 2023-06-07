package it.svil.studio.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ImpiegatoRequestDto {

    private String t_nome;
    private String t_cognome;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date d_dataNascita;
    private String t_codiceFiscale;
    private String t_professione;
    private Long n_reparto;
    private String b_active;
}
