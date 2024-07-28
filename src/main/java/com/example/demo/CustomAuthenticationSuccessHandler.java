package com.example.demo;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String redirectUrl = null;

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            redirectUrl = "/Panel-usuario";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Admin1"))) {
            redirectUrl = "/Panel-gestion";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Admin2"))) {
            redirectUrl = "/Panel-admin";
        }

        if (redirectUrl != null) {
            response.sendRedirect(redirectUrl);
        } else {
            throw new IllegalStateException("Role not found");
        }
    }
}


