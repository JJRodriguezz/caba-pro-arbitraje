/**
 * Archivo: TorneoController.java Autores: Diego.Gonzalez Fecha última modificación: 06.09.2025
 * Descripción: Controlador para la gestión de torneos en la aplicación Proyecto: CABA Pro - Sistema
 * de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.DTOs.TorneoDto;
import com.caba.caba_pro.enums.TorneoEstado;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Torneo;
import com.caba.caba_pro.services.TorneoService;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
@RequestMapping("/admin/torneos")
@PreAuthorize("hasRole('ADMIN')")
public class TorneoController {

  private static final Logger logger = LoggerFactory.getLogger(TorneoController.class);

  private final TorneoService torneoService;
  private final MessageSource messageSource;

  public TorneoController(TorneoService torneoService, MessageSource messageSource) {
    this.torneoService = torneoService;
    this.messageSource = messageSource;
  }

  @GetMapping
  public String listarTorneos(Model model) {
    List<Torneo> torneos = torneoService.buscarTodosActivos();
    model.addAttribute("torneos", torneos);
    return "admin/torneos/lista";
  }

  @GetMapping("/nuevo")
  public String mostrarFormularioCrear(Model model) {
    model.addAttribute("torneoDto", new TorneoDto());
    model.addAttribute("estados", TorneoEstado.values());
    return "admin/torneos/form";
  }

  @PostMapping
  public String crearTorneo(
      @Valid @ModelAttribute("torneoDto") TorneoDto torneoDto,
      BindingResult result,
      RedirectAttributes flash,
      Model model) {

    if (result.hasErrors()) {
      model.addAttribute("estados", TorneoEstado.values());
      return "admin/torneos/form";
    }

    try {
      Torneo torneoCreado = torneoService.crearTorneo(torneoDto);
      logger.info("Torneo creado exitosamente: {}", torneoCreado.getNombre());

      String mensaje =
          messageSource.getMessage(
              "torneo.creado.exito",
              new Object[] {torneoDto.getNombre()},
              LocaleContextHolder.getLocale());
      flash.addFlashAttribute("success", mensaje);
      return "redirect:/admin/torneos";

    } catch (BusinessException e) {
      logger.error("Error de negocio al crear torneo: {}", e.getMessage());
      model.addAttribute("error", e.getMessage());
      model.addAttribute("estados", TorneoEstado.values());
      return "admin/torneos/form";

    } catch (Exception e) {
      logger.error("Error inesperado al crear torneo", e);
      String mensaje =
          messageSource.getMessage("torneo.error.sistema", null, LocaleContextHolder.getLocale());
      model.addAttribute("error", mensaje);
      model.addAttribute("estados", TorneoEstado.values());
      return "admin/torneos/form";
    }
  }

  @GetMapping("/editar/{id}")
  public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
    try {
      Torneo torneo = torneoService.buscarPorId(id);
      TorneoDto torneoDto = mapearTorneoADto(torneo);

      model.addAttribute("torneoDto", torneoDto);
      model.addAttribute("torneoId", id);
      model.addAttribute("estados", TorneoEstado.values());
      return "admin/torneos/editar";

    } catch (BusinessException e) {
      logger.error("Torneo no encontrado: {}", id);
      return "redirect:/admin/torneos";
    }
  }

  @PostMapping("/{id}")
  public String actualizarTorneo(
      @PathVariable Long id,
      @Valid @ModelAttribute("torneoDto") TorneoDto torneoDto,
      BindingResult result,
      RedirectAttributes flash,
      Model model) {

    if (result.hasErrors()) {
      model.addAttribute("torneoId", id);
      model.addAttribute("estados", TorneoEstado.values());
      return "admin/torneos/editar";
    }

    try {
      torneoService.actualizarTorneo(id, torneoDto);
      String mensaje =
          messageSource.getMessage(
              "torneo.actualizado.exito", null, LocaleContextHolder.getLocale());
      flash.addFlashAttribute("success", mensaje);
      return "redirect:/admin/torneos";

    } catch (BusinessException e) {
      model.addAttribute("error", e.getMessage());
      model.addAttribute("torneoId", id);
      model.addAttribute("estados", TorneoEstado.values());
      return "admin/torneos/editar";
    }
  }

  @GetMapping("/ver/{id}")
  public String verDetalleTorneo(@PathVariable Long id, Model model) {
    try {
      Torneo torneo = torneoService.buscarPorId(id);
      model.addAttribute("torneo", torneo);
      return "admin/torneos/detalle";

    } catch (BusinessException e) {
      logger.error("Torneo no encontrado: {}", id);
      return "redirect:/admin/torneos";
    }
  }

  @GetMapping("/eliminar/{id}")
  public String eliminarTorneo(@PathVariable Long id, RedirectAttributes flash) {
    try {
      torneoService.eliminarTorneo(id);
      String mensaje =
          messageSource.getMessage("torneo.eliminado.exito", null, LocaleContextHolder.getLocale());
      flash.addFlashAttribute("success", mensaje);
    } catch (BusinessException e) {
      flash.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/admin/torneos";
  }

  @PostMapping("/cambiar-estado/{id}")
  public String cambiarEstadoTorneo(
      @PathVariable Long id,
      @ModelAttribute("nuevoEstado") String nuevoEstado,
      RedirectAttributes flash) {
    try {
      TorneoEstado estado = TorneoEstado.valueOf(nuevoEstado);
      torneoService.cambiarEstadoTorneo(id, estado);
      String mensaje =
          messageSource.getMessage(
              "torneo.estado.actualizado.exito", null, LocaleContextHolder.getLocale());
      flash.addFlashAttribute("success", mensaje);
    } catch (BusinessException e) {
      flash.addFlashAttribute("error", e.getMessage());
    } catch (IllegalArgumentException e) {
      String mensaje =
          messageSource.getMessage("torneo.estado.invalido", null, LocaleContextHolder.getLocale());
      flash.addFlashAttribute("error", mensaje);
    }
    return "redirect:/admin/torneos/ver/" + id;
  }

  private TorneoDto mapearTorneoADto(Torneo torneo) {
    TorneoDto dto = new TorneoDto();
    dto.setNombre(torneo.getNombre());
    dto.setDescripcion(torneo.getDescripcion());
    dto.setFechaInicio(torneo.getFechaInicio());
    dto.setFechaFin(torneo.getFechaFin());
    dto.setEstado(torneo.getEstado());
    dto.setUbicacion(torneo.getUbicacion());
    return dto;
  }
}
