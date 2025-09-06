/**
 * Archivo: PartidoRepository.java Autores: Juan José Fecha última modificación: 05.09.2025
 * Descripción: Acceso a datos para partidos. Proyecto: CABA Pro - Sistema de Gestión Integral de
 * Arbitraje
 */
package com.caba.caba_pro.repositories;

import com.caba.caba_pro.models.Partido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartidoRepository extends JpaRepository<Partido, Long> {

  List<Partido> findByActivoTrue();

  Optional<Partido> findByIdAndActivoTrue(Long id);
}
