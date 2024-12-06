package salesMaster.presentation;


import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import salesMaster.dao.entities.Utilisateur;
import salesMaster.dao.reposetories.IGestionUtilisateurs;

@RestController
public class RegistrationController {

    @Autowired
    private IGestionUtilisateurs myUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IGestionUtilisateurs repository;
    

    @PostMapping("/register/user")
    public Utilisateur createUser(@RequestBody Utilisateur user) {
        user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
        user.setRole("USER");
        return myUserRepository.save(user);
    }
    @GetMapping("/checkEmail")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam("email") String email) {
        boolean emailExists = myUserRepository.existsByEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", emailExists);
        System.out.println("Checking email: " + email + " - Exists: " + emailExists);
        return ResponseEntity.ok(response);
    }


    
}
