package cat.ajterrassa.validaciofactures.filter;

import cat.ajterrassa.validaciofactures.service.PlayIntegrityService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class PlayIntegrityFilterTest {

    private static final String PROTECTED_PATH = "/api/albarans/save-with-file";

    @Test
    void allowsRequestWhenTokenIsValid() throws ServletException, IOException {
        PlayIntegrityFilter filter = new PlayIntegrityFilter(new PlayIntegrityService(true, List.of("valid-token")));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(PROTECTED_PATH);
        request.addHeader(PlayIntegrityFilter.INTEGRITY_HEADER, "valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean invoked = new AtomicBoolean(false);

        filter.doFilter(request, response, (req, res) -> invoked.set(true));

        assertThat(invoked.get()).isTrue();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void rejectsRequestWhenTokenIsMissing() throws ServletException, IOException {
        PlayIntegrityFilter filter = new PlayIntegrityFilter(new PlayIntegrityService(true, List.of("valid-token")));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(PROTECTED_PATH);
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean invoked = new AtomicBoolean(false);

        filter.doFilter(request, response, (req, res) -> invoked.set(true));

        assertThat(invoked.get()).isFalse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void rejectsRequestWhenTokenIsInvalid() throws ServletException, IOException {
        PlayIntegrityFilter filter = new PlayIntegrityFilter(new PlayIntegrityService(true, List.of("valid-token")));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(PROTECTED_PATH);
        request.addHeader(PlayIntegrityFilter.INTEGRITY_HEADER, "invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean invoked = new AtomicBoolean(false);

        filter.doFilter(request, response, (req, res) -> invoked.set(true));

        assertThat(invoked.get()).isFalse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void skipsValidationForExcludedPaths() throws ServletException, IOException {
        PlayIntegrityFilter filter = new PlayIntegrityFilter(new PlayIntegrityService(true, List.of("valid-token")));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean invoked = new AtomicBoolean(false);

        filter.doFilter(request, response, (req, res) -> invoked.set(true));

        assertThat(invoked.get()).isTrue();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
