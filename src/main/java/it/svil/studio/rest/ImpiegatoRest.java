package it.svil.studio.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.svil.studio.dto.ImpiegatoRequestDto;
import it.svil.studio.dto.ImpiegatoResponseDto;
import it.svil.studio.entity.Impiegato;
import it.svil.studio.service.ImpiegatoService;
import it.svil.studio.service.RepartoService;
import it.svil.studio.util.GenericUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/impiegati")
//@CrossOrigin(origins = "http://localhost:4200/impiegati")
@Tag(name = "Impiegati", description = "Impiegati API")
public class ImpiegatoRest {

    private ImpiegatoService impiegatoService;
    private RepartoService repartoService;

    @Autowired
    public ImpiegatoRest(ImpiegatoService impiegatoService, RepartoService repartoService) {
        this.impiegatoService = impiegatoService;
        this.repartoService = repartoService;
    }

    @GetMapping(value = "/cerca")
    @Operation(description = "Cerca impiegato da ID")
    public ResponseEntity<ImpiegatoResponseDto> cercaImpiegato(@RequestParam(name = "id") Long id){
        ImpiegatoResponseDto responseDto = impiegatoService.response(impiegatoService.trova(id));
        if(responseDto.getN_id() != -1L)
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @PutMapping(value = "/aggiungi")
    @Operation(description = "Aggiungi nuovo impiegato")
    public ResponseEntity<ImpiegatoResponseDto> aggiungiImp(@RequestBody ImpiegatoRequestDto requestDto){
        ImpiegatoResponseDto responseDto = impiegatoService.response(impiegatoService.add(requestDto));
        if(responseDto.getN_id() != -1L)
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/assunti")
    @Operation(description = "Lista di impiegati assunti")
    public ResponseEntity<List<ImpiegatoResponseDto>> impiegatiAssunti(){
        ImpiegatoResponseDto responseDto;
        List<ImpiegatoResponseDto> assuntiDto = new ArrayList<>();
        for(Impiegato impiegato : impiegatoService.assunti()){
            responseDto = impiegatoService.response(impiegato);
            assuntiDto.add(responseDto);
        }
        return new ResponseEntity<>(assuntiDto, HttpStatus.OK);
    }

    @GetMapping(value = "/dimessi")
    @Operation(description = "Lista di impiegati dimessi")
    public ResponseEntity<List<ImpiegatoResponseDto>> impiegatiDimessi(){
        ImpiegatoResponseDto responseDto;
        List<ImpiegatoResponseDto> dimessiDto = new ArrayList<>();
        for(Impiegato impiegato : impiegatoService.dimessi()){
            responseDto = impiegatoService.response(impiegato);
            dimessiDto.add(responseDto);
        }
        return new ResponseEntity<>(dimessiDto, HttpStatus.OK);
    }

    @GetMapping(value = "/tutti")
    @Operation(description = "Lista di tutti gli impiegati assunti  e dimessi")
    public ResponseEntity<List<ImpiegatoResponseDto>> impiegati(){
        ImpiegatoResponseDto responseDto;
        List<ImpiegatoResponseDto> listDto = new ArrayList<>();
        for(Impiegato impiegato : impiegatoService.tuttiImpiegati()){
            responseDto = impiegatoService.response(impiegato);
            listDto.add(responseDto);
        }
        return new ResponseEntity<>(listDto, HttpStatus.OK);
    }

    @GetMapping(value = "/meidici/assunti")
    @Operation(description = "Lista di medici assunti")
    public ResponseEntity<List<ImpiegatoResponseDto>> mediciAssunti(){
        ImpiegatoResponseDto responseDto;
        List<ImpiegatoResponseDto> mediciAssuntiDto = new ArrayList<>();
        for(Impiegato impiegato : impiegatoService.assunti()){
            if(impiegato.getT_professione().equals(GenericUtil.MEDICO)){
                responseDto = impiegatoService.response(impiegato);
                mediciAssuntiDto.add(responseDto);
            }
        }
        return new ResponseEntity<>(mediciAssuntiDto, HttpStatus.OK);
    }

    @GetMapping(value = "/meidici/dimessi")
    @Operation(description = "Lista di medici dimessi")
    public ResponseEntity<List<ImpiegatoResponseDto>> medici(){
        ImpiegatoResponseDto responseDto;
        List<ImpiegatoResponseDto> mediciDimessiDto = new ArrayList<>();
        for(Impiegato impiegato : impiegatoService.dimessi()){
            if(impiegato.getT_professione().equals(GenericUtil.MEDICO)){
                responseDto = impiegatoService.response(impiegato);
                mediciDimessiDto.add(responseDto);
            }
        }
        return new ResponseEntity<>(mediciDimessiDto, HttpStatus.OK);
    }

    @GetMapping(value = "/infermieri/assunti")
    @Operation(description = "Lista di infermieri assunti")
    public ResponseEntity<List<ImpiegatoResponseDto>> InfermieriAssunti(){
        ImpiegatoResponseDto responseDto;
        List<ImpiegatoResponseDto> infermieriAssuntiDto = new ArrayList<>();
        for(Impiegato impiegato : impiegatoService.assunti()){
            if(impiegato.getT_professione().equals(GenericUtil.INFERMIERE)){
                responseDto = impiegatoService.response(impiegato);
                infermieriAssuntiDto.add(responseDto);
            }
        }
        return new ResponseEntity<>(infermieriAssuntiDto, HttpStatus.OK);
    }

    @GetMapping(value = "/infermieri/dimessi")
    @Operation(description = "Lista di infermieri dimessi")
    public ResponseEntity<List<ImpiegatoResponseDto>> InfermieriDimessi(){
        ImpiegatoResponseDto responseDto;
        List<ImpiegatoResponseDto> infermieriDimessiDto = new ArrayList<>();
        for(Impiegato impiegato : impiegatoService.dimessi()){
            if(impiegato.getT_professione().equals(GenericUtil.INFERMIERE)){
                responseDto = impiegatoService.response(impiegato);
                infermieriDimessiDto.add(responseDto);
            }
        }
        return new ResponseEntity<>(infermieriDimessiDto, HttpStatus.OK);
    }

    @GetMapping(value = "/caporeparti/dimessi")
    @Operation(description = "Lista di capi reparto dimessi")
    public ResponseEntity<List<ImpiegatoResponseDto>> CapoRepDimessi(){
        ImpiegatoResponseDto responseDto;
        List<ImpiegatoResponseDto> CapoRepDimessiDto = new ArrayList<>();
        for(Impiegato impiegato : impiegatoService.dimessi()){
            if(impiegato.getT_professione().equals(GenericUtil.CAPOREPARTO)){
                responseDto = impiegatoService.response(impiegato);
                CapoRepDimessiDto.add(responseDto);
            }
        }
        return new ResponseEntity<>(CapoRepDimessiDto, HttpStatus.OK);
    }

    @GetMapping(value = "/caporeparti/assunti")
    @Operation(description = "Lista di capi reparto assunti")
    public ResponseEntity<List<ImpiegatoResponseDto>> CapoRepAssunti(){
        ImpiegatoResponseDto responseDto;
        List<ImpiegatoResponseDto> CapoRepAssuntiDto = new ArrayList<>();
        for(Impiegato impiegato : impiegatoService.assunti()){
            if(impiegato.getT_professione().equals(GenericUtil.CAPOREPARTO)){
                responseDto = impiegatoService.response(impiegato);
                CapoRepAssuntiDto.add(responseDto);
            }
        }
        return new ResponseEntity<>(CapoRepAssuntiDto, HttpStatus.OK);
    }

    @GetMapping(value = "/reparto/{id}")
    @Operation(description = "Lista di impiegati afferenti al reparto x")
    public ResponseEntity<List<ImpiegatoResponseDto>> impiegatiReparto(@PathVariable Long id){
        List<ImpiegatoResponseDto> impiegatiRepartoDto = new ArrayList<>();
        if(repartoService.trovaDaId(id).getT_nome() != null){
            ImpiegatoResponseDto responseDto;
            for(Impiegato impiegato: impiegatoService.assunti()){
                if(impiegato.getT_reparto().getN_id().equals(repartoService.trovaDaId(id).getN_id())){
                    responseDto = impiegatoService.response(impiegato);
                    impiegatiRepartoDto.add(responseDto);
                }
            }
            return new ResponseEntity<>(impiegatiRepartoDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(impiegatiRepartoDto, HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/modifica")
    @Operation(description = "Modifica campi impiegato")
    public ResponseEntity<ImpiegatoResponseDto> modificaImp(@RequestParam(name = "id") Long id, @RequestBody ImpiegatoRequestDto requestDto){
        ImpiegatoResponseDto responseDto = impiegatoService.response(impiegatoService.modifica(id, requestDto));
        if(responseDto.getN_id() != -1L)
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "dimissione")
    @Operation(description = "Cancellazione logica impiegato, da assunto a dimesso")
    public ResponseEntity<ImpiegatoResponseDto> dimissioneImp(@RequestParam(name = "id") Long id){
        ImpiegatoResponseDto responseDto = impiegatoService.response(impiegatoService.dimissione(id));
        if(responseDto.getN_id() != -1L)
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }
}


