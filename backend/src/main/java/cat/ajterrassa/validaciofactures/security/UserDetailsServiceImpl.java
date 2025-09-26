package cat.ajterrassa.validaciofactures.security;

import cat.ajterrassa.validaciofactures.model.Usuari;
import cat.ajterrassa.validaciofactures.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuariRepository usuariRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuari usuari = usuariRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuari no trobat amb email: " + email));

        if (usuari.getRol() == null) {
            throw new UsernameNotFoundException("L'usuari no té cap rol assignat");
        }

        // ✅ Prefixa el rol amb "ROLE_" per compatibilitat amb Spring Security i
        //    exposa també l'autoritat sense prefix per poder utilitzar hasAuthority
        String roleName = "ROLE_" + usuari.getRol().name();
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(roleName),
                new SimpleGrantedAuthority(usuari.getRol().name()));

        return new User(usuari.getEmail(), usuari.getContrasenya(), authorities);
    }
}
