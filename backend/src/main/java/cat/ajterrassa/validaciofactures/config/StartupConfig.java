package cat.ajterrassa.validaciofactures.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import cat.ajterrassa.validaciofactures.model.Usuari;
import cat.ajterrassa.validaciofactures.model.Proveidor;
import cat.ajterrassa.validaciofactures.service.UsuariService;
import cat.ajterrassa.validaciofactures.service.ProveidorService;

@Component
public class StartupConfig implements CommandLineRunner {

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProveidorService proveidorService;

    @Override
    public void run(String... args) throws Exception {
        // Comprovar si ja existeix un usuari administrador
        if (!usuariService.existsByEmail("admin@admin.com")) {
            Usuari admin = new Usuari();
            admin.setNom("ADMINISTRADOR");
            admin.setEmail("admin@admin.com");
            admin.setContrasenya(passwordEncoder.encode("admin123"));
            admin.setRol(Usuari.Rol.ADMINISTRADOR);
            admin.setContrasenyaTemporal(false);
            usuariService.save(admin);
            System.out.println("Usuari administrador creat: admin@admin.com");
        }

        // Comprovar si ja existeix el proveïdor per defecte
        //si no, es genera
        if (!proveidorService.existsByNif("12345678A")) {
            Proveidor proveidor = new Proveidor();
            proveidor.setNom("GENERIC");
            proveidor.setNomComercial("GENERIC");
            proveidor.setAdreca("Carrer X");
            proveidor.setNif("12345678A");
            proveidorService.save(proveidor);
            System.out.println("Proveïdor per defecte creat: GENERIC");
        }
    }
}
