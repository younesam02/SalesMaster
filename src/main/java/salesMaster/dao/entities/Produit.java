package salesMaster.dao.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long produitId;
    private String nom;
    private String description;
    private Double prix;
    private Integer quantiteEnStock;
    private String image;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("produit")  // Prevent recursion in LigneDeVente
    private List<LigneDeVente> lignesDeVente;

    @ManyToOne
    @JoinColumn(name="utilisateurId")
    @JsonIgnoreProperties("produits")  // Prevent recursion in Utilisateur
    private Utilisateur utilisateur;
}
