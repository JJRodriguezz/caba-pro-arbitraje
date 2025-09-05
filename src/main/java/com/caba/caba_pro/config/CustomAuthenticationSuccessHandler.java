/**
 * Archivo: CustomAuthenticationSuccessHandler.java Autores: Isabella.Idarraga Fecha última modificación: [04.09.2025]
 * Descripción: Manejador personalizado de autenticación que redirige a los usuarios según su rol tras iniciar sesión. Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    String redirectUrl = determinarUrlPorRol(authentication);

    logger.info(
        "Usuario {} autenticado exitosamente. Redirigiendo a: {}",
        authentication.getName(),
        redirectUrl);

    response.sendRedirect(redirectUrl);
  }

  private String determinarUrlPorRol(Authentication authentication) {
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

    for (GrantedAuthority authority : authorities) {
      String role = authority.getAuthority();

      switch (role) {
        case "ROLE_ADMIN":
          return "/admin/dashboard";
        case "ROLE_USER":
        case "ROLE_ARBITRO":
          return "/arbitro/dashboard";
        default:
          logger.warn("Rol no reconocido: {}. Redirigiendo a página por defecto", role);
          return "/";
      }
    }

    return "/";
  }
}
