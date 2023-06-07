package it.svil.studio.service;

import it.svil.studio.dto.RepartoRequestDto;
import it.svil.studio.dto.RepartoResponseDto;
import it.svil.studio.entity.Reparto;
import it.svil.studio.repo.RepartoRepo;
import it.svil.studio.util.PostiDisponibili;
import it.svil.studio.util.RepartoCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepartoService {

    private RepartoRepo repartoRepo;

    @Autowired
    public RepartoService(RepartoRepo repartoRepo) {
        this.repartoRepo = repartoRepo;
    }

    public Reparto add(RepartoRequestDto requestDto){
        Reparto reparto = new Reparto();
        if(
                requestDto.getT_nome() != null && !requestDto.getT_nome().isBlank()
                && requestDto.getN_postiLettoEffettivi() != null
                && requestDto.getN_postiLettoEffettivi() > 0
        ){
            reparto.setT_nome(requestDto.getT_nome());
            reparto.setN_postiLettoEffettivi(requestDto.getN_postiLettoEffettivi());
            reparto.setN_postiLettoDisponibili(requestDto.getN_postiLettoEffettivi());
            reparto.setB_postiLiberi(PostiDisponibili.DISPONIBILI);
            return repartoRepo.save(reparto);
        }
        reparto.setN_id(-1L);
        return reparto;
    }

    public List<Reparto> tuttiReparti(){
        return repartoRepo.findAll();
    }

    public Reparto trovaDaId(Long id){
        Reparto reparto =  new Reparto();
        reparto.setN_id(-1L);
        return repartoRepo.findById(id).orElse(reparto);
    }


    public RepartoResponseDto response(Reparto reparto){
        return RepartoCast.castReparto(reparto);
    }
}
