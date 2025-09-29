package cat.ajterrassa.validaciofactures.filter;

import cat.ajterrassa.validaciofactures.service.PlayIntegrityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import org.springframework.core.annotation.Order;

@Component
@Order(1) // PlayIntegrityFilter debe ejecutarse primero
public class PlayIntegrityFilter extends OncePerRequestFilter {

    static final String INTEGRITY_HEADER = "X-Play-Integrity-Token";
    private static final Logger logger = LoggerFactory.getLogger(PlayIntegrityFilter.class);
    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/api/auth",
            "/api/devices/register",
            "/config",
            "/ping"
    );

    private final PlayIntegrityService playIntegrityService;
    private final ClientPlatformResolver platformResolver;

    public PlayIntegrityFilter(PlayIntegrityService playIntegrityService,
                               ClientPlatformResolver platformResolver) {
        this.playIntegrityService = playIntegrityService;
        this.platformResolver = platformResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (platformResolver.isWebRequest(request)) {
            logger.debug("Skipping Play Integrity validation for web request to {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }
        String integrityToken = request.getHeader(INTEGRITY_HEADER);
        if (integrityToken == null || !playIntegrityService.validateToken(integrityToken)) {
            logger.warn("Rejected request to {} due to invalid Play Integrity token", request.getRequestURI());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"INVALID_PLAY_INTEGRITY_TOKEN\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }
}
