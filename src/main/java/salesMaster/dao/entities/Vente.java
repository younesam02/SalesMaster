package salesMaster.dao.entities;

import java.util.Date;
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
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Vente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long VenteID;
    private Date DateVente;
    private double Total;

    @ManyToOne
    @JoinColumn(name = "ClientID")
    @JsonIgnoreProperties({"ventes", "utilisateur"})  // Prevent recursive fetching
    private Client client;

    @ManyToOne
    @JoinColumn(name = "utilisateurId")
    @JsonIgnoreProperties({"ventes", "clients", "produits"})  // Prevent recursion in Utilisateur
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "vente", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    @JsonIgnoreProperties("vente")  // Prevent recursion in LigneDeVente
    private List<LigneDeVente> lignesDeVente;

    @OneToOne(mappedBy = "vente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("vente")  // Prevent recursion in Facture
    private Facture facture;
}
