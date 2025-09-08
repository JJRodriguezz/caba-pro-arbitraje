/**
 * Archivo: EstadisticasAdminController.java Autores: JJRodriguezz Fecha última modificación:
 * 07.09.2025 Descripción: Controlador para la vista de estadísticas de desempeño de árbitros.
 * Proyecto: CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.DTOs.EstadisticasArbitrosDto;
import com.caba.caba_pro.DTOs.EstadisticasAsignacionesDto;
import com.caba.caba_pro.DTOs.TopArbitrosDto;
import com.caba.caba_pro.services.EstadisticasService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EstadisticasAdminController {

  private final EstadisticasService estadisticasService;

  @Autowired
  public EstadisticasAdminController(EstadisticasService estadisticasService) {
    this.estadisticasService = estadisticasService;
  }

  @GetMapping("/admin/estadisticas")
  public String verEstadisticas(
      @RequestParam(required = false) LocalDate desde,
      @RequestParam(required = false) LocalDate hasta,
      Model model) {
    // Estadísticas generales de árbitros
    EstadisticasArbitrosDto arbitrosStats =
        estadisticasService.obtenerEstadisticasArbitros(desde, hasta);
    // Estadísticas de asignaciones
    EstadisticasAsignacionesDto asignacionesStats =
        estadisticasService.obtenerEstadisticasAsignaciones(desde, hasta);
    // Top 5 árbitros más activos
    TopArbitrosDto topArbitros = estadisticasService.obtenerTopArbitros(desde, hasta);

    model.addAttribute("arbitrosStats", arbitrosStats);
    model.addAttribute("asignacionesStats", asignacionesStats);
    model.addAttribute("topArbitros", topArbitros);
    model.addAttribute("desde", desde);
    model.addAttribute("hasta", hasta);
    return "admin/estadisticas";
  }
}
