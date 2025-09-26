package cat.ajterrassa.validaciofactures.filter;

import cat.ajterrassa.validaciofactures.service.PlayIntegrityService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.servlet.FilterChain;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayIntegrityFilterTest {

    private static final String SECURED_ENDPOINT = "/api/factures";

    @Mock
    private PlayIntegrityService playIntegrityService;

    private PlayIntegrityFilter filter;

    @BeforeEach
    void setUp() {
        ClientPlatformResolver resolver = new ClientPlatformResolver(Set.of("http://localhost:5173"));
        filter = new PlayIntegrityFilter(playIntegrityService, resolver);
    }

    @Test
    void shouldSkipValidationForWebRequestsIdentifiedByOrigin() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(SECURED_ENDPOINT);
        request.addHeader("Origin", "http://localhost:5173");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        verify(playIntegrityService, never()).validateToken(anyString());
        assertThat(response.getStatus()).isNotEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void shouldRejectMobileRequestsWithoutValidToken() throws ServletException, IOException {
        when(playIntegrityService.validateToken(null)).thenReturn(false);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(SECURED_ENDPOINT);
        request.addHeader(ClientPlatformResolver.PLATFORM_HEADER, "android");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        verify(playIntegrityService).validateToken(null);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void shouldAllowMobileRequestsWithValidToken() throws ServletException, IOException {
        when(playIntegrityService.validateToken("valid-token")).thenReturn(true);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(SECURED_ENDPOINT);
        request.addHeader(ClientPlatformResolver.PLATFORM_HEADER, "android");
        request.addHeader(PlayIntegrityFilter.INTEGRITY_HEADER, "valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(playIntegrityService).validateToken("valid-token");
        verify(chain, times(1)).doFilter(request, response);
        assertThat(response.getStatus()).isNotEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
