package salesMaster.dao.reposetories;


import org.springframework.data.jpa.repository.JpaRepository;

import salesMaster.dao.entities.Client;
import salesMaster.dao.entities.Utilisateur;

import java.util.List;

public interface IGestionClient extends JpaRepository<Client, Long>{
	List<Client> findByUtilisateur(Utilisateur utilisateur);
}
