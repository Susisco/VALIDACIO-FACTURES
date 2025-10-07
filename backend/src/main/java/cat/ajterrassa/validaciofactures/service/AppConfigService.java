package cat.ajterrassa.validaciofactures.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppConfigService {

    @Value("${app.min.supported.version:1.0.0}")
    private String minSupportedVersion;

    @Value("${app.update.message:Actualitza l'aplicació per continuar utilitzant el servei.}")
    private String updateMessage;

    @Value("${app.update.url:https://play.google.com/store/apps/details?id=com.example.app}")
    private String updateUrl;

    public String getMinSupportedVersion() {
        return minSupportedVersion;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    // Nota: Per actualitzar aquests valors en temps d'execució,
    // caldria implementar un sistema de configuració persistent
    // (base de dades o Redis) o reiniciar l'aplicació amb noves variables d'entorn
}