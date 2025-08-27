package it.pietro.salvatore.medicuore.mappers;

import it.pietro.salvatore.medicuore.entity.Paziente;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PazienteMapper {

  void save(Paziente paziente);

  List<Paziente> findAll();

  Optional<Paziente> findById(Long id);

  void update(Long id, @Param("paziente") Paziente paziente);
}
