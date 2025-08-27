package it.pietro.salvatore.medicuore.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pietro.salvatore.medicuore.dto.request.RicoveroRequestDto;
import it.pietro.salvatore.medicuore.dto.response.RicoveroResponseDto;
import it.pietro.salvatore.medicuore.entity.Paziente;
import it.pietro.salvatore.medicuore.entity.Reparto;
import it.pietro.salvatore.medicuore.entity.Ricovero;
import it.pietro.salvatore.medicuore.service.PazienteService;
import it.pietro.salvatore.medicuore.service.RepartoService;
import it.pietro.salvatore.medicuore.service.RicoveroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/ricoveri")
@Tag(name = "Hospitalization", description = "Hospitalization API")
public class RicoveroRest {

  private final RicoveroService ricoveroService;
  private final PazienteService pazienteService;
  private final RepartoService repartoService;

  @Autowired
  public RicoveroRest(RicoveroService ricoveroService, PazienteService pazienteService, RepartoService repartoService) {
    this.ricoveroService = ricoveroService;
    this.pazienteService = pazienteService;
    this.repartoService = repartoService;
  }

  @GetMapping(value = "/cerca")
  @Operation(description = "select hospitalization by ID")
  public ResponseEntity<RicoveroResponseDto> search(@RequestParam(name = "id") Long id) {
    ResponseEntity<RicoveroResponseDto> httpResponse;
    RicoveroResponseDto responseDto = ricoveroService.response(ricoveroService.cercaRicovero(id));
    if (responseDto.getN_id() != -1L) {
      httpResponse = ResponseEntity.ok(responseDto);
    } else {
      httpResponse = ResponseEntity.badRequest().body(responseDto);
    }
    return httpResponse;
  }

  @GetMapping(value = "/paziente/ricoverato")
  @Operation(description = "Cerca paziente ricoverato da ID paziente")
  public ResponseEntity<RicoveroResponseDto> pazienteRicoverato(@RequestParam(name = "id_p") Long idPaziente) {
    ResponseEntity<RicoveroResponseDto> httpResponse = null;
    RicoveroResponseDto responseDto = new RicoveroResponseDto();
    Paziente paziente = pazienteService.trovaPaziente(idPaziente);
    if (paziente.getN_id() != -1L) {
      for (Ricovero ricovero : ricoveroService.tuttiRicoveri()) {
        if (ricovero.getD_fineRicovero() == null && ricovero.getN_paziente().equals(paziente)) {
          responseDto = ricoveroService.response(ricovero);
          httpResponse = ResponseEntity.ok(responseDto);
        }
      }
    }
    if (httpResponse == null) {
      httpResponse = ResponseEntity.badRequest().body(responseDto);
    }
    return httpResponse;
  }

  @GetMapping(value = "/paziente")
  @Operation(description = "Lista di tutti i ricoveri del paziente con ID x")
  public ResponseEntity<List<RicoveroResponseDto>> ricoveriPaziente(@RequestParam(name = "id_p") Long idPaziente) {
    RicoveroResponseDto responseDto;
    List<RicoveroResponseDto> ricoveri = new ArrayList<>();
    Paziente paziente = pazienteService.trovaPaziente(idPaziente);
    if (paziente.getN_id() != -1L) {
      for (Ricovero ricovero : ricoveroService.tuttiRicoveri()) {
        if (ricovero.getN_paziente().equals(paziente)) {
          responseDto = ricoveroService.response(ricovero);
          ricoveri.add(responseDto);
        }
      }
      return new ResponseEntity<>(ricoveri, HttpStatus.OK);
    }
    return new ResponseEntity<>(ricoveri, HttpStatus.BAD_REQUEST);
  }

  @GetMapping(value = "/reparto")
  @Operation(description = "Lista di tutti i ricoveri del reparto con ID x")
  public ResponseEntity<List<RicoveroResponseDto>> ricoveriReparto(@RequestParam(name = "id") Long id) {
    RicoveroResponseDto responseDto;
    List<RicoveroResponseDto> ricoveri = new ArrayList<>();
    Reparto reparto = repartoService.trovaDaId(id);
    if (reparto.getN_id() != -1L) {
      for (Ricovero ricovero : ricoveroService.tuttiRicoveri()) {
        if (ricovero.getT_reparto().equals(reparto)) {
          responseDto = ricoveroService.response(ricovero);
          ricoveri.add(responseDto);
        }
      }
      return new ResponseEntity<>(ricoveri, HttpStatus.OK);
    }
    return new ResponseEntity<>(ricoveri, HttpStatus.BAD_REQUEST);
  }

