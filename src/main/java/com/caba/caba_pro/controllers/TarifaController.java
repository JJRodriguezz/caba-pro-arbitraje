/**
 * Archivo: TarifaController.java Autores: JJRodriguezz Fecha última modificación: 06.09.2025
 * Descripción: Controlador HTTP para administración de tarifas por escalafón. Proyecto: CABA Pro -
 * Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.DTOs.TarifaFormDto;
import com.caba.caba_pro.DTOs.TarifaItemDto;
import com.caba.caba_pro.models.Tarifa;
import com.caba.caba_pro.services.TarifaService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/tarifas")
@PreAuthorize("hasRole('ADMIN')")
public class TarifaController {

  // 1. Constantes estáticas
  private static final List<String> ESCALAFONES = List.of("FIBA", "PRIMERA", "SEGUNDA", "TERCERA");

  // 2. Variables de instancia
  private final TarifaService tarifaService;

  // 3. Constructores
  public TarifaController(TarifaService tarifaService) {
    this.tarifaService = tarifaService;
  }

  // 4. Métodos públicos

  @GetMapping
  public String verTarifas(Model model) {
    List<Tarifa> activas = tarifaService.buscarActivas();

    // Prellenar el formulario con todos los escalafones soportados
    TarifaFormDto form = new TarifaFormDto();
    for (String esc : ESCALAFONES) {
      BigDecimal monto =
          activas.stream()
              .filter(t -> t.getEscalafon().equalsIgnoreCase(esc))
              .map(Tarifa::getMonto)
              .findFirst()
              .orElse(BigDecimal.ZERO);

      form.getTarifas().add(new TarifaItemDto(esc, monto));
    }

    model.addAttribute("tarifaForm", form);
    return "admin/tarifas/index";
  }

  @PostMapping
  public String guardarTarifas(
      @Valid @ModelAttribute("tarifaForm") TarifaFormDto tarifaForm,
      BindingResult result,
      RedirectAttributes ra,
      Model model) {

    if (result.hasErrors()) {
      // Volvemos a la misma vista con los errores de validación
      return "admin/tarifas/index";
    }

    tarifaService.guardarTarifas(tarifaForm);
    ra.addFlashAttribute("success", "Tarifas actualizadas correctamente.");
    return "redirect:/admin/tarifas";
  }
}
