/**
 * Archivo: DashboardController.java Autores: Isabella.Idarraga Fecha última modificación: [04.09.2025]
 * Descripción: Controlador para la gestión del dashboard en la aplicación Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

  // Dashboard para administradores
  @GetMapping("/admin/dashboard")
  @PreAuthorize("hasRole('ADMIN')")
  public String dashboardAdmin(Model model) {
    return "admin/dashboard";
  }

  // Dashboard para árbitros
  @GetMapping("/arbitro/dashboard")
  @PreAuthorize("hasAnyRole('USER', 'ARBITRO')")
  public String dashboardArbitro(Model model) {
    return "arbitro/dashboard";
  }

  // Página de inicio general (fallback)
  @GetMapping("/")
  public String inicio() {
    return "redirect:/login";
  }
}
