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
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ClientID;
    private String nom;
    private String adresse;
    private String email;
    private String telephone;

    @OneToMany(mappedBy = "client", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    @JsonIgnoreProperties("client")  // Prevent recursion in Vente
    private List<Vente> ventes;

    @ManyToOne
    @JoinColumn(name = "utilisateurId")
    @JsonIgnoreProperties("clients")  // Prevent recursion in Utilisateur
    private Utilisateur utilisateur;
}
