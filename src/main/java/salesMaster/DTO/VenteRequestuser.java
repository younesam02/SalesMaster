package salesMaster.DTO;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenteRequestuser {
    private Long clientId; // Ensure this matches the clientId in the request payload
    private double total;
    private Date dateVente; // Ensure this is formatted correctly or use LocalDate if appropriate
}