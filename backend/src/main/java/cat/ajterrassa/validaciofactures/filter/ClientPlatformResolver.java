package cat.ajterrassa.validaciofactures.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Helper that determines from which platform (web browser or mobile app) an HTTP request originates.
 *
 * <p>The detection prioritises the explicit {@code X-Client-Platform} header so that the Android
 * application can always mark itself as mobile. When the header is not present, the resolver falls
 * back to the {@code Origin} header (browser requests executed through CORS) and, as an additional
 * safeguard, the {@code User-Agent} value.</p>
 */
@Component
public class ClientPlatformResolver {

    static final String PLATFORM_HEADER = "X-Client-Platform";
    private static final String WEB_PLATFORM = "web";
    private static final Logger logger = LoggerFactory.getLogger(ClientPlatformResolver.class);

    private final Set<String> allowedWebOrigins;

    public ClientPlatformResolver(
            @Value("${platform.web.origins:${cors.allowed.origin:}}") String configuredOrigins
    ) {
        this(parseOrigins(configuredOrigins));
    }

    ClientPlatformResolver(Set<String> allowedWebOrigins) {
        this.allowedWebOrigins = allowedWebOrigins == null ? Collections.emptySet() : allowedWebOrigins;
    }

    public boolean isWebRequest(HttpServletRequest request) {
        String headerPlatform = request.getHeader(PLATFORM_HEADER);
        if (headerPlatform != null && !headerPlatform.isBlank()) {
            boolean isWeb = WEB_PLATFORM.equalsIgnoreCase(headerPlatform.trim());
            if (logger.isDebugEnabled()) {
                logger.debug("Detected platform via header {} = {} (web = {})", PLATFORM_HEADER, headerPlatform, isWeb);
            }
            return isWeb;
        }

        String origin = request.getHeader("Origin");
        if (origin != null && !origin.isBlank()) {
            boolean matchesOrigin = matchesAllowedOrigin(origin);
            if (logger.isDebugEnabled()) {
                logger.debug("Detected platform via Origin header {} (web = {})", origin, matchesOrigin);
            }
            if (matchesOrigin) {
                return true;
            }
        }

        String userAgent = request.getHeader("User-Agent");
        boolean looksLikeBrowser = userAgent != null && userAgent.toLowerCase(Locale.ROOT).contains("mozilla");
        if (logger.isDebugEnabled()) {
            logger.debug("Detected platform via User-Agent '{}' (web = {})", userAgent, looksLikeBrowser);
        }
        return looksLikeBrowser;
    }

    public boolean isMobileRequest(HttpServletRequest request) {
        String headerPlatform = request.getHeader(PLATFORM_HEADER);
        if (headerPlatform != null && !headerPlatform.isBlank()) {
            return !WEB_PLATFORM.equalsIgnoreCase(headerPlatform.trim());
        }
        return !isWebRequest(request);
    }

    private boolean matchesAllowedOrigin(String origin) {
        if (allowedWebOrigins.isEmpty()) {
            return true;
        }
        String normalised = normaliseOrigin(origin);
        return allowedWebOrigins.contains(normalised);
    }

    private static Set<String> parseOrigins(String configuredOrigins) {
        if (configuredOrigins == null || configuredOrigins.isBlank()) {
            return Collections.emptySet();
        }
        Set<String> result = new LinkedHashSet<>();
        Arrays.stream(configuredOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(ClientPlatformResolver::normaliseOrigin)
                .forEach(result::add);
        return result;
    }

    private static String normaliseOrigin(String origin) {
        if (origin == null) {
            return "";
        }
        String trimmed = origin.trim();
        if (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }
}
