package it.svil.studio.util;

import it.svil.studio.dto.PazienteResponseDto;
import it.svil.studio.entity.Paziente;

public class PazienteCast {

    public static PazienteResponseDto castPaziente(Paziente paziente){
        PazienteResponseDto responseDto = new PazienteResponseDto();
        responseDto.setN_id(paziente.getN_id());
        responseDto.setT_nome(paziente.getT_nome());
        responseDto.setT_cognome(paziente.getT_cognome());
        responseDto.setD_dataNascita(paziente.getD_dataNascita());
        responseDto.setT_codiceFiscale(paziente.getT_codiceFiscale());
        responseDto.setB_ricoverato(paziente.getB_ricoverato());
        return responseDto;
    }
}
