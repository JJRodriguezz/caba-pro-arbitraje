/**
 * Archivo: AsignacionRepository.java Autores: JJRodriguezz Fecha última modificación: 06.09.2025
 * Descripción: Acceso a datos para asignaciones de árbitros. Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.repositories;

import com.caba.caba_pro.models.Asignacion;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

  List<Asignacion> findByPartidoIdAndActivoTrue(Long partidoId);

  // Buscar asignaciones por árbitro
  List<Asignacion> findByArbitroIdAndActivoTrue(Long arbitroId);

  // Buscar todas las asignaciones del árbitro (incluyendo rechazadas) para el historial
  List<Asignacion> findByArbitroIdOrderByAsignadoEnDesc(Long arbitroId);

  boolean existsByPartidoIdAndArbitroIdAndActivoTrue(Long partidoId, Long arbitroId);

  boolean existsByPartidoIdAndPosicionAndActivoTrue(Long partidoId, String posicion);

  // Regla: el árbitro no puede tener más de una asignación activa el MISMO día
  boolean existsByArbitroIdAndActivoTrueAndPartido_FechaHoraBetween(
      Long arbitroId, LocalDateTime inicio, LocalDateTime fin);
}