  @GetMapping(value = "/attivi/reparto/{id}")
  @Operation(description = "Lista di tutti i ricoveri attivi del reparto con ID x")
  public ResponseEntity<List<RicoveroResponseDto>> attriviReparto(@PathVariable Long id) {
    RicoveroResponseDto responseDto;
    List<RicoveroResponseDto> attivi = new ArrayList<>();
    Reparto reparto = repartoService.trovaDaId(id);
    if (reparto.getN_id() != -1L) {
      for (Ricovero ricovero : ricoveroService.tuttiRicoveri()) {
        if (ricovero.getD_fineRicovero() == null && ricovero.getT_reparto().equals(reparto)) {
          responseDto = ricoveroService.response(ricovero);
          attivi.add(responseDto);
        }
      }
      return new ResponseEntity<>(attivi, HttpStatus.OK);
    }
    return new ResponseEntity<>(attivi, HttpStatus.BAD_REQUEST);
  }

  @GetMapping(value = "/attivi")
  @Operation(description = "Lista di tutti i ricoveri attivi")
  public ResponseEntity<List<RicoveroResponseDto>> ricoveriAttivi() {
    RicoveroResponseDto responseDto;
    List<RicoveroResponseDto> attiviDtoList = new ArrayList<>();
    for (Ricovero ricovero : ricoveroService.tuttiRicoveri()) {
      if (ricovero.getD_fineRicovero() == null) {
        responseDto = ricoveroService.response(ricovero);
        attiviDtoList.add(responseDto);
      }
    }
    return new ResponseEntity<>(attiviDtoList, HttpStatus.OK);
  }

  @GetMapping(value = "/chiusi")
  @Operation(description = "Lista di tutti i ricoveri chiusi")
  public ResponseEntity<List<RicoveroResponseDto>> ricoveriChiusi() {
    RicoveroResponseDto responseDto;
    List<RicoveroResponseDto> chiusiDtoList = new ArrayList<>();
    for (Ricovero ricovero : ricoveroService.tuttiRicoveri()) {
      if (ricovero.getD_fineRicovero() != null) {
        responseDto = ricoveroService.response(ricovero);
        chiusiDtoList.add(responseDto);
      }
    }
    return new ResponseEntity<>(chiusiDtoList, HttpStatus.OK);
  }

  @GetMapping(value = "/tutti")
  @Operation(description = "Lista di tutti i ricoveri")
  public ResponseEntity<List<RicoveroResponseDto>> ricoveri() {
    RicoveroResponseDto responseDto;
    List<RicoveroResponseDto> ricoveriDtoList = new ArrayList<>();
    for (Ricovero ricovero : ricoveroService.tuttiRicoveri()) {
      responseDto = ricoveroService.response(ricovero);
      ricoveriDtoList.add(responseDto);
    }
    return new ResponseEntity<>(ricoveriDtoList, HttpStatus.OK);
  }

  @PutMapping(value = "/nuovo")
  @Operation(description = "Apri nuovo ricovero ad un paziente")
  public ResponseEntity<RicoveroResponseDto> nuovoRicovero(@RequestBody RicoveroRequestDto requestDto) {
    RicoveroResponseDto responseDto = ricoveroService.response(ricoveroService.add(requestDto));
    if (responseDto.getN_id() != -1L) {
      return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
  }

  @PostMapping(value = "/chiudi")
  @Operation(description = "Chiudi un ricovero attivo di un paziente")
  public ResponseEntity<RicoveroResponseDto> chiudiRicovero(@RequestBody RicoveroRequestDto requestDto) {
    RicoveroResponseDto responseDto = ricoveroService.response(ricoveroService.chiudiRicovero(requestDto));
    if (responseDto.getN_id() != -1L) {
      return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
  }
}
