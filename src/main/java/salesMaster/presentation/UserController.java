package salesMaster.presentation;

import java.io.File;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.io.IOException;
import jakarta.persistence.EntityNotFoundException;
import salesMaster.DTO.LigneDeVenteRequest;
import salesMaster.DTO.LigneDeVenteRequestuser;
import salesMaster.DTO.VenteRequest;
import salesMaster.DTO.VenteRequestuser;
import salesMaster.dao.entities.Client;
import salesMaster.dao.entities.Facture;
import salesMaster.dao.entities.LigneDeVente;
import salesMaster.dao.entities.Produit;
import salesMaster.dao.entities.Utilisateur;
import salesMaster.dao.entities.Vente;
import salesMaster.dao.reposetories.IGestionFacture;
import salesMaster.dao.reposetories.IGestionLigneDeVente;
import salesMaster.dao.reposetories.IGestionUtilisateurs;
import salesMaster.dao.reposetories.IGestionVente;
import salesMaster.dao.reposetories.IGestionproduit;
import salesMaster.service.Iservice.FactureService;
import salesMaster.service.Iservice.IserviceClient;
import salesMaster.webtoken.JwtService;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
    private IserviceClient clientService;
	
	@Autowired
    private IGestionUtilisateurs repository;
	
    @Autowired
    private FactureService factureService;
    
    @Autowired
    private IGestionproduit prodrepository;
    
    @Autowired
    private IGestionVente venterepository;
    
    @Autowired
    private IGestionLigneDeVente ligneventerepository;
    
    @Autowired
    private JwtService jwtUtil;
    
    @Autowired
    private IGestionFacture facturerepository;
    
    @GetMapping("/products")
    public List<Produit> getAllProducts() {
        return prodrepository.findAll();
    }
    @GetMapping("/clients")
    public List<Client> getAllClients(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);

        // Extract email from the JWT
        String email = jwtUtil.extractUsername(jwt);

        // Find the user by their email
        Utilisateur user = repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));
        return clientService.getClientsByUtilisateur(user);
    }
    // Add new client
    @PostMapping("/addClient")
    public ResponseEntity<Client> addClient(@RequestBody Client client, @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);

        // Extract email from the JWT
        String email = jwtUtil.extractUsername(jwt);

        // Find the user by their email
        Utilisateur user = repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));

        // Associate the user ID with the client
        client.setUtilisateur(user); // Assuming you have a 'utilisateur' field in the Client entity

        // Save the client
        Client savedClient = clientService.addCl(client);

        return ResponseEntity.ok(savedClient);
    }


    // Update existing client
    @PutMapping("/updateClient/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client updatedClient) {
        try {
            return ResponseEntity.ok(clientService.updateCl(id, updatedClient));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete client
    @DeleteMapping("/deleteClient/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteCl(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/addVente")
    public ResponseEntity<Vente> addVente(@RequestBody VenteRequestuser request, @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String email = jwtUtil.extractUsername(jwt);
            Utilisateur user = repository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));

            Vente vente = new Vente();
            vente.setDateVente(new Date());
            vente.setTotal(request.getTotal());
            vente.setClient(clientService.getClientById(request.getClientId()).orElse(null));
            vente.setUtilisateur(user);

            // Save the Vente object
            
            vente = venterepository.save(vente);

            // Ensure the venteID is populated
            if (vente.getVenteID() == null) {
                throw new IllegalStateException("Vente ID was not generated.");
            }

            return ResponseEntity.ok(vente);  // This should return the Vente with its ID populated
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/addLigneDeVente")
    public ResponseEntity<LigneDeVente> addLigneDeVente(@RequestBody LigneDeVenteRequestuser request) {
        try {
            LigneDeVente ligneDeVente = new LigneDeVente();
            ligneDeVente.setQuantite(request.getQuantite());
            ligneDeVente.setPrixUnitaire(request.getPrixUnitaire());
            ligneDeVente.setProduit(prodrepository.findById(request.getProduitId()).orElse(null));
            ligneDeVente.setVente(venterepository.findById(request.getVenteId()).orElse(null));
            
            ligneDeVente = ligneventerepository.save(ligneDeVente);

            return ResponseEntity.ok(ligneDeVente);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/ventes")
    public List<Vente> getAllVentes(@RequestHeader("Authorization") String token) {
    	  String jwt = token.substring(7);

          // Extract email from the JWT
          String email = jwtUtil.extractUsername(jwt);

          // Find the user by their email
          Utilisateur user = repository.findByEmail(email)
                  .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));
          return venterepository.findByUtilisateur(user);
    }
    @PostMapping("/generateFacture/{venteId}")
    public ResponseEntity<Facture> generateFacture(@PathVariable Long venteId) {
        Facture facture = factureService.generateFacture(venteId);
        return ResponseEntity.ok(facture);
    }
    @GetMapping("/downloadFacture/{factureId}")
    public ResponseEntity<?> downloadFacture(@PathVariable Long factureId) throws java.io.IOException {
        // Récupérer la facture
        Facture facture = facturerepository.findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture non trouvée"));

        // Obtenir le chemin du fichier PDF
        String pdfPath = facture.getPdf();
        File pdfFile = new File(pdfPath);

        if (!pdfFile.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Le fichier PDF de la facture n'est pas disponible.");
        }

        try {
            // Lire le fichier PDF
            byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());

            // Retourner le fichier PDF dans la réponse avec le bon type de contenu
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "facture_" + factureId + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la lecture du fichier PDF.");
        }
    }
    @DeleteMapping("/deleteVente/{id}")
    public ResponseEntity<Void> deletevente(@PathVariable Long id) {
    	venterepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
