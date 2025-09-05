/**
 * Archivo: RegistroService.java Autores: Isabella.Idarraga Fecha última modificación: [04.09.2025]
 * Descripción: Servicio para la gestión de registros en la aplicación Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.DTOs.RegistroForm;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Usuario;
import com.caba.caba_pro.repositories.UsuarioRepository;
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

  @Autowired private UsuarioRepository usuarioRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  public Usuario registrarUsuario(RegistroForm registroForm) {

    logger.info("Iniciando registro de usuario: {}", registroForm.getUsername());

    // Validar parámetros de entrada
    if (registroForm == null) {
      throw new IllegalArgumentException("Los datos de registro no pueden ser nulos");
    }

    // Validar que el usuario no exista
    if (usuarioRepository.existsByUsername(registroForm.getUsername())) {
      logger.warn("Usuario ya existe: {}", registroForm.getUsername());
      throw new BusinessException("Ya existe un usuario con ese nombre");
    }

    // Crear y mapear el usuario
    Usuario usuario = mapearFormAUsuario(registroForm);
    logger.info("Usuario mapeado: {}", usuario.getUsername());

    // Persistir el usuario
    Usuario usuarioGuardado = usuarioRepository.save(usuario);
    logger.info("Usuario guardado exitosamente con ID: {}", usuarioGuardado.getId());

    return usuarioGuardado;
  }

  private Usuario mapearFormAUsuario(RegistroForm form) {
    Usuario usuario = new Usuario();
    usuario.setUsername(form.getUsername());
    usuario.setPassword(passwordEncoder.encode(form.getPassword()));
    usuario.setRole("ROLE_USER");
    logger.debug("Usuario mapeado: username={}, role={}", usuario.getUsername(), usuario.getRole());
    return usuario;
  }
}
