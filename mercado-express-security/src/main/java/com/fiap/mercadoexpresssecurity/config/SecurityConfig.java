package com.fiap.mercadoexpresssecurity.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RoleBasedAuthSuccessHandler roleBasedAuthSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // /api/** e exclusivamente uma API REST (usada via Postman/Insomnia para
                // testar o fluxo de compra assincrono da Parte 2) - sem sessao de
                // navegador nessas chamadas, entao o token CSRF (pensado para forms
                // HTML) nao se aplica e e desligado so para esse prefixo.
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        // Recursos estáticos e páginas públicas de entrada
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/", "/index").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/cadastro", "/cadastro/**").permitAll()

                        // Endpoint REST de compras (CP5 Parte 2) - publico, pensado para
                        // testes via Postman/Insomnia sem precisar autenticar
                        .requestMatchers("/api/**").permitAll()

                        // Área restrita do usuário comum (ROLE_USER ou ROLE_ADMIN)
                        .requestMatchers("/area/**").authenticated()

                        // Carrinho de compras e historico de compras exigem login
                        .requestMatchers("/carrinho/**", "/minhas-compras").authenticated()

                        // Painel administrativo, dashboard da gerencia e qualquer escrita
                        // no catalogo -> somente ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/mercado/novo", "/mercado/salvar",
                                "/mercado/*/editar", "/mercado/*/excluir").hasRole("ADMIN")

                        // Consulta ao catálogo (leitura) é pública
                        .requestMatchers(HttpMethod.GET, "/mercado", "/mercado/**").permitAll()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(roleBasedAuthSuccessHandler)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}