package salesMaster.presentation;

import java.io.File;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.io.IOException;
import jakarta.persistence.EntityNotFoundException;
import salesMaster.DTO.LigneDeVenteRequest;
import salesMaster.DTO.VenteRequest;
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

@RestController
@RequestMapping("/admin")
public class AdminController {
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
    private IGestionFacture facturerepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public List<Utilisateur> getAllUsers() {
        return repository.findAll();
    }

    @PostMapping("/adduser") 
    public Utilisateur createUser(@RequestBody Utilisateur utilisateur) {
    	utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
    	utilisateur.setRole("USER");
        return repository.save(utilisateur);
    }

    @GetMapping("/getuser/{id}")
    public ResponseEntity<Utilisateur> getUserById(@PathVariable Long id) {
        Optional<Utilisateur> user = repository.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/updateuser/{id}")
    public ResponseEntity<Utilisateur> updateUser(@PathVariable Long id, @RequestBody Utilisateur updatedUser) {
        Optional<Utilisateur> existingUser = repository.findById(id);
        if (existingUser.isPresent()) {
            Utilisateur user = existingUser.get();
            user.setNom(updatedUser.getNom());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());
            Utilisateur savedUser = repository.save(user);
            return ResponseEntity.ok(savedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<Utilisateur> user = repository.findById(id);
        if (user.isPresent()) {
            repository.delete(user.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/products")
    public List<Produit> getAllProducts() {
        return prodrepository.findAll();
    }

    @PostMapping("/addProduct") 
    public Produit createProduct(@RequestBody Produit produit) {
    	
        return prodrepository.save(produit);
    }
    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Optional<Produit> produit = prodrepository.findById(id);
        if (produit.isPresent()) {
        	prodrepository.delete(produit.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/updateProduct/{id}")
    public ResponseEntity<Produit> updateProduct(@PathVariable Long id, @RequestBody Produit updatedProduit) {
        Optional<Produit> existingProduct = prodrepository.findById(id);
        if (existingProduct.isPresent()) {
        	Produit produit = existingProduct.get();
        	produit.setNom(updatedProduit.getNom());
        	produit.setDescription(updatedProduit.getDescription());
        	produit.setPrix(updatedProduit.getPrix());
        	produit.setImage(updatedProduit.getImage());
        	produit.setQuantiteEnStock(updatedProduit.getQuantiteEnStock());
            Produit savedProduct = prodrepository.save(produit);
            return ResponseEntity.ok(savedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/clients")
    public List<Client> getAllClients() {
        return clientService.listerCl();
        
    }

    // Add new client
    @PostMapping("/addClient")
    public Client createClient(@RequestBody Client client) {
        return clientService.addCl(client);
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
    public ResponseEntity<Vente> addVente(@RequestBody VenteRequest request) {
        try {
            Vente vente = new Vente();
            vente.setDateVente(new Date());
            vente.setTotal(request.getTotal());
            vente.setClient(clientService.getClientById(request.getClientId()).orElse(null));
            
            vente = venterepository.save(vente);

            return ResponseEntity.ok(vente);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        
        
    }
    @PostMapping("/addLigneDeVente")
    public ResponseEntity<LigneDeVente> addLigneDeVente(@RequestBody LigneDeVenteRequest request) {
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
    public List<Vente> getAllVentes() {
        return venterepository.findAll();
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
    @GetMapping("/checkEmail")
    public ResponseEntity<Boolean> checkEmail(@RequestParam("email") String email) {
        boolean emailExists = repository.existsByEmail(email);
        return ResponseEntity.ok(emailExists);
    }
    
    }



