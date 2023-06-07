package it.svil.studio.repo;

import it.svil.studio.entity.Paziente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PazienteRepo extends JpaRepository<Paziente, Long> {
}
