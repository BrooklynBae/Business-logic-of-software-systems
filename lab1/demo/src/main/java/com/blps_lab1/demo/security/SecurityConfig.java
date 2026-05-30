//package com.blps_lab1.demo.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.jaas.AbstractJaasAuthenticationProvider;
//import org.springframework.security.authentication.jaas.DefaultJaasAuthenticationProvider;
//import org.springframework.security.authentication.jaas.AuthorityGranter;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import java.util.Collections;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true) // Явно разрешаем использование @PreAuthorize и @PostAuthorize
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtFilter;
//
//    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
//        this.jwtFilter = jwtFilter;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // 1. ЖЕСТКО И ПРИНУДИТЕЛЬНО ОТКЛЮЧАЕМ CSRF PROTECT
//                .csrf(csrf -> csrf.disable())
//
//                // 2. Настраиваем CORS, чтобы запросы не блокировались на сетевом уровне
//                .cors(cors -> cors.disable())
//
//                // 3. Переводим сессии в режим Stateless (сервер не запоминает клиентов, авторизация только по JWT)
//                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
//                // 4. Настраиваем правила для URL-адресов (сначала открытые, потом закрытые)
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/**").permitAll() // Свободный доступ к логину
//                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/reservation").permitAll() // Создание черновиков доступно всем по ТЗ
//                        .requestMatchers("/reservation/entity").permitAll() // Создание черновиков
//                        .requestMatchers("/places/town/**", "/places/rating").permitAll() // Поиск мест по ТЗ открыт неавторизованным
//
//                        // УДАЛЕНО: .requestMatchers("/user", "/owners").permitAll()
//                        // Теперь POST /user и POST /owners требуют валидный JWT-токен в соответствии с вашим ТЗ
//
//                        .anyRequest().authenticated()
//                )
//
//                // 5. Подключаем JAAS провайдер
//                .authenticationProvider(jaasAuthenticationProvider())
//
//                // 6. Добавляем наш JWT-фильтр в цепочку перед стандартным фильтром
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public AbstractJaasAuthenticationProvider jaasAuthenticationProvider() {
//        DefaultJaasAuthenticationProvider provider = new DefaultJaasAuthenticationProvider();
//
//        try {
//            ClassPathResource resource = new ClassPathResource("jaas.config");
//            System.setProperty("java.security.auth.login.config", resource.getFile().getAbsolutePath());
//
//            javax.security.auth.login.Configuration systemConfig = javax.security.auth.login.Configuration.getConfiguration();
//            provider.setConfiguration(systemConfig);
//
//        } catch (java.io.IOException e) {
//            throw new RuntimeException("Could not load jaas.config file", e);
//        }
//
//        provider.setLoginContextName("SpringSecurityJaasConfig");
//        provider.setAuthorityGranters(new AuthorityGranter[]{
//                principal -> {
//                    if (principal instanceof JaasRolePrincipal || principal instanceof JaasAuthorityPrincipal) {
//                        return Collections.singleton(principal.getName());
//                    }
//                    return null;
//                }
//        });
//
//        return provider;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//}
package com.blps_lab1.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/reservation").permitAll()
                        .requestMatchers("/reservation/entity").permitAll()
                        .requestMatchers("/places/town/**", "/places/rating").permitAll()
                        .anyRequest().authenticated()
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


        } catch (java.io.IOException e) {
            throw new RuntimeException("CRITICAL: Failed to extract and load jaas.config from classpath", e);
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
