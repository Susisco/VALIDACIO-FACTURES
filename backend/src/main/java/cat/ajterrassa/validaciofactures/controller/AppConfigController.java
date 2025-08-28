package cat.ajterrassa.validaciofactures.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class AppConfigController {

    public static final String MIN_SUPPORTED_VERSION = "1.0.0";

    @GetMapping("/app")
    public AppConfigResponse getAppConfig() {
        return new AppConfigResponse(
                MIN_SUPPORTED_VERSION,
                "Actualitza l'aplicació per continuar utilitzant el servei.",
                "https://play.google.com/store/apps/details?id=com.example.app"
        );
    }

    public static class AppConfigResponse {
        private final String minSupportedVersion;
        private final String message;
        private final String updateUrl;

        public AppConfigResponse(String minSupportedVersion, String message, String updateUrl) {
            this.minSupportedVersion = minSupportedVersion;
            this.message = message;
            this.updateUrl = updateUrl;
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
    }
}
