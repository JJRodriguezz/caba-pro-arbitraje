/**
 * Archivo: NotificacionService.java Autores: JJRodriguezz Fecha última modificación: 07.09.2025
 * Descripción: Servicio para la gestión de notificaciones de asignaciones y respuestas. Proyecto:
 * CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Notificacion;
import com.caba.caba_pro.repositories.NotificacionRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificacionService {
  private static final Logger logger = LoggerFactory.getLogger(NotificacionService.class);
  private final NotificacionRepository notificacionRepository;

  public NotificacionService(NotificacionRepository notificacionRepository) {
    this.notificacionRepository = notificacionRepository;
  }

  // Crear notificación
  public Notificacion crearNotificacion(Notificacion notificacion) {
    logger.info(
        "Creando notificación para usuario {}: {}",
        notificacion.getUsuarioId(),
        notificacion.getMensaje());
    return notificacionRepository.save(notificacion);
  }

  // Marcar notificación como leída
  public void marcarComoLeida(Long id) {
    Notificacion notificacion =
        notificacionRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException("Notificación no encontrada"));
    notificacion.setLeida(true);
    notificacionRepository.save(notificacion);
    logger.info("Notificación marcada como leída: {}", id);
  }

  // Obtener notificaciones por usuario y tipo
  @Transactional(readOnly = true)
  public List<Notificacion> obtenerNotificaciones(Long usuarioId, String tipo) {
    return notificacionRepository.findByUsuarioIdAndTipoOrderByFechaCreacionDesc(usuarioId, tipo);
  }

  // Obtener notificaciones no leídas
  @Transactional(readOnly = true)
  public List<Notificacion> obtenerNoLeidas(Long usuarioId, String tipo) {
    return notificacionRepository.findByUsuarioIdAndTipoAndLeidaFalseOrderByFechaCreacionDesc(
        usuarioId, tipo);
  }
}
