package salesMaster.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneDeVenteRequest {
    private Long produitId;
    private Integer quantite;
    private Double prixUnitaire;
    private Long venteId;
}
