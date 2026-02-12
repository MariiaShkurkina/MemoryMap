package ru.tbank.education.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .authorizeHttpRequests(auth -> auth
                                // открытые эндпоинты
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/geo/**").permitAll()
                                .requestMatchers("/error").permitAll()

                                // админ может менять каталог тегов
                                .requestMatchers(HttpMethod.POST,   "/api/tags/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT,    "/api/tags/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/tags/**").hasRole("ADMIN")

                                // весь /api/** требует токен
                                .requestMatchers("/api/**").authenticated()
                                .requestMatchers("/actuator/prometheus").permitAll()
                                .anyRequest().permitAll()
                        // .anyRequest().denyAll()
                );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        http.exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> res.sendError(401))
                .accessDeniedHandler((req, res, ex) -> res.sendError(403))
        );

        return http.build();
    }
}
