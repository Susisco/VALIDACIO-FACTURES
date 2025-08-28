package cat.ajterrassa.validaciofactures.controller;

import cat.ajterrassa.validaciofactures.model.Edifici;
import cat.ajterrassa.validaciofactures.service.EdificiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edificis")
@CrossOrigin(origins = "${cors.allowed.origin}")// permet accés al frontend, configurat el port a application.properties 
public class EdificiController {
    private final EdificiService service;

    public EdificiController(EdificiService service) {
        this.service = service;
    }

    @GetMapping
    public List<Edifici> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Edifici> getById(@PathVariable Long id) {
        return service.findById(id)
                      .map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Edifici create(@RequestBody Edifici e) {
        return service.save(e);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Edifici> update(@PathVariable Long id, @RequestBody Edifici e) {
        return service.findById(id).map(existing -> {
            e.setId(id);
            return ResponseEntity.ok(service.save(e));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
