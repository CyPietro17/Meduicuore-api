package it.svil.studio.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class PazienteResponseDto {

    private Long n_id;
    private String t_nome;
    private String t_cognome;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date d_dataNascita;
    private String t_codiceFiscale;
    private String b_ricoverato;
}
