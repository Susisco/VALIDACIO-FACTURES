// src/main/java/cat/ajterrassa/validaciofactures/controller/FacturaController.java
package cat.ajterrassa.validaciofactures.controller;

import cat.ajterrassa.validaciofactures.dto.AutoRelateDTO;
import cat.ajterrassa.validaciofactures.dto.FacturaDto;
import cat.ajterrassa.validaciofactures.dto.SelectionDTO;
import cat.ajterrassa.validaciofactures.dto.SuggerimentsDTO;
import cat.ajterrassa.validaciofactures.model.Albara;
import cat.ajterrassa.validaciofactures.model.Factura;
import cat.ajterrassa.validaciofactures.service.FacturaService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/factures")
public class FacturaController {

    @Autowired
    private final FacturaService service;

    // Constructor injection for FacturaService
    /**
     * Controlador de la classe FacturaController que gestiona les operacions
     * relacionades amb les factures. Aquesta classe actua com a intermediària
     * entre la capa de servei (FacturaService) i les peticions que arriben al
     * backend. S'encarrega de rebre les sol·licituds, delegar la lògica de
     * negoci al servei corresponent i retornar les respostes adequades.
     *
     * @param service Instància de FacturaService que proporciona les
     * funcionalitats necessàries per gestionar les operacions amb les factures.
     */
    public FacturaController(FacturaService service) {
        this.service = service;
    }

    // endpoints CRUD per a la classe Factura
    @GetMapping
    public ResponseEntity<List<Factura>> all() {
        return ResponseEntity.ok(service.findAll());
    }

    /*
    @GetMapping("/{id}")
    public ResponseEntity<Factura> one(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }*/
    @GetMapping("/{id}")
    public ResponseEntity<FacturaDto> one(@PathVariable Long id) {
        Factura factura = service.findById(id);
        FacturaDto dto = service.toDto(factura);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<Factura> create(@RequestBody FacturaDto dto, Principal principal) {
        String email = principal.getName(); // ve del token
        Factura factura = service.save(dto, email); // ✅ Correcte: el servei fa la lògica i guarda
        return ResponseEntity.status(HttpStatus.CREATED).body(factura);
    }



    @PutMapping("/{id}")
    public ResponseEntity<Factura> update(@PathVariable Long id, @RequestBody FacturaDto dto) {
        return ResponseEntity.ok(service.updateFacturaAmbDetalls(id, dto));
    }    
    
    @PutMapping("/{id}/factura")
    public ResponseEntity<Factura> updateFactura(@PathVariable Long id, @RequestBody FacturaDto dto) {
        return ResponseEntity.ok(service.updateFactura(id, dto));
    }

    /**
     * Elimina una factura per la seva ID.
     *
     * @param id L'ID de la factura a eliminar.
     * @return Una resposta HTTP sense contingut (204 No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * **********************************************************************
     */
    //IMPLEMENTEM MACH, RELACIÓ DE FACTURES I ALBARANS
    /**
     * **********************************************************************
     */
    @GetMapping("/{id}/auto-relate")
    public ResponseEntity<AutoRelateDTO> autoRelate(@PathVariable Long id) {
        return ResponseEntity.ok(service.autoRelate(id));
    }

    @GetMapping("/{id}/albarans-candidats")
    public ResponseEntity<List<Albara>> getAlbaransCandidats(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAlbaransCandidats(id));
    }

    @PutMapping("/{id}/afegir-albara/{albaraId}")
    public ResponseEntity<Factura> afegirAlbara(@PathVariable Long id, @PathVariable Long albaraId) {
        return ResponseEntity.ok(service.afegirAlbaraManualment(id, albaraId));
    }

    @DeleteMapping("/{facturaId}/treure-albara/{albaraId}")
    public ResponseEntity<Void> desvincularAlbara(
            @PathVariable Long facturaId,
            @PathVariable Long albaraId) {
        service.desvincularAlbara(facturaId, albaraId);
        return ResponseEntity.noContent().build();
    }

 
}
