/**
 * Archivo: ArbitroController.java Autores: Isabella.Idarraga & Diego.Gonzalez Fecha última
 * modificación: [10.09.2025] Descripción: Controlador para la gestión de árbitros en la aplicación
 * Proyecto: CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.DTOs.ArbitroDto;
import com.caba.caba_pro.enums.Especialidad;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.services.ArbitroService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/arbitros")
@PreAuthorize("hasRole('ADMIN')")
public class ArbitroController {

  // 1. Constantes estáticas
  private static final Logger logger = LoggerFactory.getLogger(ArbitroController.class);

  // 2. Variables de instancia
  private final ArbitroService arbitroService;
  private final MessageSource messageSource;

  // 3. Constructores
  public ArbitroController(ArbitroService arbitroService, MessageSource messageSource) {
    this.arbitroService = arbitroService;
    this.messageSource = messageSource;
  }

  // 4. Métodos públicos

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
    return "arbitro/crear-arbitro";
  }

  @PostMapping
  public String crearArbitro(
      @Valid @ModelAttribute("arbitroDto") ArbitroDto arbitroDto,
      BindingResult result,
      @RequestParam(value = "fotoPerfil", required = false) MultipartFile fotoPerfil,
      RedirectAttributes flash,
      Model model) {

    if (result.hasErrors()) {
      model.addAttribute("especialidades", Especialidad.values());
      return "arbitro/crear-arbitro";
    }

    try {
      Arbitro arbitroCreado = arbitroService.crearArbitro(arbitroDto, fotoPerfil);
      logger.info("Árbitro creado exitosamente: {}", arbitroCreado.getNombreCompleto());

      String mensaje =
          messageSource.getMessage(
              "arbitro.controller.creado.exito",
              new Object[] {arbitroDto.getNombre(), arbitroDto.getApellidos()},
              LocaleContextHolder.getLocale());
      flash.addFlashAttribute("success", mensaje);
      return "redirect:/admin/arbitros";

    } catch (BusinessException e) {
      logger.error("Error de negocio al crear árbitro: {}", e.getMessage());
      model.addAttribute("error", e.getMessage());
      model.addAttribute("especialidades", Especialidad.values());
      return "arbitro/crear-arbitro";

    } catch (Exception e) {
      logger.error("Error inesperado al crear árbitro", e);
      String mensajeError =
          messageSource.getMessage(
              "arbitro.controller.error.sistema", null, LocaleContextHolder.getLocale());
      model.addAttribute("error", mensajeError);
      model.addAttribute("especialidades", Especialidad.values());
      return "arbitro/crear-arbitro";
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
      return "arbitro/editar-arbitro";

    } catch (BusinessException e) {
      logger.error("Árbitro no encontrado: {}", id);
      return "redirect:/admin/arbitros";
    }
  }

  @PostMapping("/{id}")
  public String actualizarArbitro(
      @PathVariable Long id,
      @ModelAttribute("arbitroDto") ArbitroDto arbitroDto,
      BindingResult result,
      @RequestParam(value = "fotoPerfil", required = false) MultipartFile fotoPerfil,
      RedirectAttributes flash,
      Model model) {

    logger.info("=== INICIANDO ACTUALIZACIÓN DE ÁRBITRO ===");
    logger.info("ID del árbitro: {}", id);
    logger.info(
        "Datos recibidos - Nombre: {}, Apellidos: {}, Username: {}",
        arbitroDto.getNombre(),
        arbitroDto.getApellidos(),
        arbitroDto.getUsername());
    logger.info(
        "Foto recibida: {}",
        fotoPerfil != null ? fotoPerfil.getOriginalFilename() : "No se envió foto");

    try {
      Arbitro arbitroActualizado = arbitroService.actualizarArbitro(id, arbitroDto, fotoPerfil);
      logger.info("Árbitro actualizado exitosamente con ID: {}", arbitroActualizado.getId());
      String mensaje =
          messageSource.getMessage(
              "arbitro.controller.actualizado.exito", null, LocaleContextHolder.getLocale());
      flash.addFlashAttribute("success", mensaje);
      return "redirect:/admin/arbitros";

    } catch (BusinessException e) {
      logger.error("Error de negocio al actualizar árbitro: {}", e.getMessage());
      model.addAttribute("error", e.getMessage());
      model.addAttribute("arbitroId", id);
      model.addAttribute("especialidades", Especialidad.values());
      return "arbitro/editar-arbitro";
    } catch (Exception e) {
      logger.error("Error inesperado al actualizar árbitro: ", e);
      String mensajeError =
          messageSource.getMessage(
              "arbitro.controller.error.sistema", null, LocaleContextHolder.getLocale());
      model.addAttribute("error", mensajeError);
      model.addAttribute("arbitroId", id);
      model.addAttribute("especialidades", Especialidad.values());
      return "arbitro/editar-arbitro";
    }
  }

  @GetMapping("/eliminar/{id}")
  public String eliminarArbitro(@PathVariable Long id, RedirectAttributes flash) {
    try {
      arbitroService.eliminarArbitro(id);
      String mensaje =
          messageSource.getMessage(
              "arbitro.controller.eliminado.exito", null, LocaleContextHolder.getLocale());
      flash.addFlashAttribute("success", mensaje);
    } catch (BusinessException e) {
      flash.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/admin/arbitros";
  }

  // Métodos de mapeo privados
  private ArbitroDto mapearArbitroADto(Arbitro arbitro) {
    ArbitroDto dto = new ArbitroDto();
    dto.setNombre(arbitro.getNombre());
    dto.setApellidos(arbitro.getApellidos());
    dto.setUsername(arbitro.getUsername());
    dto.setNumeroIdentificacion(arbitro.getNumeroIdentificacion());
    dto.setEmail(arbitro.getEmail());
    dto.setTelefono(arbitro.getTelefono());
    dto.setEspecialidad(arbitro.getEspecialidad());
    dto.setEscalafon(arbitro.getEscalafon());
    dto.setFechaNacimiento(arbitro.getFechaNacimiento());
    dto.setUrlFotoPerfil(arbitro.getUrlFotoPerfil());
    // No incluir password por seguridad
    return dto;
  }

  // Endpoint para obtener lista de árbitros activos (para chat) GET /arbitros/list

  @GetMapping("/list")
  @PreAuthorize("hasRole('ADMIN')")
  public @ResponseBody List<Map<String, Object>> getArbitrosList() {
    try {
      List<Arbitro> arbitros = arbitroService.buscarTodosActivos();

      return arbitros.stream()
          .map(
              arbitro -> {
                Map<String, Object> arbitroData = new HashMap<>();
                arbitroData.put("id", arbitro.getId());
                arbitroData.put("username", arbitro.getUsername());
                arbitroData.put("nombre", arbitro.getNombre());
                arbitroData.put("nombreCompleto", arbitro.getNombreCompleto());
                return arbitroData;
              })
          .collect(Collectors.toList());
    } catch (Exception e) {
      logger.error("Error obteniendo lista de árbitros: {}", e.getMessage());
      return List.of();
    }
  }
}
