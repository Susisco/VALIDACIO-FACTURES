package cat.ajterrassa.validaciofactures.service;

import cat.ajterrassa.validaciofactures.model.Usuari;
import cat.ajterrassa.validaciofactures.repository.UsuariRepository;
import cat.ajterrassa.validaciofactures.util.PasswordUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UsuariService {

    private final UsuariRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private EmailService emailService;

    public UsuariService(UsuariRepository repo) {
        this.repo = repo;
    }

    public List<Usuari> findAll() {
        return repo.findAll();
    }

    public Optional<Usuari> findById(Long id) {
        return repo.findById(id);
    }

    public Usuari save(Usuari u) {
        // aquí podríem encryptar contrasenya, etc.
        return repo.save(u);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Usuari findByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }

    // ✅ Quan es crea un nou usuari (amb contrasenya temporal i email)
    public Usuari crearUsuari(Usuari usuari) {
        String contrasenyaTemporal = passwordUtil.generateRandomPassword();
        String hashedPassword = passwordEncoder.encode(contrasenyaTemporal);

        usuari.setContrasenya(hashedPassword);
        usuari.setContrasenyaTemporal(true);

        Usuari saved = usuariRepository.save(usuari);
        emailService.sendPasswordEmail(usuari.getEmail(), contrasenyaTemporal);
        return saved;
    }

    public void resetPassword(Long usuariId) {
        Usuari usuari = usuariRepository.findById(usuariId)
                .orElseThrow(() -> new NoSuchElementException("Usuari no trobat"));

        String novaContrasenya = passwordUtil.generateRandomPassword();
        System.out.println("Nova contrasenya generada: " + usuari.getEmail() + ": " + novaContrasenya);
        usuari.setContrasenya(passwordEncoder.encode(novaContrasenya));
        usuari.setContrasenyaTemporal(true);

        usuariRepository.save(usuari);
        emailService.sendPasswordEmail(usuari.getEmail(), novaContrasenya);
    }

    public Usuari updateUsuari(Long id, Usuari u) {
        Usuari existing = usuariRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuari no trobat"));

        existing.setNom(u.getNom());
        existing.setEmail(u.getEmail());
        existing.setRol(u.getRol());

        if (u.getContrasenya() != null && !u.getContrasenya().isBlank()) {
            existing.setContrasenya(passwordEncoder.encode(u.getContrasenya()));
            existing.setContrasenyaTemporal(false);
        }

        return usuariRepository.save(existing);
    }


public Usuari getUsuariAutenticat() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
        String email = auth.getName();
        return usuariRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuari no trobat amb email: " + email));
    }
    throw new RuntimeException("No s'ha pogut obtenir l'usuari autenticat.");
}



  // Comprovar si existeix un usuari pel correu electrònic
    public boolean existsByEmail(String email) {
        return usuariRepository.findByEmail(email).isPresent();
    }


}
