package com.blps_lab1.demo.controller;

import com.blps_lab1.demo.dto.LoginRequest;
import com.blps_lab1.demo.dto.JwtResponse;
import com.blps_lab1.demo.security.JwtTokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Создаем стандартный токен аутентификации Spring Security с голыми логином/паролем
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword()
            );

            // 2. Отправляем этот токен в AuthenticationManager.
            // Манагер внутри себя вызовет JAAS, который пойдет в XmlJaasLoginModule и проверит users.xml!
            Authentication authentication = authenticationManager.authenticate(authToken);

            // 3. Если проверка прошла успешно, вытаскиваем из объекта авторизации роли и права
            List<String> authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // 4. Генерируем красивый строковый JWT-токен, упаковывая туда эти права
            String token = jwtTokenUtils.generateToken(loginRequest.getUsername(), authorities);

            // 5. Возвращаем JSON с токеном обратно в Insomnia
            return ResponseEntity.ok(new JwtResponse(token, loginRequest.getUsername(), authorities));

        } catch (Exception e) {
            // Если JAAS вернул false (пароль неверный) — сработает этот блок
            return ResponseEntity.status(401).body("Invalid username or password: " + e.getMessage());
        }
    }
}