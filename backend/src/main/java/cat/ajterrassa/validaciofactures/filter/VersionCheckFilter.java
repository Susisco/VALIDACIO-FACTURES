package cat.ajterrassa.validaciofactures.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.core.annotation.Order;

import java.io.IOException;

@Component
@Order(3) // VersionCheckFilter debe ejecutarse después de DeviceAuthorizationFilter
public class VersionCheckFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(VersionCheckFilter.class);
    private static final String VERSION_HEADER = "X-App-Version";

    @Value("${app.min.supported.version:1.0.0}")
    private String minSupportedVersion;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientVersion = request.getHeader(VERSION_HEADER);
        if (clientVersion != null && isVersionOlder(clientVersion, minSupportedVersion)) {
            logger.warn("Rebutjada sol·licitud amb versió antiga: {} (mínima requerida: {})", clientVersion, minSupportedVersion);
            response.setStatus(HttpStatus.UPGRADE_REQUIRED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"VERSION_TOO_OLD\",\"minVersion\":\"" + minSupportedVersion + "\"}");
            return;
        }
        logger.debug("Version check passed for {} with version {}", request.getRequestURI(), clientVersion);
        filterChain.doFilter(request, response);
    }

    private boolean isVersionOlder(String version, String minimum) {
        String[] currentParts = version.split("\\.");
        String[] minParts = minimum.split("\\.");
        int length = Math.max(currentParts.length, minParts.length);
        for (int i = 0; i < length; i++) {
            int current = i < currentParts.length ? parsePart(currentParts[i]) : 0;
            int min = i < minParts.length ? parsePart(minParts[i]) : 0;
            if (current < min) return true;
            if (current > min) return false;
        }
        return false;
    }

    private int parsePart(String part) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
