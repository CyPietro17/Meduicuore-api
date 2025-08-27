package it.pietro.salvatore.medicuore.utils.dtocast;

import it.pietro.salvatore.medicuore.dto.response.RicoveroResponseDto;
import it.pietro.salvatore.medicuore.entity.Ricovero;

public class RicoveroCast {

  public static RicoveroResponseDto castRicovero(Ricovero ricovero) {
    RicoveroResponseDto responseDto = new RicoveroResponseDto();
    responseDto.setN_id(ricovero.getN_id());
    responseDto.setD_inizioRicovero(ricovero.getD_inizioRicovero());
    responseDto.setD_fineRicovero(ricovero.getD_fineRicovero());
    responseDto.setN_paziente(ricovero.getN_paziente());
    responseDto.setT_reparto(ricovero.getT_reparto());
    return responseDto;
  }
}
