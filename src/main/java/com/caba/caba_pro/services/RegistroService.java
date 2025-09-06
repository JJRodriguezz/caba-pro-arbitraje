/**
 * Archivo: RegistroService.java Autores: Diego.Gonzalez Fecha última modificación: [06.09.2025]
 * Descripción: Servicio para la gestión de registros de administradores en la aplicación Proyecto:
 * CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.DTOs.RegistroForm;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Administrador;
import com.caba.caba_pro.repositories.AdministradorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegistroService {

  private static final Logger logger = LoggerFactory.getLogger(RegistroService.class);

  @Autowired private AdministradorRepository administradorRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  public Administrador registrarAdministrador(RegistroForm registroForm) {

    logger.info("Iniciando registro de administrador: {}", registroForm.getUsername());

    // Validar que el administrador no exista
    if (administradorRepository.existsByUsername(registroForm.getUsername())) {
      logger.warn("Administrador ya existe: {}", registroForm.getUsername());
      throw new BusinessException("Ya existe un administrador con ese nombre");
    }

    // Crear y mapear el administrador
    Administrador administrador = mapearFormAAdministrador(registroForm);
    logger.info("Administrador mapeado: {}", administrador.getUsername());

    // Persistir el administrador
    Administrador administradorGuardado = administradorRepository.save(administrador);
    logger.info("Administrador guardado exitosamente con ID: {}", administradorGuardado.getId());

    return administradorGuardado;
  }

  private Administrador mapearFormAAdministrador(RegistroForm form) {
    Administrador administrador = new Administrador();
    administrador.setUsername(form.getUsername());
    administrador.setPassword(passwordEncoder.encode(form.getPassword()));
    administrador.setRole("ROLE_ADMIN");
    logger.debug(
        "Administrador mapeado: username={}, role={}",
        administrador.getUsername(),
        administrador.getRole());
    return administrador;
  }
}
