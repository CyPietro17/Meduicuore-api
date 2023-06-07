package it.svil.studio.dto;

import it.svil.studio.entity.Reparto;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ImpiegatoResponseDto {

    private java.lang.Long n_id;
    private String t_nome;
    private String t_cognome;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date d_dataNascita;
    private String t_codiceFiscale;
    private String t_professione;
    private Reparto t_reparto;
    private String b_active;
}
