/**
 * Archivo: UserInfoInterceptor.java Autores: Diego.Gonzalez Fecha última modificación: [10.09.2025]
 * Descripción: Interceptor para agregar información del usuario actual a las vistas Proyecto: CABA
 * Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.config;

import com.caba.caba_pro.models.Administrador;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.repositories.AdministradorRepository;
import com.caba.caba_pro.repositories.ArbitroRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class UserInfoInterceptor implements HandlerInterceptor {

  private static final Logger logger = LoggerFactory.getLogger(UserInfoInterceptor.class);

  private final AdministradorRepository administradorRepository;
  private final ArbitroRepository arbitroRepository;

  public UserInfoInterceptor(
      AdministradorRepository administradorRepository, ArbitroRepository arbitroRepository) {
    this.administradorRepository = administradorRepository;
    this.arbitroRepository = arbitroRepository;
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {

    logger.info("=== UserInfoInterceptor ejecutándose para: {} ===", request.getRequestURI());

    if (modelAndView != null) {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();

      if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
        String username = auth.getName();
        logger.info("Usuario autenticado: {}", username);

        // Buscar como administrador primero
        Administrador admin = administradorRepository.findByUsername(username);
        if (admin != null && admin.isActivo()) {
          UserInfo userInfo = new UserInfo();
          userInfo.setId(admin.getId());
          userInfo.setUsername(admin.getUsername());
          userInfo.setRol("ROLE_ADMIN");
          userInfo.setActivo(admin.isActivo());
          modelAndView.addObject("usuarioActual", userInfo);
          logger.info("Usuario admin agregado al modelo: {}", userInfo.getUsername());
          return;
        }

        // Buscar como árbitro
        Arbitro arbitro = arbitroRepository.findByUsername(username);
        if (arbitro != null && arbitro.isActivo()) {
          UserInfo userInfo = new UserInfo();
          userInfo.setId(arbitro.getId());
          userInfo.setUsername(arbitro.getUsername());
          userInfo.setRol("ROLE_ARBITRO");
          userInfo.setActivo(arbitro.isActivo());
          modelAndView.addObject("usuarioActual", userInfo);
          logger.info("Usuario árbitro agregado al modelo: {}", userInfo.getUsername());
        }
      } else {
        logger.warn("Usuario no autenticado o anónimo");
      }
    } else {
      logger.warn("ModelAndView es null para: {}", request.getRequestURI());
    }
  }

  // Clase interna para la información del usuario
  public static class UserInfo {
    private Long id;
    private String username;
    private String rol;
    private Boolean activo;

    // Getters y setters
    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getRol() {
      return rol;
    }

    public void setRol(String rol) {
      this.rol = rol;
    }

    public Boolean getActivo() {
      return activo;
    }

    public void setActivo(Boolean activo) {
      this.activo = activo;
    }
  }
}
