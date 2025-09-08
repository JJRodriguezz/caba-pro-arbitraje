/**
 * Archivo: NotificacionRepository.java Autores: JJRodriguezz Fecha última modificación: 07.09.2025
 * Descripción: Acceso a datos para notificaciones de asignaciones y respuestas. Proyecto: CABA Pro
 * - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.repositories;

import com.caba.caba_pro.models.Notificacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
  // Buscar notificaciones por usuario y tipo
  List<Notificacion> findByUsuarioIdAndTipoOrderByFechaCreacionDesc(Long usuarioId, String tipo);

  // Buscar notificaciones no leídas
  List<Notificacion> findByUsuarioIdAndTipoAndLeidaFalseOrderByFechaCreacionDesc(
      Long usuarioId, String tipo);
}
