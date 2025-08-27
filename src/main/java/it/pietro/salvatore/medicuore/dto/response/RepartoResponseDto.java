package it.pietro.salvatore.medicuore.dto.response;

import lombok.Data;

@Data
public class RepartoResponseDto {

  private Long n_id;
  private String t_nome;
  private Integer n_postiLettoEffettivi;
  private Integer n_postiLettoDisponibili;
  private String b_postiLiberi;
}
