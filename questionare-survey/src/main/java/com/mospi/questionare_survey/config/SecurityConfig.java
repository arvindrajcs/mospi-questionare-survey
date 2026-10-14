package com.mospi.questionare_survey.config;

import org.springframework.beans.factory.annotation.Value;
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
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Value("${app.security.cors.allowed-origins:http://localhost:3000,http://10.68.61.136:3000,https://maayojanatest.rajasthan.gov.in,https://maayojana.rajasthan.gov.in}")
    private List<String> allowedOrigins;

    // ---------------------------------------------------------
    // 1. Password Encoder (LOGIN only)
    // ---------------------------------------------------------
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ---------------------------------------------------------
    // 2. Authentication Provider (LOGIN only)
    // ---------------------------------------------------------
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ---------------------------------------------------------
    // 3. Authentication Manager
    // ---------------------------------------------------------
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ---------------------------------------------------------
    // 4. Security Filter Chain (JWT + Stateless)
    // ---------------------------------------------------------
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; frame-ancestors 'none'; object-src 'none'; base-uri 'self'"))
                        .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
                        .addHeaderWriter((request, response) -> {
                            response.setHeader("X-Permitted-Cross-Domain-Policies", "none");
                            response.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
                            response.setHeader("X-Content-Type-Options", "nosniff");
                        })
                )

                // -------------------------------------------------
                // Authorization Rules
                // -------------------------------------------------
                .authorizeHttpRequests(auth -> auth
                                // PUBLIC APIs
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(
                                        "/api/v1/auth/authentication/login",
                                        "/api/v1/auth/authentication/refresh",
                                        "/api/v1/auth/sso/callback",
//                                "/api/v1/auth/authentication/validate",
//                                "/api/v1/auth/authentication/user-authorization",
//                                "/api/v1/auth/master/import-data",
//                                "/api/v1/auth/user/get-org-type",
//                                "/api/v1/auth/user/create-user",
//                                "/api/v1/auth/user/update-user",
//                                "/api/v1/auth/user/update-status",
//                                "/api/v1/auth/user/assign-role",
//                                "/api/v1/auth/role/update-role",
//                                "/api/v1/auth/user/approve-user",
//                                "/api/v1/auth/user/approval-pending",
//                                "/api/v1/auth/user/approval-reject",
//                                "/api/v1/auth/user/approval-approved",
//                                "/api/v1/auth/user/users-all",
//                                "/api/v1/auth/role/all-role",
//                                "/api/v1/auth/menu/all-menu",
//                                "/api/v1/auth/role/get-all-role",
//                                "/api/v1/auth/menu/update-status",
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/actuator/**"
                                ).permitAll()

                                // EVERYTHING ELSE NEEDS TOKEN
                                .anyRequest().authenticated()
                )

                // -------------------------------------------------
                // Provider used ONLY for login
                // -------------------------------------------------
                .authenticationProvider(authenticationProvider())

                // -------------------------------------------------
                // JWT Filter
                // -------------------------------------------------
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }


    // 2. Define the CORS configuration bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "userId", "X-Requested-With"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(
//            HttpSecurity http) throws Exception {
//
//        http
//                .csrf(csrf -> csrf.disable())
//                .cors(cors -> cors.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()
//                )
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(
//                                SessionCreationPolicy.STATELESS
//                        )
//                );
//
//        return http.build();
//    }
}