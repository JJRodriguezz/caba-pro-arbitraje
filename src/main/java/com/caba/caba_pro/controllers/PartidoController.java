/**
 * Archivo: PartidoController.java Autores: Juan José Fecha última modificación: 05.09.2025
 * Descripción: Controlador HTTP para administración de partidos. Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.DTOs.AsignacionDto;
import com.caba.caba_pro.DTOs.PartidoDto;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.models.Partido;
import com.caba.caba_pro.repositories.ArbitroRepository;
import com.caba.caba_pro.services.PartidoService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/admin/partidos")
@PreAuthorize("hasRole('ADMIN')")
public class PartidoController {

  // 1. Constantes estáticas

  // 2. Variables de instancia
  private final PartidoService partidoService;
  private final ArbitroRepository arbitroRepository;

  // 3. Constructores
  public PartidoController(PartidoService partidoService, ArbitroRepository arbitroRepository) {
    this.partidoService = partidoService;
    this.arbitroRepository = arbitroRepository;
  }

  // 4. Métodos públicos

  @GetMapping
  public String listar(Model model) {
    List<Partido> partidos = partidoService.buscarActivos();
    model.addAttribute("partidos", partidos);
    return "admin/partidos/lista";
  }

  @GetMapping("/nuevo")
  public String mostrarFormularioCreacion(Model model) {
    model.addAttribute("partidoDto", new PartidoDto());
    return "admin/partidos/form";
  }

  @PostMapping
  public String crear(
      @Valid @ModelAttribute("partidoDto") PartidoDto partidoDto,
      BindingResult result,
      Model model) {

    if (result.hasErrors()) {
      return "admin/partidos/form";
    }

    partidoService.crear(partidoDto);
    return "redirect:/admin/partidos";
  }

  @GetMapping("/{id}")
  public String detalle(@PathVariable Long id, Model model) {
    Partido partido = partidoService.buscarPorId(id);
    List<Arbitro> arbitros = arbitroRepository.findByActivoTrue();
    model.addAttribute("partido", partido);
    model.addAttribute("arbitros", arbitros);
    model.addAttribute("asignacionDto", new AsignacionDto());
    return "admin/partidos/detalle";
  }

  @GetMapping("/{id}/editar")
  public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
    // Cargar entidad y prellenar DTO
    Partido partido = partidoService.buscarPorId(id);

    PartidoDto dto = new PartidoDto();
    dto.setNombre(partido.getNombre());
    dto.setDescripcion(partido.getDescripcion());
    dto.setFechaHora(partido.getFechaHora());
    dto.setSede(partido.getSede());
    dto.setEquipoLocal(partido.getEquipoLocal());
    dto.setEquipoVisitante(partido.getEquipoVisitante());

    model.addAttribute("partido", partido);
    model.addAttribute("partidoDto", dto);
    return "admin/partidos/editar";
  }

  // Asignar árbitro (permanece en la vista y muestra aviso si hay conflicto)
  @PostMapping("/{id}/asignar")
  public String asignarArbitro(
      @PathVariable Long id,
      @Valid @ModelAttribute("asignacionDto") AsignacionDto asignacionDto,
      BindingResult result,
      Model model) {

    // Si hay errores de validación del formulario, volvemos al detalle sin perder
    // lo digitado
    if (result.hasErrors()) {
      // Cargamos datos necesarios de la vista
      model.addAttribute("partido", partidoService.buscarPorId(id));
      model.addAttribute("arbitros", arbitroRepository.findByActivoTrue());
      return "admin/partidos/detalle";
    }

    try {
      partidoService.asignarArbitro(id, asignacionDto);
    } catch (BusinessException e) {
      // Mostrar el mensaje en la misma vista (bloque rojo .message.error ya existe en
      // el template)
      model.addAttribute("error", e.getMessage());
      // Reponer datos de la pantalla y mantener el DTO con lo que el usuario eligió
      model.addAttribute("partido", partidoService.buscarPorId(id));
      model.addAttribute("arbitros", arbitroRepository.findByActivoTrue());
      return "admin/partidos/detalle";
    }

    // Éxito: volvemos al detalle (si prefieres, podrías redirigir a "#asignar")
    return "redirect:/admin/partidos/{id}";
  }

  // Eliminar partido
  @PostMapping("/{id}/eliminar")
  public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
    partidoService.eliminar(id); // activo=false
    ra.addFlashAttribute("success", "Partido eliminado correctamente.");
    return "redirect:/admin/partidos"; // vuelve al listado
  }

  @PostMapping("/{id}/editar")
  public String actualizar(
      @PathVariable Long id,
      @Valid @ModelAttribute("partidoDto") PartidoDto partidoDto,
      BindingResult result,
      RedirectAttributes ra,
      Model model) {

    if (result.hasErrors()) {
      // Requiere el objeto partido para el header de la vista editar
      model.addAttribute("partido", partidoService.buscarPorId(id));
      return "admin/partidos/editar";
    }

    partidoService.actualizar(id, partidoDto);
    ra.addFlashAttribute("success", "Partido actualizado correctamente.");
    return "redirect:/admin/partidos";
  }
}
