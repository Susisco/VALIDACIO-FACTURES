package cat.ajterrassa.validaciofactures.controller;

import cat.ajterrassa.validaciofactures.model.HistoricCanvi;
import cat.ajterrassa.validaciofactures.service.HistoricCanviService;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/historic")
public class HistoricCanviController {

    private final HistoricCanviService historicCanviService;

    public HistoricCanviController(HistoricCanviService historicCanviService) {
        this.historicCanviService = historicCanviService;
    }

    @GetMapping("/albara/{id}")
    public List<HistoricCanvi> getHistoricPerAlbara(@PathVariable Long id) {
        return historicCanviService.findByTipusAndDocumentId("Albara", id);
    }
}
