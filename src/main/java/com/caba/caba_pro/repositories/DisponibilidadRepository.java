/**
 * Archivo: DisponibilidadRepository.java Autores: JJRodriguezz Fecha última modificación:
 * 10.09.2025 Descripción: Repositorio para la gestión de disponibilidad de árbitros Proyecto: CABA
 * Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.repositories;

import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.models.Disponibilidad;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

  /** Busca la disponibilidad de un árbitro específico */
  Optional<Disponibilidad> findByArbitro(Arbitro arbitro);

  /** Busca la disponibilidad por ID del árbitro */
  Optional<Disponibilidad> findByArbitroId(Long arbitroId);

  /** Verifica si existe disponibilidad para un árbitro */
  boolean existsByArbitro(Arbitro arbitro);

  /** Elimina la disponibilidad de un árbitro */
  void deleteByArbitro(Arbitro arbitro);
}
