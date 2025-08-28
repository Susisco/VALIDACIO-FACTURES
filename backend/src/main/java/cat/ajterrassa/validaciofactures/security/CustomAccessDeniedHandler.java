package cat.ajterrassa.validaciofactures.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        System.out.println("❌ ACCÉS DENEGAT:");
        System.out.println("🔹 Path: " + request.getRequestURI());
        System.out.println("🔹 Mètode: " + request.getMethod());
        System.out.println("🔹 Missatge: " + accessDeniedException.getMessage());

        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accés denegat");
    }
}
