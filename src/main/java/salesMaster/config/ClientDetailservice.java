package salesMaster.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import salesMaster.dao.entities.Utilisateur;
import salesMaster.dao.reposetories.IGestionUtilisateurs;

import java.util.Optional;

@Service
public class ClientDetailservice implements UserDetailsService {

    @Autowired
    private IGestionUtilisateurs repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Utilisateur> user = repository.findByEmail(email);
        if (user.isPresent()) {
            var userObj = user.get();
            return User.builder()
                    .username(userObj.getEmail())
                    .password(userObj.getMotDePasse())
                    .roles(getRoles(userObj))
                    .build();
        } else {
            throw new UsernameNotFoundException(email);
        }
    }

    private String[] getRoles(Utilisateur user) {
        if (user.getRole() == null) {
            return new String[]{"USER"};
        }
        return user.getRole().split(",");
    }
}