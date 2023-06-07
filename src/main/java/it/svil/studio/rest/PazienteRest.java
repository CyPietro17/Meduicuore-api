package it.svil.studio.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.svil.studio.dto.PazienteRequestDto;
import it.svil.studio.dto.PazienteResponseDto;
import it.svil.studio.entity.Paziente;
import it.svil.studio.service.PazienteService;
import it.svil.studio.util.StatoRicovero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/pazienti")
//@CrossOrigin(origins = "http://localhost:4200/pazienti")
@Tag(name = "Pazienti", description = "Pazienti API")
public class PazienteRest {

    private PazienteService pazienteService;

    @Autowired
    public PazienteRest(PazienteService pazienteService) {
        this.pazienteService = pazienteService;
    }

    @GetMapping(value = "/tutti")
    @Operation(description = "Lista di tutti i pazienti")
    public ResponseEntity<List<PazienteResponseDto>> tuttiPazienti(){
        PazienteResponseDto responseDto;
        List<PazienteResponseDto> responseDtoList = new ArrayList<>();
        for(Paziente p : pazienteService.pazienti()){
            responseDto = pazienteService.response(p);
            responseDtoList.add(responseDto);
        }
        return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
    }

    @GetMapping(value = "/dimessi")
    @Operation(description = "Lista di tutti i pazienti dimessi")
    public ResponseEntity<List<PazienteResponseDto>> pazientiDimessi(){
        PazienteResponseDto responseDto;
        List<PazienteResponseDto> dimessiDtoList = new ArrayList<>();
        for(Paziente p : pazienteService.pazienti()){
            if(p.getB_ricoverato().equals(StatoRicovero.CHIUSO)){
                responseDto = pazienteService.response(p);
                dimessiDtoList.add(responseDto);
            }
        }
        return new ResponseEntity<>(dimessiDtoList, HttpStatus.OK);
    }

    @GetMapping(value = "/ricoverati")
    @Operation(description = "Lista di tutti i pazienti ricoverati")
    public ResponseEntity<List<PazienteResponseDto>> pazientiRicoverati(){
        PazienteResponseDto responseDto;
        List<PazienteResponseDto> ricoveratiDtoList = new ArrayList<>();
        for(Paziente p : pazienteService.pazienti()){
            if(p.getB_ricoverato().equals(StatoRicovero.RICOVERATO)){
                responseDto = pazienteService.response(p);
                ricoveratiDtoList.add(responseDto);
            }
        }
        return new ResponseEntity<>(ricoveratiDtoList, HttpStatus.OK);
    }

    @GetMapping(value = "/cerca")
    @Operation(description = "Cerca paziente da ID")
    public ResponseEntity<PazienteResponseDto> cercaPaziente(@RequestParam(name = "id") Long id){
        PazienteResponseDto responseDto = pazienteService.response(pazienteService.trovaPaziente(id));
        if(responseDto.getN_id() != -1L)
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/modifica")
    @Operation(description = "Modifica campi del paziente")
    public ResponseEntity<PazienteResponseDto> updatePaziente(@RequestParam(name = "id") Long id, @RequestBody PazienteRequestDto requestDto){
        PazienteResponseDto responseDto = pazienteService.response(pazienteService.modifica(id, requestDto));
        if(responseDto.getN_id() != -1L)
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @PutMapping(value = "/nuovo")
    @Operation(description = "Registra nuovo paziente")
    public ResponseEntity<PazienteResponseDto> nuovoPaziente(@RequestBody PazienteRequestDto requestDto){
        PazienteResponseDto responseDto = pazienteService.response(pazienteService.add(requestDto));
        if(responseDto.getN_id() != -1L)
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }
}
