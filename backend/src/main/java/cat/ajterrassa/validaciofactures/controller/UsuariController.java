package cat.ajterrassa.validaciofactures.controller;

import cat.ajterrassa.validaciofactures.dto.UsuariDto;
import cat.ajterrassa.validaciofactures.dto.UsuariSimpleDto;
import cat.ajterrassa.validaciofactures.model.Usuari;
import cat.ajterrassa.validaciofactures.service.UsuariService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/usuaris")
@CrossOrigin(origins = "${cors.allowed.origin}")// permet accés al frontend, configurat el port a application.properties 
public class UsuariController {

    private final UsuariService service;

    @Autowired
    private UsuariService usuariService;

    public UsuariController(UsuariService service) {
        this.service = service;
    }

    @GetMapping
    public List<Usuari> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuari> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /*
     * @PostMapping
     * public Usuari create(@RequestBody Usuari u) {
     * return service.save(u);
     * }
     */
    private void applyDtoToEntity(UsuariDto dto, Usuari u) {
        u.setNom(dto.getNom());
        u.setEmail(dto.getEmail());
        u.setContrasenya(dto.getContrasenya());
        u.setRol(Usuari.Rol.valueOf(dto.getRol())); // <-- Enum correcte
    }

    @PostMapping
    public ResponseEntity<Usuari> create(@RequestBody UsuariDto dto) {
        Usuari u = new Usuari();
        applyDtoToEntity(dto, u);
        Usuari saved = usuariService.crearUsuari(u); // Assegura’t de cridar el mètode que encripta!
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuari> update(@PathVariable Long id, @RequestBody Usuari u) {
        try {
            Usuari updated = service.updateUsuari(id, u);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

// Endpoint per obtenir l'usuari actual, retornant el seu nom i id
    @GetMapping("/me")
    public ResponseEntity<UsuariSimpleDto> getMe(Authentication auth) {
        String email = auth.getName();
        Usuari usuari = usuariService.findByEmail(email);
        if (usuari == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(new UsuariSimpleDto(usuari));
    }

// Endpoint per canviar la contrasenya de l'usuari actual
    @PostMapping("/reset-password/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> resetPassword(@PathVariable Long id) {
        try {
            usuariService.resetPassword(id);
            return ResponseEntity.ok("Contrasenya restablerta i enviada per correu.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error en restablir la contrasenya.");
        }
    }

}
