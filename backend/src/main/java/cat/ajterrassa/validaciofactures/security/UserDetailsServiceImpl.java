package cat.ajterrassa.validaciofactures.security;

import cat.ajterrassa.validaciofactures.model.Usuari;
import cat.ajterrassa.validaciofactures.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuariRepository usuariRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuari usuari = usuariRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuari no trobat amb email: " + email));

        // ✅ Prefixa el rol amb "ROLE_" per compatibilitat amb Spring Security
        String roleName = "ROLE_" + usuari.getRol().name();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);

        if (usuari.getRol() == null) {
            throw new UsernameNotFoundException("L'usuari no té cap rol assignat");
        }

        return new User(usuari.getEmail(), usuari.getContrasenya(), Collections.singletonList(authority));
    }
}
