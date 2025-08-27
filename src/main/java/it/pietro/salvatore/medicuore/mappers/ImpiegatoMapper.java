package it.pietro.salvatore.medicuore.mappers;

import it.pietro.salvatore.medicuore.dto.request.ImpiegatoRequestDto;
import it.pietro.salvatore.medicuore.entity.Impiegato;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ImpiegatoMapper {

  void save(Impiegato impiegato);

  Optional<Impiegato> findById(Long id);

  List<Impiegato> findAll();

  List<Impiegato> findAllDimessi();

  List<Impiegato> findAllAssunti();

  void update(Long id, @Param("impiegato") Impiegato impiegato);

  List<Impiegato> filterImpiegatiDimessi(@Param("impiegato") ImpiegatoRequestDto requestDto);

  List<Impiegato> filterImpiegatiAssunti(@Param("impiegato") ImpiegatoRequestDto requestDto);
}
