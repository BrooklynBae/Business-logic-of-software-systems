package com.blps_lab1.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.jaas.AbstractJaasAuthenticationProvider;
import org.springframework.security.authentication.jaas.DefaultJaasAuthenticationProvider;
import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/owners").permitAll()
                        .requestMatchers(HttpMethod.POST, "/reservation").permitAll()
                        .requestMatchers("/reservation/entity").permitAll()
                        .requestMatchers("/places/town/**", "/places/rating").permitAll()
                        .requestMatchers("/reservation/contracts/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            String json = String.format(
                                    "{\"message\":\"%s\",\"status\":%d,\"timestamp\":\"%s\"}",
                                    "Unauthorized access. Please login.",
                                    HttpStatus.UNAUTHORIZED.value(),
                                    Instant.now()
                            );
                            response.getWriter().write(json);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            String json = String.format(
                                    "{\"message\":\"%s\",\"status\":%d,\"timestamp\":\"%s\"}",
                                    "Access denied. You do not have permission to access this resource.",
                                    HttpStatus.FORBIDDEN.value(),
                                    Instant.now()
                            );
                            response.getWriter().write(json);
                        })
                )
                .authenticationProvider(jaasAuthenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AbstractJaasAuthenticationProvider jaasAuthenticationProvider() {
        DefaultJaasAuthenticationProvider provider = new DefaultJaasAuthenticationProvider();

        try {
            ClassPathResource resource = new ClassPathResource("jaas.config");
            File tempJaasFile = File.createTempFile("jaas_v1_", ".config");
            tempJaasFile.deleteOnExit();

            try (InputStream inputStream = resource.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(tempJaasFile)) {
                inputStream.transferTo(outputStream);
            }

            System.setProperty("java.security.auth.login.config", tempJaasFile.getAbsolutePath());
            javax.security.auth.login.Configuration systemConfig = javax.security.auth.login.Configuration.getConfiguration();
            provider.setConfiguration(systemConfig);

        } catch (Exception e) {
            throw new RuntimeException("Internal configuration error: unable to load JAAS settings", e);
        }

        provider.setLoginContextName("SpringSecurityJaasConfig");
        provider.setAuthorityGranters(new AuthorityGranter[]{
                principal -> {
                    if (principal instanceof JaasRolePrincipal || principal instanceof JaasAuthorityPrincipal) {
                        return Collections.singleton(principal.getName());
                    }
                    return null;
                }
        });

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}