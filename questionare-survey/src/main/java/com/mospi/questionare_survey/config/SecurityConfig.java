package com.mospi.questionare_survey.config;

import com.mospi.questionare_survey.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    // Constructor injection
    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    // 1. Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Authentication provider (Updated for Spring Security 6.x / Spring Boot 3.3.x)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Setter method ab fully support karta hai
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 3. Authentication manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 4. Security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives(
                                        "default-src 'self'; " +
                                                "frame-ancestors 'none'; " +
                                                "object-src 'none'; " +
                                                "base-uri 'self'"
                                )
                        )
                        .referrerPolicy(referrer ->
                                referrer.policy(
                                        ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER
                                )
                        )
                        .httpStrictTransportSecurity(hsts ->
                                hsts.includeSubDomains(true)
                                        .maxAgeInSeconds(31536000)
                        )
                        .addHeaderWriter((request, response) -> {
                            response.setHeader(
                                    "X-Permitted-Cross-Domain-Policies", "none");
                            response.setHeader(
                                    "Permissions-Policy",
                                    "camera=(), microphone=(), geolocation=()");
                            response.setHeader(
                                    "X-Content-Type-Options", "nosniff");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // Public APIs
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/v1/auth/authentication/login",
                                "/api/v1/auth/authentication/refresh",
                                "/api/v1/user/user/create-user",
                                "/api/v1/mas/role/create-role",
                                "/api/v1/mapping/user-role/create",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/health"
                        ).permitAll()

                        // All other APIs require authentication
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // 5. Global CORS configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // Allow any origin pattern
        configuration.setAllowedOriginPatterns(
                Arrays.asList("*")
        );

        configuration.setAllowCredentials(true);

        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS",
                "PATCH"
        ));

        // Allow all request headers, including Authorization
        configuration.setAllowedHeaders(
                Arrays.asList("*")
        );

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}