package cat.ajterrassa.validaciofactures.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


//***NOTA pendent unificar CORS aqui i no en els controladors... explicació: 
/*
tenir dues configuracions de CORS (un CorsConfigurationSource + un CorsFilter propi) pot generar comportaments duplicats o confusos. Tria una sola via i elimina l’altra.

Què et recomano
Mantén la via “estàndard” de Spring Security: el bean CorsConfigurationSource (el que ja tens a CorsConfig) + http.cors(Customizer.withDefaults()) dins SecurityConfig.

Elimina el @Bean CorsFilter manual amb @Order(HIGHEST_PRECEDENCE) per evitar solapaments.

Per què
http.cors() ja busca automàticament un CorsConfigurationSource al context i registra el filtre de CORS al lloc correcte de la cadena de filtres de Spring Security.

Un CorsFilter teu, a més a més, s’executa “al marge” de Spring Security i pot aplicar normes diferents o duplicar capçaleres.

Detalls útils
Si uses cfg.setAllowCredentials(true), no pots fer allowedOrigins("*"). Amb llista explícita d’orígens (com tu fas), perfecte.

App Android o clients “no navegador” no necessiten CORS (CORS és un mecanisme del navegador). Pots treure orígens com http://10.0.2.2:8080 si l’app pega directament a l’API.

Pots centralitzar els orígens en properties i llegir-los com a llista.
*/
@Configuration
public class CorsConfig {

    // Comentado el CorsFilter manual para evitar conflictos
    // Solo usaremos CorsConfigurationSource + Spring Security
    /*
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        // 🔓 Orígens permesos: local, docker, producció, android
        config.setAllowedOrigins(List.of(
            "https://validacio-backend.fly.dev",
            "https://validacio-factures.vercel.app",
            "http://localhost:5173",       // Frontend local amb Vite
            "http://localhost:3000",       // Frontend servit per Docker (serve/nginx)
            "http://10.0.2.2:8080",        // Android Emulator
            "http://192.168.1.133:8080",   // IP local per dispositius mòbils a la mateixa WiFi (ajusta-la)
            "http://34.201.113.44:3000"    // Producció AWS (IP pública EC2 + port del frontend)
            // Afegeix aquí el teu domini si tens un DNS personalitzat (ex: https://validacio.factures.cat)
        ));

        config.addAllowedHeader("*"); // Permet tots els headers
        config.addAllowedMethod("*"); // Permet tots els mètodes HTTP

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
    */


/**
 * Configuració global de CORS (Cross-Origin Resource Sharing) per a l'API.
 *
 * Aquesta classe defineix un bean {@link CorsConfigurationSource} que permet
 * especificar des de quins orígens es poden fer peticions a l'API, així com
 * quins mètodes HTTP, capçaleres i credencials estan permesos.
 *
 * ⚙️ Com funciona:
 * - El valor de l'origen permès es llegeix del paràmetre `cors.allowed.origin`
 *   definit a `application-*.properties` (per exemple, http://localhost:5173 en local
 *   i https://validacio-factures.vercel.app en producció).
 * - S'apliquen les regles de CORS a totes les rutes (`/**`).
 * - S'habiliten mètodes HTTP comuns (GET, POST, PUT, DELETE, OPTIONS).
 * - S'accepten totes les capçaleres (`*`).
 * - `setAllowCredentials(true)` permet enviar cookies o tokens JWT
 *   amb les peticions CORS.
 *
 * Avantatge:
 * Centralitza la configuració de CORS per a tots els controladors sense
 * necessitat d'afegir @CrossOrigin a cada classe o mètode.
 */

@Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${cors.allowed.origin}") String allowedOrigins
    ) {
        CorsConfiguration cfg = new CorsConfiguration();
        // Dividim les URLs separades per comes
        String[] origins = allowedOrigins.split(",");
        cfg.setAllowedOrigins(List.of(origins));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }


}
