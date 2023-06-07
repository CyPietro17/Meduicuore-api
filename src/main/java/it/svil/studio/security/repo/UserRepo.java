package it.svil.studio.security.repo;

import it.svil.studio.security.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<MyUser, Long> {

    MyUser findByUsername(String username);
}
