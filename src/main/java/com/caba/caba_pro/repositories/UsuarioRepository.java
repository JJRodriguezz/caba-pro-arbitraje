/**
 * Archivo: UsuarioRepository.java Autores: Isabella.Idarraga Fecha última modificación:
 * [04.09.2025] Descripción: Repositorio para la gestión de usuarios Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.repositories;

import com.caba.caba_pro.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  Usuario findByUsername(String username);

  boolean existsByUsername(String username);

  boolean existsByUsernameAndActivoTrue(String username);
}
