/**
 * Archivo: RegistroService.java Autores: Isabella.Idarraga Fecha última modificación: [06.09.2025]
 * Descripción: Servicio para la gestión de registros de administradores en la aplicación Proyecto:
 * CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

// 2. Librerías externas
import com.caba.caba_pro.DTOs.RegistroForm;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Administrador;
import com.caba.caba_pro.repositories.AdministradorRepository;
import com.caba.caba_pro.repositories.ArbitroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegistroService {

  // 1. Constantes estáticas
  private static final Logger logger = LoggerFactory.getLogger(RegistroService.class);

  // 2. Variables de instancia
  private final AdministradorRepository administradorRepository;
  private final ArbitroRepository arbitroRepository;
  private final PasswordEncoder passwordEncoder;

  // 3. Constructores
  public RegistroService(
      AdministradorRepository administradorRepository,
      ArbitroRepository arbitroRepository,
      PasswordEncoder passwordEncoder) {
    this.administradorRepository = administradorRepository;
    this.arbitroRepository = arbitroRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // 4. Métodos públicos
  public Administrador registrarAdministrador(RegistroForm registroForm) {

    logger.info("Iniciando registro de administrador: {}", registroForm.getUsername());

    // Validar que el administrador no exista
    if (administradorRepository.existsByUsername(registroForm.getUsername())) {
      logger.warn("Administrador ya existe: {}", registroForm.getUsername());
      throw new BusinessException(
          "El nombre de usuario '"
              + registroForm.getUsername()
              + "' ya está registrado en el sistema");
    }

    // Validar que el username no esté siendo usado por un árbitro
    if (arbitroRepository.existsByUsername(registroForm.getUsername())) {
      logger.warn("Username ya usado por árbitro: {}", registroForm.getUsername());
      throw new BusinessException(
          "El nombre de usuario '"
              + registroForm.getUsername()
              + "' ya está registrado en el sistema");
    }

    // Crear y mapear el administrador
    Administrador administrador = mapearFormAAdministrador(registroForm);
    logger.info("Administrador mapeado: {}", administrador.getUsername());

    // Persistir el administrador
    Administrador administradorGuardado = administradorRepository.save(administrador);
    logger.info("Administrador guardado exitosamente con ID: {}", administradorGuardado.getId());

    return administradorGuardado;
  }

  // Métodos de mapeo privados
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
