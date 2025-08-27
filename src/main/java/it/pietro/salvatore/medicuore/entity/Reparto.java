package it.pietro.salvatore.medicuore.entity;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serial;
import java.io.Serializable;

@Data
@Alias(value = "reparti")
public class Reparto implements Serializable {

  @Serial
  private static final long serial = 1L;

  private Long n_id;
  private String t_nome;
  private Integer n_postiLettoEffettivi;
  private Integer n_postiLettoDisponibili;
  private String b_postiLiberi;
}
