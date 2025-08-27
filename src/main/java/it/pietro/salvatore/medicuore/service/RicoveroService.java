package it.pietro.salvatore.medicuore.service;

import it.pietro.salvatore.medicuore.dto.request.RicoveroRequestDto;
import it.pietro.salvatore.medicuore.dto.response.RicoveroResponseDto;
import it.pietro.salvatore.medicuore.entity.Paziente;
import it.pietro.salvatore.medicuore.entity.Reparto;
import it.pietro.salvatore.medicuore.entity.Ricovero;
import it.pietro.salvatore.medicuore.mappers.RicoveroMapper;
import it.pietro.salvatore.medicuore.utils.PostiDisponibili;
import it.pietro.salvatore.medicuore.utils.StatoRicovero;
import it.pietro.salvatore.medicuore.utils.dtocast.RicoveroCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RicoveroService {

  private final RicoveroMapper ricoveroMapper;
  private final PazienteService pazienteService;
  private final RepartoService repartoService;

  @Autowired
  public RicoveroService(RicoveroMapper ricoveroMapper, PazienteService pazienteService, RepartoService repartoService) {
    this.ricoveroMapper = ricoveroMapper;
    this.pazienteService = pazienteService;
    this.repartoService = repartoService;
  }

  public List<Ricovero> tuttiRicoveri() {
    return ricoveroMapper.findAll();
  }

  public Ricovero add(RicoveroRequestDto requestDto) {
    Ricovero ricovero = new Ricovero();
    if (requestDto != null && requestDto.getN_paziente() != null && requestDto.getN_reparto() != null &&
          requestDto.getD_inizioRicovero() != null) {
      Reparto reparto = repartoService.trovaDaId(requestDto.getN_reparto());
      Paziente paziente = pazienteService.trovaPaziente(requestDto.getN_paziente());
      if (reparto.getN_id() != -1L && reparto.getB_postiLiberi().equals(PostiDisponibili.DISPONIBILI) &&
            paziente.getN_id() != -1L && paziente.getB_ricoverato().equals(StatoRicovero.CHIUSO)) {
        if (this.tuttiRicoveri() != null) {
          for (Ricovero r : this.tuttiRicoveri()) {
            if (r.getD_fineRicovero() == null && r.getN_paziente().getN_id().equals(requestDto.getN_paziente())) {
              ricovero.setN_id(-1L);
              return ricovero;
            }
          }
        }
        ricovero.setD_inizioRicovero(requestDto.getD_inizioRicovero());
        ricovero.setD_fineRicovero(null);
        ricovero.setN_paziente(pazienteService.trovaPaziente(requestDto.getN_paziente()));
        ricovero.setT_reparto(repartoService.trovaDaId(requestDto.getN_reparto()));
        paziente.setB_ricoverato(StatoRicovero.RICOVERATO);
        reparto.setN_postiLettoDisponibili(reparto.getN_postiLettoDisponibili() - 1);
        if (reparto.getN_postiLettoDisponibili() == 0) {
          reparto.setB_postiLiberi(PostiDisponibili.INDISPONIBILI);
        }
        ricoveroMapper.save(ricovero);
        repartoService.disponibilitaLetti(reparto);
        pazienteService.statoRicovero(paziente);
        return ricovero;
      }
    }
    ricovero.setN_id(-1L);
    return ricovero;
  }

  public Ricovero chiudiRicovero(RicoveroRequestDto requestDto) {
    Ricovero ricovero = new Ricovero();
    if ((requestDto != null && requestDto.getN_paziente() != null && requestDto.getN_reparto() != null &&
           requestDto.getD_fineRicovero() != null) && pazienteService.trovaPaziente(requestDto.getN_paziente()).getN_id() != -1L &&
          repartoService.trovaDaId(requestDto.getN_reparto()).getN_id() != -1L) {
      for (Ricovero r : this.tuttiRicoveri()) {
        if (r.getD_fineRicovero() == null && r.getN_paziente().getN_id().equals(requestDto.getN_paziente()) &&
              requestDto.getD_fineRicovero().compareTo(r.getD_inizioRicovero()) > 0) {
          ricovero = r;
          ricovero.setD_fineRicovero(requestDto.getD_fineRicovero());
          Paziente paziente = pazienteService.trovaPaziente(ricovero.getN_paziente().getN_id());
          paziente.setB_ricoverato(StatoRicovero.CHIUSO);
          Reparto reparto = repartoService.trovaDaId(ricovero.getT_reparto().getN_id());
          reparto.setN_postiLettoDisponibili(reparto.getN_postiLettoDisponibili() + 1);

          if (reparto.getB_postiLiberi().equals(PostiDisponibili.INDISPONIBILI)) {
            reparto.setB_postiLiberi(PostiDisponibili.DISPONIBILI);
          }

          ricoveroMapper.update(ricovero.getN_id(), ricovero);
          repartoService.disponibilitaLetti(reparto);
          pazienteService.statoRicovero(paziente);
          return ricovero;
        }
      }
    }
    ricovero.setN_id(-1L);
    return ricovero;
  }

  public Ricovero cercaRicovero(Long id) {
    Ricovero ricovero = new Ricovero();
    ricovero.setN_id(-1L);
    return ricoveroMapper.findById(id).orElse(ricovero);
  }

  public RicoveroResponseDto response(Ricovero ricovero) {
    return RicoveroCast.castRicovero(ricovero);
  }
}
