package in.lucidpoint.app.config;

import in.lucidpoint.app.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Central security wiring:
 *  - Disables CSRF (irrelevant for a stateless JWT API — no browser session/cookies to forge)
 *  - Sets session policy to STATELESS (every request must carry its own JWT; server keeps no session)
 *  - Registers our JwtAuthenticationFilter before Spring's default username/password filter
 *  - Declares which endpoints are public vs which require a role
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // enables @PreAuthorize("hasRole('ADMIN')") on controller/service methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        // Publishing workflow (ROADMAP.md §3.5): these two are NOT part of the
                        // public reading surface, even though they sit under the same path prefix
                        // the wildcard below permits — order matters here, first match wins.
                        .requestMatchers(HttpMethod.GET, "/api/content/resources/mine").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/content/resources/pending").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/content/resources/recommended").authenticated()
                        // Free knowledge layer (ROADMAP.md §3.2): reading resources needs no login;
                        // publishing (POST) still requires auth + @PreAuthorize on the controller.
                        .requestMatchers(HttpMethod.GET, "/api/content/resources/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // https://frontend-three-rho-27.vercel.app is the real, deployed production frontend
        // (Vercel project "lucid8/frontend") — everything else here is local dev.
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://192.168.1.6:5173",
                "https://lucidpoint.in",
                "https://frontend-three-rho-27.vercel.app"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
