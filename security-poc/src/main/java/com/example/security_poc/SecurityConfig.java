package com.example.security_poc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Import necessário para liberar o console H2 com Spring Security
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Libera todos os nossos endpoints de PoC
                .requestMatchers("/", "/vulneravel", "/login-vulneravel", 
                                 "/seguro", "/login-seguro", "/resultado",
                                 "/xss-demo", "/add-comment",
                                 "/csrf-demo", "/change-password-protegido",
                                 "/css/**").permitAll() 
                // Libera o console H2
                .requestMatchers(toH2Console()).permitAll()
                // Qualquer OUTRA requisição precisaria de autenticação (mas não vamos usar)
                .anyRequest().authenticated()
            )
            // Configuração específica para o H2 Console funcionar (precisa desabilitar frameOptions)
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); 

        // IMPORTANTE: Por padrão, o Spring Security habilita a proteção CSRF.
        // Nós mantemos ela LIGADA. Apenas liberamos o H2 Console da verificação CSRF.
        http.csrf(csrf -> csrf
            .ignoringRequestMatchers(toH2Console())
        );

        return http.build();
    }
}