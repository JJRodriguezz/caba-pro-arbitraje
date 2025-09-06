/**
 * Archivo: TorneoRepository.java Autores: Diego.Gonzalez Fecha última modificación: [06.09.2025]
 * Descripción: Repositorio para la gestión de torneos en la aplicación Proyecto: CABA Pro - Sistema
 * de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.repositories;

import com.caba.caba_pro.enums.TorneoEstado;
import com.caba.caba_pro.models.Torneo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Long> {

  // Búsquedas específicas del dominio
  List<Torneo> findByActivoTrue();

  List<Torneo> findByEstadoAndActivoTrue(TorneoEstado estado);

  List<Torneo> findByActivoTrueOrderByFechaInicioDesc();

  // Validaciones
  boolean existsByNombreAndActivoTrue(String nombre);

  // Búsquedas por fechas
  List<Torneo> findByFechaInicioBetweenAndActivoTrue(LocalDate fechaInicio, LocalDate fechaFin);

  List<Torneo> findByFechaFinBeforeAndActivoTrue(LocalDate fecha);

  // Query personalizada para torneos activos
  @Query("SELECT t FROM Torneo t WHERE t.activo = true AND t.fechaCreacion >= :fecha")
  List<Torneo> findTorneosActivosDesde(@Param("fecha") LocalDateTime fecha);

  // Búsqueda por ubicación
  List<Torneo> findByUbicacionContainingIgnoreCaseAndActivoTrue(String ubicacion);
}
