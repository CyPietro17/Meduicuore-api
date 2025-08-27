package it.pietro.salvatore.medicuore.service;

import it.pietro.salvatore.medicuore.dto.request.ImpiegatoRequestDto;
import it.pietro.salvatore.medicuore.dto.response.ImpiegatoResponseDto;
import it.pietro.salvatore.medicuore.entity.Impiegato;
import it.pietro.salvatore.medicuore.entity.Reparto;
import it.pietro.salvatore.medicuore.mappers.ImpiegatoMapper;
import it.pietro.salvatore.medicuore.utils.GenericUtil;
import it.pietro.salvatore.medicuore.utils.StatoAssunzione;
import it.pietro.salvatore.medicuore.utils.dtocast.ImpiegatoCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImpiegatoService {

  private final ImpiegatoMapper impiegatoMapper;
  private final RepartoService repartoService;

  @Autowired
  public ImpiegatoService(ImpiegatoMapper impiegatoMapper, RepartoService repartoService) {
    this.impiegatoMapper = impiegatoMapper;
    this.repartoService = repartoService;
  }

  public Impiegato add(ImpiegatoRequestDto requestDto) {
    Impiegato impiegato = new Impiegato();
    if (requestDto != null) {
      if (requestDto.getT_nome() != null && !requestDto.getT_nome().isBlank() && requestDto.getT_cognome() != null &&
            !requestDto.getT_cognome().isBlank() && requestDto.getD_dataNascita() != null &&
            requestDto.getT_professione() != null && !requestDto.getT_professione().isBlank() &&
            (requestDto.getT_professione().equals(GenericUtil.MEDICO) ||
               requestDto.getT_professione().equals(GenericUtil.INFERMIERE) ||
               requestDto.getT_professione().equals(GenericUtil.CAPOREPARTO)) && requestDto.getN_reparto() != null &&
            !repartoService.trovaDaId(requestDto.getN_reparto()).equals(repartoService.trovaDaId(-1L))) {
        impiegato.setT_nome(requestDto.getT_nome());
        impiegato.setT_cognome(requestDto.getT_cognome());
        impiegato.setD_dataNascita(requestDto.getD_dataNascita());
        impiegato.setT_codiceFiscale(GenericUtil.setCodiceFiscale(impiegato));
        impiegato.setT_professione(requestDto.getT_professione());
        impiegato.setT_reparto(repartoService.trovaDaId(requestDto.getN_reparto()));
        impiegato.setB_active(StatoAssunzione.ASSUNTO);
        impiegatoMapper.save(impiegato);
        //        return impiegatoMapper.findById(impiegato.getN_id()).orElse(this.trova(-1L));
      }
    } else {
      impiegato.setN_id(-1L);
    }
    return impiegato;
  }

  public Impiegato trova(Long id) {
    Impiegato impiegato = new Impiegato();
    impiegato.setN_id(-1L);
    return impiegatoMapper.findById(id).orElse(impiegato);
  }

  public List<Impiegato> tuttiImpiegati() {
    return impiegatoMapper.findAll();
  }

  public List<Impiegato> assunti() {
    return impiegatoMapper.findAllAssunti();
  }

  public List<Impiegato> dimessi() {
    return impiegatoMapper.findAllDimessi();
  }

  public Impiegato update(Long id, ImpiegatoRequestDto requestDto) {
    Impiegato impiegato = this.trova(id);
    if (impiegato.getN_id() != -1L) {
      if (requestDto.getT_nome() != null && !requestDto.getT_nome().isBlank()) {
        impiegato.setT_nome(requestDto.getT_nome().trim());
      }
      if (requestDto.getT_cognome() != null && !requestDto.getT_cognome().isBlank()) {
        impiegato.setT_cognome(requestDto.getT_cognome().trim());
      }
      if (requestDto.getD_dataNascita() != null) {
        impiegato.setD_dataNascita(requestDto.getD_dataNascita());
      }
      if (requestDto.getT_professione() != null && !requestDto.getT_professione().isBlank()) {
        impiegato.setT_professione(requestDto.getT_professione().trim());
        if (!requestDto.getT_professione().equals(GenericUtil.MEDICO) &&
              !requestDto.getT_professione().equals(GenericUtil.INFERMIERE) &&
              !requestDto.getT_professione().equals(GenericUtil.CAPOREPARTO)) {
          Impiegato imp = new Impiegato();
          imp.setN_id(-1L);
          return imp;
        }
      }
      if (requestDto.getT_codiceFiscale() != null && !requestDto.getT_codiceFiscale().isBlank()) {
        impiegato.setT_codiceFiscale(requestDto.getT_codiceFiscale().toUpperCase());
      }
      if (requestDto.getN_reparto() != null) {
        impiegato.setT_reparto(repartoService.trovaDaId(requestDto.getN_reparto()));
        if (impiegato.getT_reparto().getN_id() == -1L) {
          Impiegato imp = new Impiegato();
          imp.setN_id(-1L);
          return imp;
        }
      }
      impiegatoMapper.update(id, impiegato);
      return impiegato;
    }
    return impiegato;
  }

  public Impiegato dimissione(Long id) {
    Impiegato impiegato = this.trova(id);
    if (impiegato.getN_id() != -1L) {
      if (impiegato.getB_active().equals(StatoAssunzione.ASSUNTO) && repartoService.trovaDaNome(impiegato.getT_reparto()) != null) {
        Reparto reparto = new Reparto();
        reparto.setN_id(-1L);
        impiegato.setT_reparto(reparto);
        impiegato.setB_active(StatoAssunzione.NON_ASSUNTO);
        impiegatoMapper.update(id, impiegato);
        return impiegato;
      }
      impiegato = new Impiegato();
      impiegato.setN_id(-1L);
      return impiegato;
    }
    return impiegato;
  }

  public List<Impiegato> filterAssunti(ImpiegatoRequestDto requestDto) {
    List<Impiegato> listFilter = new ArrayList<>();
    if ((requestDto.getT_nome() != null || !requestDto.getT_nome().isBlank()) ||
          (requestDto.getT_cognome() != null || !requestDto.getT_cognome().isBlank()) ||
          (requestDto.getT_codiceFiscale() != null || !requestDto.getT_codiceFiscale().isBlank()) ||
          (requestDto.getT_professione() != null || !requestDto.getT_professione().isBlank()) ||
          requestDto.getD_dataNascita() != null) {
      listFilter = impiegatoMapper.filterImpiegatiAssunti(requestDto);
    }

    return listFilter;
  }

  public List<Impiegato> filterDimessi(ImpiegatoRequestDto requestDto) {
    List<Impiegato> listFilter = new ArrayList<>();
    if ((requestDto.getT_nome() != null || !requestDto.getT_nome().isBlank()) ||
          (requestDto.getT_cognome() != null || !requestDto.getT_cognome().isBlank()) ||
          (requestDto.getT_codiceFiscale() != null || !requestDto.getT_codiceFiscale().isBlank()) ||
          (requestDto.getT_professione() != null || !requestDto.getT_professione().isBlank()) ||
          requestDto.getD_dataNascita() != null) {
      listFilter = impiegatoMapper.filterImpiegatiDimessi(requestDto);
    }

    return listFilter;
  }

  public ImpiegatoResponseDto response(Impiegato impiegato) {
    return ImpiegatoCast.castImpiegato(impiegato);
  }
}
