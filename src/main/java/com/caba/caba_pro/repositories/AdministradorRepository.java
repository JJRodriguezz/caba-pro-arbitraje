/**
 * Archivo: AdministradorRepository.java Autores: Diego.Gonzalez Fecha última modificación:
 * [06.09.2025] Descripción: Repositorio para la gestión de administradores Proyecto: CABA Pro -
 * Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.repositories;

import com.caba.caba_pro.models.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

  Administrador findByUsername(String username);

  boolean existsByUsername(String username);

  boolean existsByUsernameAndActivoTrue(String username);
}
