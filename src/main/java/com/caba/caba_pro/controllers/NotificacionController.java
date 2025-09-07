/**
 * Archivo: NotificacionController.java Autores: JJRodriguezz Fecha última modificación: 07.09.2025
 * Descripción: Controlador para mostrar notificaciones a árbitros y administradores. Proyecto: CABA
 * Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.models.Notificacion;
import com.caba.caba_pro.services.ArbitroService;
import com.caba.caba_pro.services.NotificacionService;
import java.security.Principal;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NotificacionController {
  private final NotificacionService notificacionService;
  private final ArbitroService arbitroService;
  private final com.caba.caba_pro.repositories.AdministradorRepository administradorRepository;

  public NotificacionController(
      NotificacionService notificacionService,
      ArbitroService arbitroService,
      com.caba.caba_pro.repositories.AdministradorRepository administradorRepository) {
    this.notificacionService = notificacionService;
    this.arbitroService = arbitroService;
    this.administradorRepository = administradorRepository;
  }

  // Vista de notificaciones para árbitros
  @PreAuthorize("hasRole('ARBITRO')")
  @GetMapping("/arbitro/notificaciones")
  public String verNotificacionesArbitro(Model model, Principal principal) {
    Arbitro arbitro = arbitroService.buscarPorUsername(principal.getName());
    List<Notificacion> notificaciones =
        notificacionService.obtenerNotificaciones(arbitro.getId(), "ARBITRO");
    // Formatear fecha para cada notificación
    List<org.springframework.util.LinkedMultiValueMap<String, Object>> notificacionesFormateadas =
        new java.util.ArrayList<>();
    for (Notificacion n : notificaciones) {
      org.springframework.util.LinkedMultiValueMap<String, Object> map =
          new org.springframework.util.LinkedMultiValueMap<>();
      map.add("mensaje", n.getMensaje());
      map.add("leida", n.getLeida());
      map.add("tipo", n.getTipo());
      map.add("asignacionId", n.getAsignacionId());
      map.add(
          "fechaFormateada",
          n.getFechaCreacion() != null
              ? n.getFechaCreacion()
                  .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
              : "-");
      notificacionesFormateadas.add(map);
    }
    model.addAttribute("notificaciones", notificacionesFormateadas);
    return "arbitro/notificaciones";
  }

  // Vista de notificaciones para administradores
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/admin/notificaciones")
  public String verNotificacionesAdmin(Model model, Principal principal) {
    // Obtener el administrador autenticado por username
    com.caba.caba_pro.models.Administrador admin =
        administradorRepository.findByUsername(principal.getName());
    if (admin == null || !admin.isActivo()) {
      throw new com.caba.caba_pro.exceptions.BusinessException(
          "Administrador no encontrado o inactivo");
    }
    List<Notificacion> notificaciones =
        notificacionService.obtenerNotificaciones(admin.getId(), "ADMIN");
    List<org.springframework.util.LinkedMultiValueMap<String, Object>> notificacionesFormateadas =
        new java.util.ArrayList<>();
    for (Notificacion n : notificaciones) {
      org.springframework.util.LinkedMultiValueMap<String, Object> map =
          new org.springframework.util.LinkedMultiValueMap<>();
      map.add("mensaje", n.getMensaje());
      map.add("leida", n.getLeida());
      map.add("tipo", n.getTipo());
      map.add("asignacionId", n.getAsignacionId());
      map.add(
          "fechaFormateada",
          n.getFechaCreacion() != null
              ? n.getFechaCreacion()
                  .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
              : "-");
      notificacionesFormateadas.add(map);
    }
    model.addAttribute("notificaciones", notificacionesFormateadas);
    return "admin/notificaciones";
  }

  // Marcar notificación como leída
  @PostMapping("/notificaciones/{id}/leida")
  public String marcarComoLeida(@PathVariable Long id) {
    notificacionService.marcarComoLeida(id);
    return "redirect:/arbitro/notificaciones";
  }
}
