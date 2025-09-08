/**
 * Archivo: TarifaRepository.java Autores: JJRodriguezz Fecha última modificación: 06.09.2025
 * Descripción: Acceso a datos para tarifas por escalafón. Proyecto: CABA Pro - Sistema de Gestión
 * Integral de Arbitraje
 */
package com.caba.caba_pro.repositories;

import com.caba.caba_pro.models.Tarifa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarifaRepository extends JpaRepository<Tarifa, Long> {

  List<Tarifa> findByActivoTrueOrderByEscalafonAsc();

  Optional<Tarifa> findByEscalafonIgnoreCaseAndActivoTrue(String escalafon);
}
