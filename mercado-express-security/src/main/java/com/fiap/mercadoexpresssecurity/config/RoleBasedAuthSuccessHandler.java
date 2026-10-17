package com.fiap.mercadoexpresssecurity.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Decide para onde cada usuário é redirecionado logo após o login,
 * de acordo com o seu "role":
 * - ROLE_ADMIN -> /admin  (painel administrativo: CRUD completo + relatórios)
 * - ROLE_USER  -> /area   (área restrita do usuário: catálogo + dados da conta)
 *
 * Isso cumpre o requisito de implementar pelo menos 2 tipos de usuário com
 * acessos (e telas de destino) diferentes.
 */
@Component
public class RoleBasedAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));

        String destino = isAdmin ? "/admin" : "/area";
        response.sendRedirect(request.getContextPath() + destino);
    }
}
