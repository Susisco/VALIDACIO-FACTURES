package cat.ajterrassa.validaciofactures.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        boolean isPublicRoute = (
            (method.equals("POST") && path.equals("/api/albarans")) ||
            (method.equals("POST") && path.equals("/api/albarans/frontend")) ||
            path.equals("/api/auth/login")  // ❗ NOMÉS login és públic
        );

        if (method.equalsIgnoreCase("OPTIONS") || isPublicRoute) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        logger.debug("🔒 Path: {}, Method: {}", path, method);
        logger.debug("📥 Authorization header: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                email = jwtUtil.extractUsername(jwt);
                logger.info("📧 Usuari extret del token: {}", email);
            } catch (Exception e) {
                logger.error("❌ Error extrayent email del token: {}", e.getMessage());
            }
        } else {
            logger.warn("⚠️ No s'ha rebut cap token vàlid al header Authorization.");
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                        );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("✅ Usuari autenticat correctament: {}", email);
                } else {
                    logger.error("❌ Token invàlid per al usuari: {}", email);
                }
            } catch (Exception e) {
                logger.error("❌ Error carregant usuari '{}': {}", email, e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
