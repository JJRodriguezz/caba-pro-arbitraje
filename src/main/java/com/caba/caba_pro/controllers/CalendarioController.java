/**
 * Archivo: CalendarioController.java Autores: Sistema CABA Pro Fecha última modificación:
 * 07.09.2025 Descripción: Controlador para las vistas del calendario y API de eventos Proyecto:
 * CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.DTOs.CalendarioDto;
import com.caba.caba_pro.models.Torneo;
import com.caba.caba_pro.services.CalendarioService;
import com.caba.caba_pro.services.TorneoService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CalendarioController {

  @Autowired private CalendarioService calendarioService;

  @Autowired private TorneoService torneoService;

  /** Vista del calendario para administradores */
  @GetMapping("/admin/calendario")
  public String calendarioAdmin(Model model, Authentication authentication) {
    model.addAttribute("rol", "ADMIN");
    model.addAttribute("username", authentication.getName());
    return "admin/calendario/calendario";
  }

  /** Vista del calendario para árbitros */
  @GetMapping("/arbitro/calendario")
  public String calendarioArbitro(Model model, Authentication authentication) {
    model.addAttribute("rol", "ARBITRO");
    model.addAttribute("username", authentication.getName());
    return "arbitro/calendario/calendario";
  }

  /** API para obtener eventos en formato JSON para FullCalendar */
  @GetMapping("/eventos")
  @ResponseBody
  public ResponseEntity<List<CalendarioDto>> obtenerEventos(
      Authentication authentication,
      @RequestParam(required = false) String fechaInicio,
      @RequestParam(required = false) String fechaFin,
      @RequestParam(required = false) Long torneoId) {

    String username = authentication.getName();
    List<CalendarioDto> eventos;

    // Determinar rol del usuario
    if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
      // Es administrador - ver todos los eventos
      if (fechaInicio != null || fechaFin != null || torneoId != null) {
        eventos =
            calendarioService.obtenerEventosConFiltros(
                username, "ADMIN", fechaInicio, fechaFin, torneoId);
      } else {
        eventos = calendarioService.obtenerEventosAdmin();
      }
    } else {
      // Es árbitro - ver solo sus asignaciones
      if (fechaInicio != null || fechaFin != null || torneoId != null) {
        eventos =
            calendarioService.obtenerEventosConFiltros(
                username, "ARBITRO", fechaInicio, fechaFin, torneoId);
      } else {
        eventos = calendarioService.obtenerEventosArbitro(username);
      }
    }

    return ResponseEntity.ok(eventos);
  }

  /** API para obtener eventos específicos de un árbitro (solo para admins) */
  @GetMapping("/admin/eventos/arbitro")
  @ResponseBody
  public ResponseEntity<List<CalendarioDto>> obtenerEventosDeArbitro(
      @RequestParam String usernameArbitro, Authentication authentication) {

    // Verificar que sea administrador
    if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
      return ResponseEntity.status(403).build();
    }

    List<CalendarioDto> eventos = calendarioService.obtenerEventosArbitro(usernameArbitro);
    return ResponseEntity.ok(eventos);
  }

  /** Endpoint para obtener lista de torneos disponibles para filtros */
  @GetMapping("/calendario/torneos")
  @ResponseBody
  public ResponseEntity<List<Map<String, Object>>> obtenerTorneosParaFiltros() {
    List<Torneo> torneos = torneoService.buscarTodosActivos();
    List<Map<String, Object>> opciones =
        torneos.stream()
            .map(
                t -> {
                  Map<String, Object> m = new HashMap<>();
                  m.put("id", t.getId());
                  m.put("nombre", t.getNombre());
                  m.put("estado", t.getEstado() != null ? t.getEstado().name() : null);
                  return m;
                })
            .collect(Collectors.toList());
    return ResponseEntity.ok(opciones);
  }
}
