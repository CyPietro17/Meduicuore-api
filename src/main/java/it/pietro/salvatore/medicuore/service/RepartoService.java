package it.pietro.salvatore.medicuore.service;

import it.pietro.salvatore.medicuore.dto.request.RepartoRequestDto;
import it.pietro.salvatore.medicuore.dto.response.RepartoResponseDto;
import it.pietro.salvatore.medicuore.entity.Reparto;
import it.pietro.salvatore.medicuore.mappers.RepartoMapper;
import it.pietro.salvatore.medicuore.utils.PostiDisponibili;
import it.pietro.salvatore.medicuore.utils.dtocast.RepartoCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepartoService {

  private final RepartoMapper repartoMapper;

  @Autowired
  public RepartoService(RepartoMapper repartoMapper) {
    this.repartoMapper = repartoMapper;
  }

  public Reparto add(RepartoRequestDto requestDto) {
    Reparto reparto = new Reparto();
    if (requestDto.getT_nome() != null && !requestDto.getT_nome().isBlank() && requestDto.getN_postiLettoEffettivi() != null &&
          requestDto.getN_postiLettoEffettivi() > 0) {
      reparto.setT_nome(requestDto.getT_nome());
      reparto.setN_postiLettoEffettivi(requestDto.getN_postiLettoEffettivi());
      reparto.setN_postiLettoDisponibili(requestDto.getN_postiLettoEffettivi());
      reparto.setB_postiLiberi(PostiDisponibili.DISPONIBILI);
      repartoMapper.save(reparto);
      return reparto;
    }
    reparto.setN_id(-1L);
    return reparto;
  }

  public List<Reparto> tuttiReparti() {
    return repartoMapper.findAll();
  }

  public Reparto trovaDaId(Long id) {
    Reparto reparto = new Reparto();
    reparto.setN_id(-1L);
    return repartoMapper.findById(id).orElse(reparto);
  }

  public Reparto disponibilitaLetti(Reparto reparto) {
    repartoMapper.update(reparto.getN_id(), reparto);
    return reparto;
  }

  public Reparto trovaDaNome(Reparto reparto) {
    return repartoMapper.findByName(reparto.getT_nome()).orElse(this.trovaDaId(reparto.getN_id()));
  }

  public RepartoResponseDto response(Reparto reparto) {
    return RepartoCast.castReparto(reparto);
  }
}
