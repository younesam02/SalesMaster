package salesMaster.dao.reposetories;

import org.springframework.data.jpa.repository.JpaRepository;

import salesMaster.dao.entities.Produit;

public interface IGestionproduit extends JpaRepository<Produit, Long> {
}
