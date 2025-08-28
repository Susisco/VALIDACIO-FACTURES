package cat.ajterrassa.validaciofactures.controller;

import cat.ajterrassa.validaciofactures.model.Proveidor;
import cat.ajterrassa.validaciofactures.service.ProveidorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveidors")
@CrossOrigin(origins = "${cors.allowed.origin}")// permet accés al frontend, configurat el port a application.properties 
public class ProveidorController {
    private final ProveidorService service;

    public ProveidorController(ProveidorService service) {
        this.service = service;
    }


    /* GET /api/proveidors?search=abc
     * Retorna tots els proveidors o, si hi ha el paràmetre de cerca, els que continguin
     * la cadena en el nom comercial o en el nom.
     */
    @GetMapping
    public List<Proveidor> getAll(@RequestParam(required = false) String search) {
        // ara suporta: /api/proveidors o /api/proveidors?search=abc
        return service.findAll(search);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveidor> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Proveidor create(@RequestBody Proveidor p) {
        return service.save(p);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveidor> update(@PathVariable Long id, @RequestBody Proveidor p) {
        return service.findById(id).map(existing -> {
            p.setId(id);
            return ResponseEntity.ok(service.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
