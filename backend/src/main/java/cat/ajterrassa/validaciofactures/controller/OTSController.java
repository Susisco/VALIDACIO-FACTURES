package cat.ajterrassa.validaciofactures.controller;

import cat.ajterrassa.validaciofactures.model.OTS;
import cat.ajterrassa.validaciofactures.service.OTSService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ots")
@CrossOrigin(origins = "${cors.allowed.origin}")// permet accés al frontend, configurat el port a application.properties 
public class OTSController {
    private final OTSService service;

    public OTSController(OTSService service) {
        this.service = service;
    }

    @GetMapping
    public List<OTS> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OTS> getById(@PathVariable Long id) {
        return service.findById(id)
                      .map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public OTS create(@RequestBody OTS ots) {
        return service.save(ots);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OTS> update(@PathVariable Long id, @RequestBody OTS ots) {
        return service.findById(id).map(existing -> {
            ots.setId(id);
            return ResponseEntity.ok(service.save(ots));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
