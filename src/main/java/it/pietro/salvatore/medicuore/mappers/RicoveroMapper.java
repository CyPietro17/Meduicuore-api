package it.pietro.salvatore.medicuore.mappers;

import it.pietro.salvatore.medicuore.entity.Ricovero;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RicoveroMapper {

  List<Ricovero> findAll();

  void save(Ricovero ricovero);

  Optional<Ricovero> findById(Long id);

  void update(Long id, @Param("ricovero") Ricovero ricovero);
}
