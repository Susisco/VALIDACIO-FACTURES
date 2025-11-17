package cat.ajterrassa.validaciofactures.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import cat.ajterrassa.validaciofactures.config.ClientPlatformProperties;
import cat.ajterrassa.validaciofactures.model.DeviceRegistration;
import cat.ajterrassa.validaciofactures.model.DeviceRegistrationStatus;
import cat.ajterrassa.validaciofactures.repository.DeviceRegistrationRepository;
import java.time.Instant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(2) // DeviceAuthorizationFilter debe ejecutarse después de PlayIntegrityFilter
public class DeviceAuthorizationFilter extends OncePerRequestFilter {

    private static final String FID_HEADER = "X-Firebase-Installation-Id";

    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/api/devices/register",
            "/api/devices/associate-user",
            "/api/devices/status", // Nou endpoint per verificar estat dispositiu
            "/api/fitxers",
            "/config",
            "/ping"
    );

    private final DeviceRegistrationRepository deviceRepository;
    private final ClientPlatformProperties clientPlatformProperties;
    private final Set<String> trustedWebOrigins;

    public DeviceAuthorizationFilter(DeviceRegistrationRepository deviceRepository,
                                     ClientPlatformProperties clientPlatformProperties) {
        this.deviceRepository = deviceRepository;
        this.clientPlatformProperties = clientPlatformProperties;
        this.trustedWebOrigins = Collections.unmodifiableSet(
                clientPlatformProperties.getTrustedWebOrigins().stream()
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toSet())
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String fid = request.getHeader(FID_HEADER);
        if (!StringUtils.hasText(fid)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }
        DeviceRegistration registration = deviceRepository.findByFid(fid).orElse(null);
        if (registration == null || registration.getStatus() != DeviceRegistrationStatus.APPROVED) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }
        // Marquem activitat del dispositiu aprovat
        registration.setLastSeenAt(Instant.now());
        deviceRepository.save(registration);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            return true;
        }
        return isWebClient(request);
    }

    private boolean isWebClient(HttpServletRequest request) {
        String platformHeader = request.getHeader(clientPlatformProperties.getHeaderName());
        if (StringUtils.hasText(platformHeader)) {
            if (platformHeader.equalsIgnoreCase(clientPlatformProperties.getWebValue())) {
                return true;
            }
            if (platformHeader.equalsIgnoreCase(clientPlatformProperties.getAndroidValue())) {
                return false;
            }
        }

        String origin = request.getHeader("Origin");
        if (StringUtils.hasText(origin) && trustedWebOrigins.contains(origin)) {
            return true;
        }

        String referer = request.getHeader("Referer");
        if (StringUtils.hasText(referer)) {
            return trustedWebOrigins.stream().anyMatch(referer::startsWith);
        }

        return false;
    }
}
