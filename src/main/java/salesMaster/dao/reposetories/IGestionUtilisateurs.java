package salesMaster.dao.reposetories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import salesMaster.dao.entities.Utilisateur;

public interface IGestionUtilisateurs extends JpaRepository<Utilisateur, Long>{
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
}
