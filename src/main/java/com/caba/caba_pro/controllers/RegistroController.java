/**
 * Archivo: RegistroController.java Autores: Isabella.Idarraga Fecha última modificación:
 * [04.09.2025] Descripción: Controlador para la gestión de registro de usuarios Proyecto: CABA Pro
 * - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.DTOs.RegistroForm;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.services.RegistroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistroController {

  @Autowired private RegistroService registroService;

  @GetMapping("/registro")
  public String mostrarRegistro(Model model) {
    model.addAttribute("registroForm", new RegistroForm());
    return "registro";
  }

  @PostMapping("/registro")
  public String registrarAdministrador(
      @Valid @ModelAttribute("registroForm") RegistroForm registroForm,
      BindingResult result,
      RedirectAttributes flash,
      Model model) {

    // Validación de contraseñas coincidentes
    if (!registroForm.getPassword().equals(registroForm.getConfirmPassword())) {
      result.rejectValue("confirmPassword", "error.registroForm", "Las contraseñas no coinciden");
    }

    if (result.hasErrors()) {
      return "registro";
    }

    try {
      registroService.registrarAdministrador(registroForm);
      flash.addFlashAttribute("success", "Administrador registrado exitosamente");
      return "redirect:/login?registroExitoso";
    } catch (BusinessException e) {
      flash.addFlashAttribute("error", e.getMessage());
      return "registro";
    } catch (Exception e) {
      flash.addFlashAttribute("error", "Error interno del sistema");
      return "registro";
    }
  }
}
