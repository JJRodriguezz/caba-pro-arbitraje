/**
 * Archivo: ArbitroRepository.java Autores: Isabella.Idarraga Fecha última modificación:
 * [04.09.2025] Descripción: Repositorio para la gestión de árbitros en la aplicación Proyecto: CABA
 * Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.repositories;

import com.caba.caba_pro.enums.Especialidad;
import com.caba.caba_pro.models.Arbitro;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArbitroRepository extends JpaRepository<Arbitro, Long> {

  // Búsquedas específicas del dominio
  List<Arbitro> findByEspecialidadAndActivoTrue(Especialidad especialidad);

  List<Arbitro> findByEscalafonAndActivoTrue(String escalafon);

  List<Arbitro> findByActivoTrue();

  // Validaciones
  boolean existsByNumeroIdentificacion(String numeroIdentificacion);

  boolean existsByEmailAndActivoTrue(String email);

  boolean existsByUsernameAndActivoTrue(String username);

  // Búsqueda para autenticación
  Arbitro findByEmail(String email);

  Arbitro findByUsername(String username);

  // Query personalizada para árbitros disponibles
  @Query("SELECT a FROM Arbitro a WHERE a.activo = true AND a.fechaCreacion >= :fecha")
  List<Arbitro> findArbitrosActivosDesde(@Param("fecha") LocalDateTime fecha);
}
