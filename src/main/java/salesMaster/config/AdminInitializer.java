package salesMaster.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import salesMaster.dao.entities.Utilisateur;
import salesMaster.dao.reposetories.IGestionUtilisateurs;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private IGestionUtilisateurs repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!repository.existsById((long) 1)) {
            Utilisateur admin = new Utilisateur();
            admin.setNom("Admin");
            admin.setEmail("admin@gmail.com");
            admin.setMotDePasse(passwordEncoder.encode("admin"));
            admin.setRole("ADMIN");
            repository.save(admin);
        }
    }
}

