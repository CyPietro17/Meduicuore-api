package it.svil.studio.util;

import it.svil.studio.dto.RepartoResponseDto;
import it.svil.studio.entity.Reparto;

public class RepartoCast {

    public static RepartoResponseDto castReparto(Reparto reparto){
        RepartoResponseDto responseDto = new RepartoResponseDto();
        responseDto.setN_id(reparto.getN_id());
        responseDto.setT_nome(reparto.getT_nome());
        responseDto.setN_postiLettoEffettivi(reparto.getN_postiLettoEffettivi());
        responseDto.setN_postiLettoDisponibili(reparto.getN_postiLettoDisponibili());
        responseDto.setB_postiLiberi(reparto.getB_postiLiberi());
        return responseDto;
    }
}
