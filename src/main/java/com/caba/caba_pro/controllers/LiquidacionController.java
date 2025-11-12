/**
 * Archivo: LiquidacionController.java Autores: JJRodriguezz Fecha última modificación: 11.11.2025
 * Descripción: Controlador web para la vista de generación de liquidaciones Proyecto: CABA Pro -
 * Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.dto.LiquidacionDTO;
import com.caba.caba_pro.services.LiquidacionService;
import java.security.Principal;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LiquidacionController {

  private static final Logger logger = LoggerFactory.getLogger(LiquidacionController.class);

  private final LiquidacionService liquidacionService;

  public LiquidacionController(LiquidacionService liquidacionService) {
    this.liquidacionService = liquidacionService;
  }

  @GetMapping("/admin/liquidaciones")
  public String verLiquidacionesAdmin(
      @RequestParam(required = false) LocalDate fechaInicio,
      @RequestParam(required = false) LocalDate fechaFin,
      Model model) {

    logger.info("Accediendo a vista de liquidaciones (Admin)");

    // Si se proporcionan fechas, generar liquidación
    if (fechaInicio != null && fechaFin != null) {
      try {
        logger.info("Generando liquidación para período {} - {}", fechaInicio, fechaFin);

        LiquidacionDTO liquidacion =
            liquidacionService.generarLiquidacion(
                fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59));

        model.addAttribute("liquidacion", liquidacion);
        model.addAttribute("mostrarResultados", true);

      } catch (Exception e) {
        logger.error("Error al generar liquidación: {}", e.getMessage(), e);
        model.addAttribute("error", "Error al generar liquidación: " + e.getMessage());
      }
    }

    model.addAttribute("fechaInicio", fechaInicio);
    model.addAttribute("fechaFin", fechaFin);

    return "admin/liquidaciones";
  }

  @GetMapping("/arbitro/liquidaciones")
  public String verLiquidacionesArbitro(
      @RequestParam(required = false) LocalDate fechaInicio,
      @RequestParam(required = false) LocalDate fechaFin,
      Principal principal,
      Model model) {

    String username = principal.getName();
    logger.info("Accediendo a vista de liquidaciones (Árbitro: {})", username);

    // Si se proporcionan fechas, generar liquidación para el árbitro actual
    if (fechaInicio != null && fechaFin != null) {
      try {
        logger.info(
            "Generando liquidación para árbitro {} en período {} - {}",
            username,
            fechaInicio,
            fechaFin);

        var liquidacionArbitro =
            liquidacionService.generarLiquidacionPorArbitro(
                username, fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59));

        model.addAttribute("liquidacionArbitro", liquidacionArbitro);
        model.addAttribute("mostrarResultados", true);

      } catch (Exception e) {
        logger.error("Error al generar liquidación para árbitro: {}", e.getMessage(), e);
        model.addAttribute("error", "Error al generar liquidación: " + e.getMessage());
      }
    }

    model.addAttribute("fechaInicio", fechaInicio);
    model.addAttribute("fechaFin", fechaFin);

    return "arbitro/liquidaciones";
  }
}
