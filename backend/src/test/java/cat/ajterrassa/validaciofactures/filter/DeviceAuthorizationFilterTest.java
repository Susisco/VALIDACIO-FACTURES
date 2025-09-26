package cat.ajterrassa.validaciofactures.filter;

import cat.ajterrassa.validaciofactures.model.DeviceRegistration;
import cat.ajterrassa.validaciofactures.model.DeviceRegistrationStatus;
import cat.ajterrassa.validaciofactures.repository.DeviceRegistrationRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceAuthorizationFilterTest {

    private static final String PROTECTED_ENDPOINT = "/api/factures";

    @Mock
    private DeviceRegistrationRepository deviceRegistrationRepository;

    private DeviceAuthorizationFilter filter;

    @BeforeEach
    void setUp() {
        ClientPlatformResolver resolver = new ClientPlatformResolver(Set.of("http://localhost:5173"));
        filter = new DeviceAuthorizationFilter(deviceRegistrationRepository, resolver);
    }

    @Test
    void shouldBypassValidationForWebRequests() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(PROTECTED_ENDPOINT);
        request.addHeader("Origin", "http://localhost:5173");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {});

        verifyNoInteractions(deviceRegistrationRepository);
        assertThat(response.getStatus()).isNotEqualTo(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    void shouldRejectMobileRequestsWithoutFid() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(PROTECTED_ENDPOINT);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {});

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
        verifyNoInteractions(deviceRegistrationRepository);
    }

    @Test
    void shouldAllowMobileRequestsWithApprovedDevice() throws ServletException, IOException {
        DeviceRegistration registration = DeviceRegistration.builder()
                .fid("abc")
                .status(DeviceRegistrationStatus.APPROVED)
                .build();
        when(deviceRegistrationRepository.findByFid("abc")).thenReturn(Optional.of(registration));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(PROTECTED_ENDPOINT);
        request.addHeader("X-Firebase-Installation-Id", "abc");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {});

        verify(deviceRegistrationRepository).findByFid("abc");
        assertThat(response.getStatus()).isNotEqualTo(HttpServletResponse.SC_FORBIDDEN);
    }
}
