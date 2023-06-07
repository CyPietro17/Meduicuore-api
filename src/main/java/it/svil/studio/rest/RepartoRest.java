package it.svil.studio.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.svil.studio.dto.RepartoRequestDto;
import it.svil.studio.dto.RepartoResponseDto;
import it.svil.studio.entity.Reparto;
import it.svil.studio.service.RepartoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/reparti")
//@CrossOrigin(origins = "http://localhost:4200/reparti")
@Tag(name = "Reparti", description = "Reparti API")
public class RepartoRest {

    private RepartoService repartoService;

    @Autowired
    public RepartoRest(RepartoService repartoService) {
        this.repartoService = repartoService;
    }

    @PutMapping(value = "/nuovo")
    @Operation(description = "Inserisci nuovo reparto")
    public ResponseEntity<RepartoResponseDto> nuovoReparto(@RequestBody RepartoRequestDto requestDto){
        RepartoResponseDto responseDto = repartoService.response(repartoService.add(requestDto));
        if(responseDto.getN_id() != -1L)
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    @Operation(description = "Lista di tutti i reparti")
    public ResponseEntity<List<RepartoResponseDto>> Reparti(){
        RepartoResponseDto responseDto;
        List<RepartoResponseDto> responseDtoList = new ArrayList<>();
        for(Reparto reparto : repartoService.tuttiReparti()){
            responseDto = repartoService.response(reparto);
            responseDtoList.add(responseDto);
        }
        return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
    }

    @GetMapping(value = "/cerca")
    @Operation(description = "Cerca reparto da ID")
    public ResponseEntity<RepartoResponseDto> cercaReparto(@RequestParam(name = "id") Long id){
        RepartoResponseDto responseDto = repartoService.response(repartoService.trovaDaId(id));
        if(responseDto.getN_id() != -1L)
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }
}
