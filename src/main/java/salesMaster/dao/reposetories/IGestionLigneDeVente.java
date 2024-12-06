package salesMaster.dao.reposetories;

import org.springframework.data.jpa.repository.JpaRepository;

import salesMaster.dao.entities.LigneDeVente;

public interface IGestionLigneDeVente extends JpaRepository<LigneDeVente, Integer> {

}
