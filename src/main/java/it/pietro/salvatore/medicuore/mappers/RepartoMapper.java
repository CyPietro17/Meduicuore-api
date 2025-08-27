package it.pietro.salvatore.medicuore.mappers;

import it.pietro.salvatore.medicuore.entity.Reparto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RepartoMapper {

  void save(Reparto reparto);

  List<Reparto> findAll();

  Optional<Reparto> findById(Long id);

  Optional<Reparto> findByName(String nome);

  void update(Long id, @Param("reparto") Reparto reparto);
}
