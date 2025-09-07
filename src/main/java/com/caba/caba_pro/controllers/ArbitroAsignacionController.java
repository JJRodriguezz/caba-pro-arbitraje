/**
 * Archivo: ArbitroAsignacionController.java Autores: Juan José Fecha última modificación:
 * 07.09.2025 Descripción: Controlador para que los árbitros gestionen sus asignaciones
 * (aceptar/rechazar). Proyecto: CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.enums.AsignacionEstado;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.models.Asignacion;
import com.caba.caba_pro.repositories.AsignacionRepository;
import com.caba.caba_pro.services.ArbitroService;
import java.security.Principal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasRole('ARBITRO')")
public class ArbitroAsignacionController {
  // Logger
  private static final Logger logger = LoggerFactory.getLogger(ArbitroAsignacionController.class);

  private final ArbitroService arbitroService;
  private final AsignacionRepository asignacionRepository;

  public ArbitroAsignacionController(
      ArbitroService arbitroService, AsignacionRepository asignacionRepository) {
    this.arbitroService = arbitroService;
    this.asignacionRepository = asignacionRepository;
  }

  // Mostrar asignaciones del árbitro autenticado
  @GetMapping("/arbitro/asignaciones")
  public String verMisAsignaciones(Model model, Principal principal) {
    // Buscar árbitro autenticado
    Arbitro arbitro = arbitroService.buscarPorUsername(principal.getName());
    List<Asignacion> asignaciones =
        asignacionRepository.findByArbitroIdAndActivoTrue(arbitro.getId());
    model.addAttribute("asignaciones", asignaciones);
    return "arbitro/asignaciones";
  }

  // Aceptar asignación
  @PostMapping("/arbitro/asignaciones/{id}/aceptar")
  public String aceptarAsignacion(
      @PathVariable Long id, Principal principal, RedirectAttributes ra) {
    try {
      Arbitro arbitro = arbitroService.buscarPorUsername(principal.getName());
      Asignacion asignacion =
          asignacionRepository
              .findById(id)
              .orElseThrow(() -> new BusinessException("Asignación no encontrada"));
      if (!asignacion.getArbitro().getId().equals(arbitro.getId())) {
        throw new BusinessException("No tienes permiso para modificar esta asignación");
      }
      if (asignacion.getEstado() != AsignacionEstado.PENDIENTE) {
        throw new BusinessException("La asignación ya fue respondida");
      }
      asignacion.setEstado(AsignacionEstado.ACEPTADA);
      asignacion.setRespondidoEn(java.time.LocalDateTime.now());
      asignacionRepository.save(asignacion);
      ra.addFlashAttribute("success", "Asignación aceptada correctamente");
    } catch (BusinessException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/arbitro/asignaciones";
  }

  // Rechazar asignación
  @PostMapping("/arbitro/asignaciones/{id}/rechazar")
  public String rechazarAsignacion(
      @PathVariable Long id, Principal principal, RedirectAttributes ra) {
    try {
      Arbitro arbitro = arbitroService.buscarPorUsername(principal.getName());
      Asignacion asignacion =
          asignacionRepository
              .findById(id)
              .orElseThrow(() -> new BusinessException("Asignación no encontrada"));
      if (!asignacion.getArbitro().getId().equals(arbitro.getId())) {
        throw new BusinessException("No tienes permiso para modificar esta asignación");
      }
      if (asignacion.getEstado() != AsignacionEstado.PENDIENTE) {
        throw new BusinessException("La asignación ya fue respondida");
      }
      asignacion.setEstado(AsignacionEstado.RECHAZADA);
      asignacion.setRespondidoEn(java.time.LocalDateTime.now());
      asignacionRepository.save(asignacion);
      ra.addFlashAttribute("success", "Asignación rechazada correctamente");
    } catch (BusinessException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/arbitro/asignaciones";
  }
}
