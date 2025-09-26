package cat.ajterrassa.validaciofactures.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.client-platform")
public class ClientPlatformProperties {

    /**
     * Header used by clients to identify their platform (e.g. WEB, ANDROID).
     */
    private String headerName = "X-Client-Platform";

    /**
     * Header value that identifies trusted web clients.
     */
    private String webValue = "WEB";

    /**
     * Header value that identifies Android mobile clients.
     */
    private String androidValue = "ANDROID";

    /**
     * Additional trusted web origins used when the platform header is missing
     * (for example, legacy browsers).
     */
    private List<String> trustedWebOrigins = new ArrayList<>();

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getWebValue() {
        return webValue;
    }

    public void setWebValue(String webValue) {
        this.webValue = webValue;
    }

    public String getAndroidValue() {
        return androidValue;
    }

    public void setAndroidValue(String androidValue) {
        this.androidValue = androidValue;
    }

    public List<String> getTrustedWebOrigins() {
        return trustedWebOrigins;
    }

    public void setTrustedWebOrigins(List<String> trustedWebOrigins) {
        this.trustedWebOrigins = trustedWebOrigins;
    }
}