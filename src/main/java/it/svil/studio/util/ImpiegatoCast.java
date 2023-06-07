package it.svil.studio.util;

import it.svil.studio.dto.ImpiegatoResponseDto;
import it.svil.studio.entity.Impiegato;

public class ImpiegatoCast {

    public static ImpiegatoResponseDto castImpiegato(Impiegato impiegato){
        ImpiegatoResponseDto responseDto = new ImpiegatoResponseDto();
        responseDto.setN_id(impiegato.getN_id());
        responseDto.setT_nome(impiegato.getT_nome());
        responseDto.setT_cognome(impiegato.getT_cognome());
        responseDto.setD_dataNascita(impiegato.getD_dataNascita());
        responseDto.setT_codiceFiscale(impiegato.getT_codiceFiscale());
        responseDto.setT_professione(impiegato.getT_professione());
        responseDto.setT_reparto(impiegato.getT_reparto());
        responseDto.setB_active(impiegato.getB_active());
        return responseDto;
    }
}
