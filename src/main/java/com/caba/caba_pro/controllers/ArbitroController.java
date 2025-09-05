/**
 * Archivo: ArbitroController.java Autores: Isabella.Idarraga Fecha última modificación: [04.09.2025]
 * Descripción: Controlador para la gestión de árbitros en la aplicación Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.DTOs.ArbitroDto;
import com.caba.caba_pro.enums.Especialidad;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.services.ArbitroService;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/arbitros")
@PreAuthorize("hasRole('ADMIN')")
public class ArbitroController {

  private static final Logger logger = LoggerFactory.getLogger(ArbitroController.class);

  @Autowired private ArbitroService arbitroService;

  @GetMapping
  public String listarArbitros(Model model) {
    List<Arbitro> arbitros = arbitroService.buscarTodosActivos();
    model.addAttribute("arbitros", arbitros);
    return "admin/arbitros";
  }

  @GetMapping("/nuevo")
  public String mostrarFormularioCrear(Model model) {
    model.addAttribute("arbitroDto", new ArbitroDto());
    model.addAttribute("especialidades", Especialidad.values());
    return "admin/crear-arbitro";
  }

  @PostMapping
  public String crearArbitro(
      @Valid @ModelAttribute("arbitroDto") ArbitroDto arbitroDto,
      BindingResult result,
      RedirectAttributes flash,
      Model model) {

    if (result.hasErrors()) {
      model.addAttribute("especialidades", Especialidad.values());
      return "admin/crear-arbitro";
    }

    try {
      Arbitro arbitroCreado = arbitroService.crearArbitro(arbitroDto);
      logger.info("Árbitro creado exitosamente: {}", arbitroCreado.getNombreCompleto());

      flash.addFlashAttribute(
          "success", "Árbitro creado exitosamente. Usuario: " + arbitroDto.getUsername());
      return "redirect:/admin/arbitros";

    } catch (BusinessException e) {
      logger.error("Error de negocio al crear árbitro: {}", e.getMessage());
      model.addAttribute("error", e.getMessage());
      model.addAttribute("especialidades", Especialidad.values());
      return "admin/crear-arbitro";

    } catch (Exception e) {
      logger.error("Error inesperado al crear árbitro", e);
      model.addAttribute("error", "Error interno del sistema");
      model.addAttribute("especialidades", Especialidad.values());
      return "admin/crear-arbitro";
    }
  }

  @GetMapping("/editar/{id}")
  public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
    try {
      Arbitro arbitro = arbitroService.buscarPorId(id);
      ArbitroDto arbitroDto = mapearArbitroADto(arbitro);

      model.addAttribute("arbitroDto", arbitroDto);
      model.addAttribute("arbitroId", id);
      model.addAttribute("especialidades", Especialidad.values());
      return "admin/editar-arbitro";

    } catch (BusinessException e) {
      logger.error("Árbitro no encontrado: {}", id);
      return "redirect:/admin/arbitros";
    }
  }

  @PostMapping("/{id}")
  public String actualizarArbitro(
      @PathVariable Long id,
      @Valid @ModelAttribute("arbitroDto") ArbitroDto arbitroDto,
      BindingResult result,
      RedirectAttributes flash,
      Model model) {

    if (result.hasErrors()) {
      model.addAttribute("arbitroId", id);
      model.addAttribute("especialidades", Especialidad.values());
      return "admin/editar-arbitro";
    }

    try {
      arbitroService.actualizarArbitro(id, arbitroDto);
      flash.addFlashAttribute("success", "Árbitro actualizado exitosamente");
      return "redirect:/admin/arbitros";

    } catch (BusinessException e) {
      model.addAttribute("error", e.getMessage());
      model.addAttribute("arbitroId", id);
      model.addAttribute("especialidades", Especialidad.values());
      return "admin/editar-arbitro";
    }
  }

  @GetMapping("/eliminar/{id}")
  public String eliminarArbitro(@PathVariable Long id, RedirectAttributes flash) {
    try {
      arbitroService.eliminarArbitro(id);
      flash.addFlashAttribute("success", "Árbitro eliminado exitosamente");
    } catch (BusinessException e) {
      flash.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/admin/arbitros";
  }

  private ArbitroDto mapearArbitroADto(Arbitro arbitro) {
    ArbitroDto dto = new ArbitroDto();
    dto.setNombre(arbitro.getNombre());
    dto.setApellidos(arbitro.getApellidos());
    dto.setNumeroIdentificacion(arbitro.getNumeroIdentificacion());
    dto.setEmail(arbitro.getEmail());
    dto.setTelefono(arbitro.getTelefono());
    dto.setEspecialidad(arbitro.getEspecialidad());
    dto.setEscalafon(arbitro.getEscalafon());
    dto.setFechaNacimiento(arbitro.getFechaNacimiento());
    // No incluir password por seguridad
    return dto;
  }
}
