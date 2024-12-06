package salesMaster.dao.reposetories;

import org.springframework.data.jpa.repository.JpaRepository;

import salesMaster.dao.entities.Facture;

public interface IGestionFacture extends JpaRepository<Facture, Long> {

}
