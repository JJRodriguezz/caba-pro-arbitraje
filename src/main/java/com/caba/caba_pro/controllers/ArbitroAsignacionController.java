/**
 * Archivo: ArbitroAsignacionController.java Autores: JJRodriguezz Fecha última modificación:
 * 07.09.2025 Descripción: Controlador para que los árbitros gestionen sus asignaciones
 * (aceptar/rechazar). Proyecto: CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.enums.AsignacionEstado;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.models.Asignacion;
import com.caba.caba_pro.repositories.AsignacionRepository;
import com.caba.caba_pro.services.ArbitroService;
import java.security.Principal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasRole('ARBITRO')")
public class ArbitroAsignacionController {
  // Logger
  private static final Logger logger = LoggerFactory.getLogger(ArbitroAsignacionController.class);

  private final ArbitroService arbitroService;
  private final AsignacionRepository asignacionRepository;
  private final com.caba.caba_pro.services.NotificacionService notificacionService;
  private final com.caba.caba_pro.repositories.AdministradorRepository administradorRepository;
  private final MessageSource messageSource;

  public ArbitroAsignacionController(
      ArbitroService arbitroService,
      AsignacionRepository asignacionRepository,
      com.caba.caba_pro.services.NotificacionService notificacionService,
      com.caba.caba_pro.repositories.AdministradorRepository administradorRepository,
      MessageSource messageSource) {
    this.arbitroService = arbitroService;
    this.asignacionRepository = asignacionRepository;
    this.notificacionService = notificacionService;
    this.administradorRepository = administradorRepository;
    this.messageSource = messageSource;
  }

  // Mostrar asignaciones del árbitro autenticado
  @GetMapping("/arbitro/asignaciones")
  public String verMisAsignaciones(Model model, Principal principal) {
    // Buscar árbitro autenticado
    Arbitro arbitro = arbitroService.buscarPorUsername(principal.getName());
    List<Asignacion> asignaciones =
        asignacionRepository.findByArbitroIdAndActivoTrue(arbitro.getId());
    model.addAttribute("asignaciones", asignaciones);
    return "arbitro/asignaciones";
  }

  // Aceptar asignación
  @PostMapping("/arbitro/asignaciones/{id}/aceptar")
  public String aceptarAsignacion(
      @PathVariable Long id, Principal principal, RedirectAttributes ra) {
    try {
      Arbitro arbitro = arbitroService.buscarPorUsername(principal.getName());
      Asignacion asignacion =
          asignacionRepository
              .findById(id)
              .orElseThrow(
                  () ->
                      new BusinessException(
                          messageSource.getMessage(
                              "controller.asignacion.no.encontrada",
                              null,
                              LocaleContextHolder.getLocale())));
      if (!asignacion.getArbitro().getId().equals(arbitro.getId())) {
        throw new BusinessException(
            messageSource.getMessage(
                "controller.asignacion.sin.permiso", null, LocaleContextHolder.getLocale()));
      }
      if (asignacion.getEstado() != AsignacionEstado.PENDIENTE) {
        throw new BusinessException(
            messageSource.getMessage(
                "controller.asignacion.ya.respondida", null, LocaleContextHolder.getLocale()));
      }
      asignacion.setEstado(AsignacionEstado.ACEPTADA);
      asignacion.setRespondidoEn(java.time.LocalDateTime.now());
      asignacionRepository.save(asignacion);
      ra.addFlashAttribute(
          "success",
          messageSource.getMessage(
              "controller.asignacion.aceptada", null, LocaleContextHolder.getLocale()));
      // Notificación para el admin que asignó
      // Suponiendo que el partido tiene un campo para el admin asignador (debería agregarse en el
      // modelo si no existe)
      // Aquí se busca el admin por algún criterio, por ahora se toma el primero activo
      // Notificación para el admin asignador real
      String adminUsername = asignacion.getAdminUsername();
      com.caba.caba_pro.models.Administrador admin =
          administradorRepository.findByUsername(adminUsername);
      if (admin != null) {
        String mensajeAdmin =
            messageSource.getMessage(
                "controller.asignacion.notif.admin.aceptada",
                new Object[] {arbitro.getNombreCompleto(), asignacion.getPartido().getNombre()},
                LocaleContextHolder.getLocale());
        com.caba.caba_pro.models.Notificacion notificacion =
            new com.caba.caba_pro.models.Notificacion();
        notificacion.setMensaje(mensajeAdmin);
        notificacion.setTipo("ADMIN");
        notificacion.setUsuarioId(admin.getId());
        notificacion.setAsignacionId(asignacion.getId());
        notificacionService.crearNotificacion(notificacion);
        // Notificación para el árbitro, personalizada con el nombre del admin
        String mensajeArbitro =
            messageSource.getMessage(
                "controller.asignacion.notif.arbitro.aceptada",
                new Object[] {asignacion.getPartido().getNombre(), adminUsername},
                LocaleContextHolder.getLocale());
        com.caba.caba_pro.models.Notificacion notificacionArbitro =
            new com.caba.caba_pro.models.Notificacion();
        notificacionArbitro.setMensaje(mensajeArbitro);
        notificacionArbitro.setTipo("ARBITRO");
        notificacionArbitro.setUsuarioId(arbitro.getId());
        notificacionArbitro.setAsignacionId(asignacion.getId());
        notificacionService.crearNotificacion(notificacionArbitro);
      }
    } catch (BusinessException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/arbitro/asignaciones";
  }

  // Rechazar asignación
  @PostMapping("/arbitro/asignaciones/{id}/rechazar")
  public String rechazarAsignacion(
      @PathVariable Long id, Principal principal, RedirectAttributes ra) {
    try {
      Arbitro arbitro = arbitroService.buscarPorUsername(principal.getName());
      Asignacion asignacion =
          asignacionRepository
              .findById(id)
              .orElseThrow(
                  () ->
                      new BusinessException(
                          messageSource.getMessage(
                              "controller.asignacion.no.encontrada",
                              null,
                              LocaleContextHolder.getLocale())));
      if (!asignacion.getArbitro().getId().equals(arbitro.getId())) {
        throw new BusinessException(
            messageSource.getMessage(
                "controller.asignacion.sin.permiso", null, LocaleContextHolder.getLocale()));
      }
      if (asignacion.getEstado() != AsignacionEstado.PENDIENTE) {
        throw new BusinessException(
            messageSource.getMessage(
                "controller.asignacion.ya.respondida", null, LocaleContextHolder.getLocale()));
      }
      // Regla de negocio: solo puede rechazar si faltan 48 horas o más
      java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
      java.time.LocalDateTime fechaPartido = asignacion.getPartido().getFechaHora();
      java.time.Duration diferencia = java.time.Duration.between(ahora, fechaPartido);
      if (diferencia.toHours() < 48) {
        ra.addFlashAttribute(
            "error",
            messageSource.getMessage(
                "controller.asignacion.rechazo.48h", null, LocaleContextHolder.getLocale()));
        return "redirect:/arbitro/asignaciones";
      }
      asignacion.setEstado(AsignacionEstado.RECHAZADA);
      asignacion.setRespondidoEn(ahora);
      asignacionRepository.save(asignacion);
      ra.addFlashAttribute(
          "success",
          messageSource.getMessage(
              "controller.asignacion.rechazada", null, LocaleContextHolder.getLocale()));
      // Notificación para el admin que asignó
      // Notificación para el admin asignador real
      String adminUsername = asignacion.getAdminUsername();
      com.caba.caba_pro.models.Administrador admin =
          administradorRepository.findByUsername(adminUsername);
      if (admin != null) {
        String mensajeAdmin =
            messageSource.getMessage(
                "controller.asignacion.notif.admin.rechazada",
                new Object[] {arbitro.getNombreCompleto(), asignacion.getPartido().getNombre()},
                LocaleContextHolder.getLocale());
        com.caba.caba_pro.models.Notificacion notificacion =
            new com.caba.caba_pro.models.Notificacion();
        notificacion.setMensaje(mensajeAdmin);
        notificacion.setTipo("ADMIN");
        notificacion.setUsuarioId(admin.getId());
        notificacion.setAsignacionId(asignacion.getId());
        notificacionService.crearNotificacion(notificacion);
        // Notificación para el árbitro, personalizada con el nombre del admin
        String mensajeArbitro =
            messageSource.getMessage(
                "controller.asignacion.notif.arbitro.rechazada",
                new Object[] {asignacion.getPartido().getNombre(), adminUsername},
                LocaleContextHolder.getLocale());
        com.caba.caba_pro.models.Notificacion notificacionArbitro =
            new com.caba.caba_pro.models.Notificacion();
        notificacionArbitro.setMensaje(mensajeArbitro);
        notificacionArbitro.setTipo("ARBITRO");
        notificacionArbitro.setUsuarioId(arbitro.getId());
        notificacionArbitro.setAsignacionId(asignacion.getId());
        notificacionService.crearNotificacion(notificacionArbitro);
      }
    } catch (BusinessException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/arbitro/asignaciones";
  }

  // Completar asignación (marcar como COMPLETADA después del partido)
  @PostMapping("/arbitro/asignaciones/{id}/completar")
  public String completarAsignacion(
      @PathVariable Long id, Principal principal, RedirectAttributes ra) {
    try {
      Arbitro arbitro = arbitroService.buscarPorUsername(principal.getName());
      Asignacion asignacion =
          asignacionRepository
              .findById(id)
              .orElseThrow(() -> new BusinessException("Asignación no encontrada"));

      if (!asignacion.getArbitro().getId().equals(arbitro.getId())) {
        throw new BusinessException("No tienes permiso para modificar esta asignación");
      }

      if (asignacion.getEstado() != AsignacionEstado.ACEPTADA) {
        throw new BusinessException("Solo puedes completar asignaciones aceptadas");
      }

      // Verificar que el partido ya haya pasado
      java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
      java.time.LocalDateTime fechaPartido = asignacion.getPartido().getFechaHora();

      if (fechaPartido.isAfter(ahora)) {
        ra.addFlashAttribute(
            "error",
            "No puedes marcar como completada una asignación de un partido que aún no ha comenzado.");
        return "redirect:/arbitro/asignaciones";
      }

      asignacion.setEstado(AsignacionEstado.COMPLETADA);
      asignacionRepository.save(asignacion);

      ra.addFlashAttribute(
          "success", "Asignación marcada como completada. Ya es elegible para liquidación.");

      // Notificación para el admin
      String adminUsername = asignacion.getAdminUsername();
      com.caba.caba_pro.models.Administrador admin =
          administradorRepository.findByUsername(adminUsername);
      if (admin != null) {
        String mensajeAdmin =
            "El árbitro '"
                + arbitro.getNombreCompleto()
                + "' ha completado el partido '"
                + asignacion.getPartido().getNombre()
                + "'.";
        com.caba.caba_pro.models.Notificacion notificacion =
            new com.caba.caba_pro.models.Notificacion();
        notificacion.setMensaje(mensajeAdmin);
        notificacion.setTipo("ADMIN");
        notificacion.setUsuarioId(admin.getId());
        notificacion.setAsignacionId(asignacion.getId());
        notificacionService.crearNotificacion(notificacion);
      }

    } catch (BusinessException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/arbitro/asignaciones";
  }
}
