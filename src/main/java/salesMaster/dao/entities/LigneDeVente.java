package salesMaster.dao.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LigneDeVente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ligneDeVenteId;
    private Integer quantite;
    private Double prixUnitaire;

    @ManyToOne
    @JoinColumn(name = "VenteID")
    @JsonIgnore  // Prevent recursion
    private Vente vente;

    @ManyToOne
    @JoinColumn(name = "produitId")
    @JsonIgnore  // Prevent recursion
    private Produit produit;
}
