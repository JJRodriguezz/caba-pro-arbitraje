/**
 * Archivo: UserDetailsServiceImpl.java Autores: Diego.Gonzalez Fecha última modificación:
 * [06.09.2025] Descripción: Implementación de UserDetailsService para la autenticación de
 * administradores y árbitros Proyecto: CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

// 1. Java estándar
import com.caba.caba_pro.models.Administrador;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.repositories.AdministradorRepository;
import com.caba.caba_pro.repositories.ArbitroRepository;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

  // 1. Constantes estáticas
  private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

  // 2. Variables de instancia
  private final AdministradorRepository administradorRepository;
  private final ArbitroRepository arbitroRepository;

  // 3. Constructores
  public UserDetailsServiceImpl(
      AdministradorRepository administradorRepository, ArbitroRepository arbitroRepository) {
    this.administradorRepository = administradorRepository;
    this.arbitroRepository = arbitroRepository;
  }

  // 4. Métodos públicos

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío");
    }

    logger.info("Intentando autenticar usuario: {}", username);

    // Buscar primero como administrador por username
    Administrador administrador = administradorRepository.findByUsername(username);
    if (administrador != null && administrador.isActivo()) {
      Set<GrantedAuthority> authorities = new HashSet<>();
      GrantedAuthority authority = new SimpleGrantedAuthority(administrador.getRole());
      authorities.add(authority);

      logger.info(
          "Administrador {} autenticado exitosamente con rol {}",
          username,
          administrador.getRole());
      return new User(username, administrador.getPassword(), authorities);
    }

    // Buscar como árbitro por email
    Arbitro arbitro = arbitroRepository.findByEmail(username);
    if (arbitro != null && arbitro.isActivo()) {
      Set<GrantedAuthority> authorities = new HashSet<>();
      GrantedAuthority authority = new SimpleGrantedAuthority(arbitro.getRole());
      authorities.add(authority);

      logger.info("Árbitro {} autenticado exitosamente con rol {}", username, arbitro.getRole());
      return new User(username, arbitro.getPassword(), authorities);
    }

    logger.warn("Usuario no encontrado: {}", username);
    throw new UsernameNotFoundException("Usuario no encontrado: " + username);
  }
}
