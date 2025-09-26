package cat.ajterrassa.validaciofactures.filter;

import cat.ajterrassa.validaciofactures.model.DeviceRegistration;
import cat.ajterrassa.validaciofactures.model.DeviceRegistrationStatus;
import cat.ajterrassa.validaciofactures.repository.DeviceRegistrationRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class DeviceAuthorizationFilter extends OncePerRequestFilter {

    private final DeviceRegistrationRepository deviceRepository;
    private final ClientPlatformResolver platformResolver;

    public DeviceAuthorizationFilter(DeviceRegistrationRepository deviceRepository,
                                     ClientPlatformResolver platformResolver) {
        this.deviceRepository = deviceRepository;
        this.platformResolver = platformResolver;
    }

    private static final String FID_HEADER = "X-Firebase-Installation-Id";

    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/api/auth",
            "/api/devices/register",
            "/config",
            "/ping"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String fid = request.getHeader(FID_HEADER);
        if (platformResolver.isWebRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (fid == null) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }
        DeviceRegistration registration = deviceRepository.findByFid(fid).orElse(null);
        if (registration == null || registration.getStatus() != DeviceRegistrationStatus.APPROVED) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
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
