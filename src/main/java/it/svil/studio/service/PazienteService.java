package it.svil.studio.service;

import it.svil.studio.dto.PazienteRequestDto;
import it.svil.studio.dto.PazienteResponseDto;
import it.svil.studio.entity.Paziente;
import it.svil.studio.repo.PazienteRepo;
import it.svil.studio.util.GenericUtil;
import it.svil.studio.util.PazienteCast;
import it.svil.studio.util.StatoRicovero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PazienteService {

    private PazienteRepo pazienteRepo;

    @Autowired
    public PazienteService(PazienteRepo pazienteRepo) {
        this.pazienteRepo = pazienteRepo;
    }

    public Paziente add(PazienteRequestDto requestDto){
        Paziente paziente = new Paziente();
        if(
                requestDto.getT_nome() != null && !requestDto.getT_nome().isBlank()
                && requestDto.getT_cognome() != null && !requestDto.getT_cognome().isBlank()
                && requestDto.getD_dataNascita() != null
        ){
            paziente.setT_nome(requestDto.getT_nome());
            paziente.setT_cognome(requestDto.getT_cognome());
            paziente.setD_dataNascita(requestDto.getD_dataNascita());
            paziente.setT_codiceFiscale(GenericUtil.setCodiceFiscale(paziente));
            paziente.setB_ricoverato(StatoRicovero.CHIUSO);
            return pazienteRepo.save(paziente);
        }
        paziente.setN_id(-1L);
        return paziente;
    }

    public Paziente modifica(Long id, PazienteRequestDto requestDto){
        Paziente paziente = this.trovaPaziente(id);
        if(paziente.getN_id() != -1L){
            if(requestDto.getT_nome() != null && !requestDto.getT_nome().isBlank())
                paziente.setT_nome(requestDto.getT_nome());
            if(requestDto.getT_cognome() != null && !requestDto.getT_cognome().isBlank())
                paziente.setT_cognome(requestDto.getT_cognome());
            if(requestDto.getD_dataNascita() != null)
                paziente.setD_dataNascita(requestDto.getD_dataNascita());
            if(requestDto.getT_codiceFiscale() != null && !requestDto.getT_codiceFiscale().isBlank())
                paziente.setT_codiceFiscale(requestDto.getT_codiceFiscale());

            return pazienteRepo.saveAndFlush(paziente);
        }
        return paziente;
    }

    public List<Paziente> pazienti(){
        return pazienteRepo.findAll();
    }

    public Paziente trovaPaziente(Long id){
        Paziente paziente = new Paziente();
        paziente.setN_id(-1L);
        return pazienteRepo.findById(id).orElse(paziente);
    }

    public PazienteResponseDto response(Paziente paziente){
        return PazienteCast.castPaziente(paziente);
    }
}
