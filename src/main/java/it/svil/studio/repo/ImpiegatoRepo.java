package it.svil.studio.repo;

import it.svil.studio.entity.Impiegato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImpiegatoRepo extends JpaRepository<Impiegato, Long> {
}
