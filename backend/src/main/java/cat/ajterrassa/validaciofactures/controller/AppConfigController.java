package cat.ajterrassa.validaciofactures.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class AppConfigController {

    @Value("${app.min.supported.version:1.0.0}")
    private String minSupportedVersion;

    @Value("${app.update.message:Actualitza l'aplicació per continuar utilitzant el servei.}")
    private String updateMessage;

    @Value("${app.update.url:https://play.google.com/store/apps/details?id=com.example.app}")
    private String updateUrl;

    @Value("${play.integrity.cloud.project.number:1013719707047}")
    private Long playIntegrityCloudProjectNumber;

    @Value("${app.version:1.0.0}")
    private String backendVersion;

    // Mantenim aquesta constant per compatibilitat amb VersionCheckFilter
    public static final String MIN_SUPPORTED_VERSION = "1.0.0";

    @GetMapping("/app")
    public AppConfigResponse getAppConfig() {
        return new AppConfigResponse(
                minSupportedVersion,
                updateMessage,
                updateUrl,
                playIntegrityCloudProjectNumber,
                backendVersion
        );
    }

    public static class AppConfigResponse {
        private final String minSupportedVersion;
        private final String message;
        private final String updateUrl;
        private final Long playIntegrityCloudProjectNumber;
        private final String backendVersion;

        public AppConfigResponse(String minSupportedVersion, String message, String updateUrl, 
                               Long playIntegrityCloudProjectNumber, String backendVersion) {
            this.minSupportedVersion = minSupportedVersion;
            this.message = message;
            this.updateUrl = updateUrl;
            this.playIntegrityCloudProjectNumber = playIntegrityCloudProjectNumber;
            this.backendVersion = backendVersion;
        }

        public String getMinSupportedVersion() {
            return minSupportedVersion;
        }

        public String getMessage() {
            return message;
        }

        public String getUpdateUrl() {
            return updateUrl;
        }

        public Long getPlayIntegrityCloudProjectNumber() {
            return playIntegrityCloudProjectNumber;
        }

        public String getBackendVersion() {
            return backendVersion;
        }
    }
}
