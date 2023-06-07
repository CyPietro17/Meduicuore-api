package it.svil.studio.repo;

import it.svil.studio.entity.Ricovero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RicoveroRepo extends JpaRepository<Ricovero, Long> {
}
