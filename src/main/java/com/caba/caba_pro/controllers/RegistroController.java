/**
 * Archivo: RegistroController.java Autores: Isabella.Idarraga Fecha última modificación:
 * [04.09.2025] Descripción: Controlador para la gestión de registro de usuarios Proyecto: CABA Pro
 * - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

// 2. Librerías externas
import com.caba.caba_pro.DTOs.RegistroForm;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.services.RegistroService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistroController {

  // 2. Variables de instancia
  private final RegistroService registroService;
  private final MessageSource messageSource;

  // 3. Constructores
  public RegistroController(RegistroService registroService, MessageSource messageSource) {
    this.registroService = registroService;
    this.messageSource = messageSource;
  }

  // 4. Métodos públicos

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
      String mensaje =
          messageSource.getMessage(
              "registro.password.no.coinciden", null, LocaleContextHolder.getLocale());
      result.rejectValue("confirmPassword", "error.registroForm", mensaje);
    }

    if (result.hasErrors()) {
      return "registro";
    }

    try {
      registroService.registrarAdministrador(registroForm);
      String mensaje =
          messageSource.getMessage("registro.admin.exito", null, LocaleContextHolder.getLocale());
      flash.addFlashAttribute("success", mensaje);
      return "redirect:/login?registroExitoso";
    } catch (BusinessException e) {
      model.addAttribute("error", e.getMessage());
      return "registro";
    } catch (Exception e) {
      String mensaje =
          messageSource.getMessage("registro.error.sistema", null, LocaleContextHolder.getLocale());
      model.addAttribute("error", mensaje);
      return "registro";
    }
  }
}
