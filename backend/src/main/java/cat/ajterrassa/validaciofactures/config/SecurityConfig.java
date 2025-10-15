package cat.ajterrassa.validaciofactures.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

import cat.ajterrassa.validaciofactures.security.CustomAccessDeniedHandler;
import cat.ajterrassa.validaciofactures.security.UserDetailsServiceImpl;
import cat.ajterrassa.validaciofactures.security.JwtFilter;

@Configuration
@EnableMethodSecurity // ✅ AIXÒ ÉS EL QUE NECESSITES per habilitar les anotacions @PreAuthorize i @PostAuthorize
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    private JwtFilter jwtFilter;

    // Los filtros ahora se registran automáticamente por @Order, no necesitan inyección manual

    /**
     * Criterio de organización: 1. Primero se definen las rutas públicas (sin
     * autenticación). 2. Luego, las rutas protegidas se agrupan por temas
     * (segundo nivel de la URL, después de "/api"). 3. Dentro de cada grupo,
     * las rutas más específicas van primero, seguidas de las más generales. 4.
     * Finalmente, se aplica una regla general para cualquier otra solicitud no
     * especificada.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //.cors(cors -> cors.disable()) // Deshabilitar CORS (ajustar según sea necesario)
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para simplificar APIs REST

                .authorizeHttpRequests(auth -> auth
                // --- Rutas públicas ---
                .requestMatchers("/ping").permitAll()
                .requestMatchers("/config/**").permitAll() // ⭐ TEMPORALMENT: permetre /config
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/devices/register").permitAll() // ⭐ AFEGIT: registre de dispositius
                .requestMatchers("/api/devices/status").permitAll() // ⭐ AFEGIT: verificació estat dispositiu
                .requestMatchers("/api/test/**").permitAll()
                // ✅ Fitxers: només autenticats (o restringeix per rol)
                //.requestMatchers(HttpMethod.GET, "/api/fitxers/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/fitxers/**").permitAll()
                // --- Rutas protegidas: Dispositius ---
                .requestMatchers("/api/devices/associate-user").authenticated() // Associar usuari amb dispositiu
                // --- Rutas protegidas: Albarans ---
                .requestMatchers(HttpMethod.POST, "/api/albarans/app/save-with-file").authenticated() // Guardar albarans desde la app
                .requestMatchers(HttpMethod.POST, "/api/albarans/save-with-file").authenticated() // Guardar albarans desde el frontend
                .requestMatchers("/api/albarans/**").authenticated() // Cualquier otra operación sobre albarans

                // --- Rutas protegidas: Usuaris ---
                .requestMatchers("/api/usuaris/change-password").authenticated() // Cambio de contraseña de usuario
                .requestMatchers("/api/usuaris/**").authenticated() // Gestión general de usuarios

                // --- Rutas protegidas: Proveidors ---
                .requestMatchers("/api/proveidors/detall/**").authenticated() // Detalles específicos de proveedores
                .requestMatchers("/api/proveidors/**").authenticated() // Gestión general de proveedores

                // --- Rutas protegidas: Factures ---
                .requestMatchers("/api/factures/detall/**").authenticated() // Detalles específicos de facturas
                .requestMatchers("/api/factures/**").authenticated() // Gestión general de facturas

                // --- Rutas protegidas: Edificis ---
                .requestMatchers("/api/edificis/detall/**").authenticated() // Detalles específicos de edificios
                .requestMatchers("/api/edificis/**").authenticated() // Gestión general de edificios

                // --- Rutas protegidas: OTS ---
                .requestMatchers("/api/ots/detall/**").authenticated() // Detalles específicos de órdenes de trabajo
                .requestMatchers("/api/ots/**").authenticated() // Gestión general de órdenes de trabajo

                // --- Rutas protegidas: Historic ---
                .requestMatchers(HttpMethod.GET, "/api/historic/albara/**").authenticated()
                // --- Regla general ---
                .anyRequest().authenticated() // Cualquier otra solicitud requiere autenticación
                )
                // Manejo de excepciones
                .exceptionHandling(e -> e.accessDeniedHandler(customAccessDeniedHandler))
                // Configuración de sesiones
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Filtros y autenticación
                .authenticationProvider(authenticationProvider())
                // ✅ AÑADIR EL JWT FILTER A LA CADENA DE FILTROS
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
