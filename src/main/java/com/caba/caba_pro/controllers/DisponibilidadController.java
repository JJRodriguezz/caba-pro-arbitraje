/**
 * Archivo: DisponibilidadController.java Autores: JJRodriguezz Fecha última modificación:
 * 10.09.2025 Descripción: Controlador para la gestión de disponibilidad de árbitros Proyecto: CABA
 * Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.DTOs.DisponibilidadDto;
import com.caba.caba_pro.enums.TipoDisponibilidad;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.services.DisponibilidadService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/arbitro/disponibilidad")
@PreAuthorize("hasRole('ARBITRO')")
public class DisponibilidadController {

  private static final Logger logger = LoggerFactory.getLogger(DisponibilidadController.class);

  private final DisponibilidadService disponibilidadService;

  public DisponibilidadController(DisponibilidadService disponibilidadService) {
    this.disponibilidadService = disponibilidadService;
  }

  /** Muestra el formulario de disponibilidad del árbitro */
  @GetMapping
  public String mostrarDisponibilidad(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    logger.info("Mostrando disponibilidad para el árbitro: {}", username);

    try {
      DisponibilidadDto disponibilidad =
          disponibilidadService.obtenerOCrearDisponibilidadPorDefecto(username);

      model.addAttribute("disponibilidad", disponibilidad);
      model.addAttribute("tiposDisponibilidad", TipoDisponibilidad.values());

      return "arbitro/disponibilidad";

    } catch (Exception e) {
      logger.error("Error al cargar disponibilidad del árbitro: {}", e.getMessage());
      model.addAttribute("error", "Error al cargar la disponibilidad");
      return "arbitro/dashboard";
    }
  }

  /** Guarda la disponibilidad del árbitro */
  @PostMapping
  public String guardarDisponibilidad(
      @Valid @ModelAttribute("disponibilidad") DisponibilidadDto disponibilidadDto,
      BindingResult result,
      RedirectAttributes flash,
      Model model) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    logger.info("Guardando disponibilidad para el árbitro: {}", username);

    // Validación adicional para horario específico
    if (disponibilidadDto.getTipoDisponibilidad() == TipoDisponibilidad.HORARIO_ESPECIFICO) {
      if (disponibilidadDto.getHoraInicio() == null) {
        result.rejectValue("horaInicio", "error.horaInicio", "La hora de inicio es obligatoria");
      }
      if (disponibilidadDto.getHoraFin() == null) {
        result.rejectValue("horaFin", "error.horaFin", "La hora de fin es obligatoria");
      }
      if (disponibilidadDto.getHoraInicio() != null
          && disponibilidadDto.getHoraFin() != null
          && !disponibilidadDto.getHoraInicio().isBefore(disponibilidadDto.getHoraFin())) {
        result.rejectValue(
            "horaFin", "error.horaFin", "La hora de fin debe ser posterior a la hora de inicio");
      }
    }

    if (result.hasErrors()) {
      model.addAttribute("tiposDisponibilidad", TipoDisponibilidad.values());
      return "arbitro/disponibilidad";
    }

    try {
      disponibilidadService.guardarDisponibilidad(username, disponibilidadDto);

      flash.addFlashAttribute("success", "Disponibilidad actualizada exitosamente");
      return "redirect:/arbitro/disponibilidad";

    } catch (BusinessException e) {
      logger.error("Error de negocio al guardar disponibilidad: {}", e.getMessage());
      model.addAttribute("error", e.getMessage());
      model.addAttribute("tiposDisponibilidad", TipoDisponibilidad.values());
      return "arbitro/disponibilidad";

    } catch (Exception e) {
      logger.error("Error inesperado al guardar disponibilidad: ", e);
      flash.addFlashAttribute("error", "Error interno del sistema");
      return "redirect:/arbitro/disponibilidad";
    }
  }
}
