package it.pietro.salvatore.medicuore.entity;

import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@Alias(value = "pazienti")
public class Paziente implements Serializable, Persona {

  @Serial
  private static final long serial = 1L;

  private Long n_id;
  private String t_nome;
  private String t_cognome;
  //    @Temporal(TemporalType.DATE)
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date d_dataNascita;
  private String t_codiceFiscale;
  private String b_ricoverato;
}
