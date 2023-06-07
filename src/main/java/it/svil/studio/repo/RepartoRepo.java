package it.svil.studio.repo;

import it.svil.studio.entity.Reparto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepartoRepo extends JpaRepository<Reparto, Long> {
}
