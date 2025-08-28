package cat.ajterrassa.validaciofactures.filter;

import cat.ajterrassa.validaciofactures.controller.AppConfigController;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class VersionCheckFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(VersionCheckFilter.class);
    private static final String VERSION_HEADER = "X-App-Version";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientVersion = request.getHeader(VERSION_HEADER);
        if (clientVersion != null && isVersionOlder(clientVersion, AppConfigController.MIN_SUPPORTED_VERSION)) {
            logger.warn("Rebutjada sol·licitud amb versió antiga: {}", clientVersion);
            response.setStatus(HttpStatus.UPGRADE_REQUIRED.value());
            return;
        }
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
