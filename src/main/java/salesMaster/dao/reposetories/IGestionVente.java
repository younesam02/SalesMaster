package salesMaster.dao.reposetories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import salesMaster.dao.entities.Utilisateur;
import salesMaster.dao.entities.Vente;

public interface IGestionVente extends JpaRepository<Vente, Long>{
	List<Vente> findByUtilisateur(Utilisateur utilisateur);
}
