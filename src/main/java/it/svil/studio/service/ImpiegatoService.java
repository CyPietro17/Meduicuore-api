package it.svil.studio.service;

import it.svil.studio.dto.ImpiegatoRequestDto;
import it.svil.studio.dto.ImpiegatoResponseDto;
import it.svil.studio.entity.Impiegato;
import it.svil.studio.entity.Reparto;
import it.svil.studio.repo.ImpiegatoRepo;
import it.svil.studio.util.GenericUtil;
import it.svil.studio.util.ImpiegatoCast;
import it.svil.studio.util.StatoAssunzione;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImpiegatoService {

    private ImpiegatoRepo impiegatoRepo;
    private RepartoService repartoService;

    @Autowired
    public ImpiegatoService(ImpiegatoRepo impiegatoRepo, RepartoService repartoService) {
        this.impiegatoRepo = impiegatoRepo;
        this.repartoService = repartoService;
    }

    public Impiegato add(ImpiegatoRequestDto requestDto){
        Impiegato impiegato = new Impiegato();
        if(requestDto != null){
            if (
                    requestDto.getT_nome() != null && !requestDto.getT_nome().isBlank()
                    && requestDto.getT_cognome() != null && !requestDto.getT_cognome().isBlank()
                    && requestDto.getD_dataNascita() != null
                    && requestDto.getT_professione() != null && !requestDto.getT_professione().isBlank()
                    && (requestDto.getT_professione().equals(GenericUtil.MEDICO)
                    || requestDto.getT_professione().equals(GenericUtil.INFERMIERE)
                    || requestDto.getT_professione().equals(GenericUtil.CAPOREPARTO))
                    && requestDto.getN_reparto() != null
                    && !repartoService.trovaDaId(requestDto.getN_reparto()).equals(repartoService.trovaDaId(-1L))
            ) {
                impiegato.setT_nome(requestDto.getT_nome());
                impiegato.setT_cognome(requestDto.getT_cognome());
                impiegato.setD_dataNascita(requestDto.getD_dataNascita());
                impiegato.setT_codiceFiscale(GenericUtil.setCodiceFiscale(impiegato));
                impiegato.setT_professione(requestDto.getT_professione());
                impiegato.setT_reparto(repartoService.trovaDaId(requestDto.getN_reparto()));
                impiegato.setB_active(StatoAssunzione.ASSUNTO);
                return impiegatoRepo.save(impiegato);
            }
        }
        impiegato.setN_id(-1L);
        return impiegato;
    }

    public Impiegato trova(Long id){
        Impiegato impiegato = new Impiegato();
        impiegato.setN_id(-1L);
        return impiegatoRepo.findById(id).orElse(impiegato);
    }

    public List<Impiegato> tuttiImpiegati(){
        return impiegatoRepo.findAll();
    }

    public List<Impiegato> assunti(){
        List<Impiegato> listAssunti = new ArrayList<>();
        for(Impiegato impiegato: impiegatoRepo.findAll()){
            if(impiegato.getB_active().equals(StatoAssunzione.ASSUNTO))
                listAssunti.add(impiegato);
        }
        return listAssunti;
    }

    public List<Impiegato> dimessi(){
        List<Impiegato> listDimessi = new ArrayList<>();
        for(Impiegato impiegato: impiegatoRepo.findAll()){
            if(impiegato.getB_active().equals(StatoAssunzione.NON_ASSUNTO))
                listDimessi.add(impiegato);
        }
        return listDimessi;
    }

    public Impiegato modifica(Long id, ImpiegatoRequestDto requestDto){
        Impiegato impiegato = this.trova(id);
        if(impiegato.getN_id() != -1L) {
            if (requestDto.getT_nome() != null && !requestDto.getT_nome().isBlank())
                impiegato.setT_nome(requestDto.getT_nome());
            if (requestDto.getT_cognome() != null && !requestDto.getT_cognome().isBlank())
                impiegato.setT_cognome(requestDto.getT_cognome());
            if (requestDto.getD_dataNascita() != null)
                impiegato.setD_dataNascita(requestDto.getD_dataNascita());
            if (requestDto.getT_professione() != null && !requestDto.getT_professione().isBlank()) {
                impiegato.setT_professione(requestDto.getT_professione());
                if(
                        !requestDto.getT_professione().equals(GenericUtil.MEDICO)
                        && !requestDto.getT_professione().equals(GenericUtil.INFERMIERE)
                        && !requestDto.getT_professione().equals(GenericUtil.CAPOREPARTO)
                ){
                    Impiegato imp = new Impiegato();
                    imp.setN_id(-1L);
                    return imp;
                }
            }
            if(requestDto.getT_codiceFiscale() != null && !requestDto.getT_codiceFiscale().isBlank())
                impiegato.setT_codiceFiscale(requestDto.getT_codiceFiscale().toUpperCase());
            if (requestDto.getN_reparto() != null){
                impiegato.setT_reparto(repartoService.trovaDaId(requestDto.getN_reparto()));
                if(impiegato.getT_reparto().getN_id() == -1L) {
                    Impiegato imp = new Impiegato();
                    imp.setN_id(-1L);
                    return imp;
                }
            }
            return impiegatoRepo.saveAndFlush(impiegato);
        }
        return impiegato;
    }

    public Impiegato dimissione(Long id){
        Impiegato impiegato = this.trova(id);
        if(impiegato.getN_id() != -1L){
            if(impiegato.getB_active().equals(StatoAssunzione.ASSUNTO) && repartoService.trovaDaId(impiegato.getT_reparto().getN_id()).getN_id() != -1L){
                Reparto reparto = new Reparto();
                reparto.setN_id(-1L);
                impiegato.setT_reparto(reparto);
                impiegato.setB_active(StatoAssunzione.NON_ASSUNTO);
                return impiegatoRepo.saveAndFlush(impiegato);
            }
            impiegato = new Impiegato();
            impiegato.setN_id(-1L);
            return impiegato;
        }
        return impiegato;
    }

    public ImpiegatoResponseDto response(Impiegato impiegato){
        return ImpiegatoCast.castImpiegato(impiegato);
    }
}
