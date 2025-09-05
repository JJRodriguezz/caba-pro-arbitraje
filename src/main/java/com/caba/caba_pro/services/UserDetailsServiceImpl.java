/**
 * Archivo: UserDetailsServiceImpl.java Autores: Isabella.Idarraga Fecha última modificación: [04.09.2025]
 * Descripción: Implementación de UserDetailsService para la autenticación de usuarios Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.models.Usuario;
import com.caba.caba_pro.repositories.UsuarioRepository;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

  private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

  @Autowired private UsuarioRepository usuarioRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío");
    }

    logger.info("Intentando autenticar usuario: {}", username);

    Usuario usuario = usuarioRepository.findByUsername(username);
    if (usuario == null) {
      logger.warn("Usuario no encontrado: {}", username);
      throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }

    Set<GrantedAuthority> authorities = new HashSet<>();
    GrantedAuthority authority = new SimpleGrantedAuthority(usuario.getRole());
    authorities.add(authority);

    logger.info("Usuario {} autenticado exitosamente con rol {}", username, usuario.getRole());

    return new User(username, usuario.getPassword(), authorities);
  }
}
