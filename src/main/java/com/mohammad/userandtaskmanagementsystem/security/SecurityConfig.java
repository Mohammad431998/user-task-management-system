package com.mohammad.userandtaskmanagementsystem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // ==========================================
    // PASSWORD ENCODER
    // ==========================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ==========================================
    // AUTHENTICATION MANAGER
    // ==========================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    // ==========================================
    // CORS
    // ==========================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:4200"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    // ==========================================
    // SECURITY FILTER CHAIN
    // ==========================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

                // ==================================
                // CSRF
                // ==================================

                .csrf(csrf ->
                        csrf.disable()
                )

                // ==================================
                // CORS
                // ==================================

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                // ==================================
                // SESSION
                // ==================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // ==================================
                // EXCEPTION HANDLING
                // ==================================

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                authenticationEntryPoint()
                        )
                )

                // ==================================
                // AUTHORIZATION
                // ==================================

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // AUTH
                        // Public
                        // =========================

                        .requestMatchers(
                                "/api/auth/**"
                        )
                        .permitAll()

                        // =========================
                        // WEBSOCKET HANDSHAKE
                        // Public (SockJS negotiation)
                        // =========================

                        .requestMatchers(
                                "/ws/**"
                        )
                        .permitAll()

                        // =========================
                        // MY PROFILE
                        // ADMIN + USER
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users/me"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/users/me"
                        )
                        .authenticated()

                        // =========================
                        // USER MANAGEMENT
                        // ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                "/api/users/**"
                        )
                        .hasRole("ADMIN")

                        // =========================
                        // ROLES (lookup for forms)
                        // ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                "/api/roles/**"
                        )
                        .hasRole("ADMIN")

                        // =========================
                        // ACTIVITY LOG
                        // ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                "/api/activity-logs/**"
                        )
                        .hasRole("ADMIN")

                        // =========================
                        // USER TASKS
                        // USER ONLY
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/tasks/my"
                        )
                        .hasRole("USER")

                        // =========================
                        // CREATE TASK
                        // ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/tasks"
                        )
                        .hasRole("ADMIN")

                        // =========================
                        // VIEW ALL TASKS
                        // ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/tasks"
                        )
                        .hasRole("ADMIN")

                        // =========================
                        // VIEW TASK BY ID
                        // ADMIN + USER
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/tasks/*"
                        )
                        .authenticated()

                        // =========================
                        // UPDATE TASK
                        // ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/tasks/*"
                        )
                        .hasRole("ADMIN")

                        // =========================
                        // DELETE TASK
                        // ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/tasks/*"
                        )
                        .hasRole("ADMIN")

                        // =========================
                        // ASSIGN TASK
                        // ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/tasks/*/assign"
                        )
                        .hasRole("ADMIN")

                        // =========================
                        // UPDATE TASK STATUS
                        // USER ONLY
                        // =========================

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/tasks/*/status"
                        )
                        .hasRole("USER")

                        // =========================
                        // COMMENTS
                        // ADMIN + USER
                        // =========================

                        .requestMatchers(
                                "/api/comments/**"
                        )
                        .authenticated()

                        // =========================
                        // NOTIFICATIONS
                        // ADMIN + USER
                        // =========================

                        .requestMatchers(
                                "/api/notifications/**"
                        )
                        .authenticated()

                        // =========================
                        // EVERYTHING ELSE
                        // =========================

                        .anyRequest()
                        .authenticated()
                )

                // ==================================
                // JWT FILTER
                // ==================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // ==========================================
    // AUTHENTICATION ENTRY POINT
    // ==========================================

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {

        return (request, response, authException) -> {

            response.setStatus(
                    HttpStatus.UNAUTHORIZED.value()
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write("""
                {
                    "status": 401,
                    "message": "Authentication required"
                }
                """);
        };
    }
}