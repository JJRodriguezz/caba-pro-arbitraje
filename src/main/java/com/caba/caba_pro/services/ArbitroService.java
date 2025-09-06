/**
 * Archivo: ArbitroService.java Autores: Isabella.Idarraga Fecha última modificación: [04.09.2025]
 * Descripción: Servicio para la gestión de árbitros en la aplicación Proyecto: CABA Pro - Sistema
 * de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.DTOs.ArbitroDto;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.models.Usuario;
import com.caba.caba_pro.repositories.ArbitroRepository;
import com.caba.caba_pro.repositories.UsuarioRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ArbitroService {

  private static final Logger logger = LoggerFactory.getLogger(ArbitroService.class);

  @Autowired private ArbitroRepository arbitroRepository;

  @Autowired private UsuarioRepository usuarioRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Transactional(readOnly = true)
  public List<Arbitro> buscarTodosActivos() {
    return arbitroRepository.findByActivoTrue();
  }

  @Transactional(readOnly = true)
  public Arbitro buscarPorId(Long id) {
    return arbitroRepository
        .findById(id)
        .orElseThrow(() -> new BusinessException("Árbitro no encontrado con ID: " + id));
  }

  public Arbitro crearArbitro(ArbitroDto arbitroDto) {
    logger.info("Creando árbitro: {}", arbitroDto.getUsername());

    // Validaciones de negocio
    validarDatosArbitro(arbitroDto);

    // Crear usuario para el árbitro
    Usuario usuario = crearUsuarioParaArbitro(arbitroDto);

    // Crear árbitro
    Arbitro arbitro = mapearDtoAArbitro(arbitroDto);

    // Asociar usuario al árbitro (si tienes esta relación)
    // arbitro.setUsuario(usuario);

    arbitro = arbitroRepository.save(arbitro);
    logger.info("Árbitro creado exitosamente con ID: {}", arbitro.getId());

    return arbitro;
  }

  public Arbitro actualizarArbitro(Long id, ArbitroDto arbitroDto) {
    Arbitro arbitroExistente = buscarPorId(id);

    // Validar si el username cambió y no existe en otro usuario
    if (!arbitroExistente.getEmail().equals(arbitroDto.getEmail())) {
      if (usuarioRepository.existsByUsername(arbitroDto.getUsername())) {
        throw new BusinessException("Ya existe un usuario con ese nombre");
      }
    }

    // Actualizar datos del árbitro
    actualizarDatosArbitro(arbitroExistente, arbitroDto);

    return arbitroRepository.save(arbitroExistente);
  }

  public void eliminarArbitro(Long id) {
    Arbitro arbitro = buscarPorId(id);
    arbitro.setActivo(false); // Soft delete
    arbitroRepository.save(arbitro);
    logger.info("Árbitro desactivado: {}", id);
  }

  private void validarDatosArbitro(ArbitroDto dto) {
    // Validar que no exista árbitro con mismo número de identificación
    if (arbitroRepository.existsByNumeroIdentificacion(dto.getNumeroIdentificacion())) {
      throw new BusinessException("Ya existe un árbitro con ese número de identificación");
    }

    // Validar que no exista árbitro con mismo email
    if (arbitroRepository.existsByEmailAndActivoTrue(dto.getEmail())) {
      throw new BusinessException("Ya existe un árbitro con ese email");
    }

    // Validar que no exista usuario con ese username
    if (usuarioRepository.existsByUsername(dto.getUsername())) {
      throw new BusinessException("Ya existe un usuario con ese nombre");
    }
  }

  private Usuario crearUsuarioParaArbitro(ArbitroDto dto) {
    Usuario usuario = new Usuario();
    usuario.setUsername(dto.getUsername());
    usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
    usuario.setRole("ROLE_USER"); // Los árbitros tienen rol de usuario
    usuario.setActivo(true);

    usuario = usuarioRepository.save(usuario);
    logger.info("Usuario creado para árbitro: {}", usuario.getUsername());

    return usuario;
  }

  private Arbitro mapearDtoAArbitro(ArbitroDto dto) {
    Arbitro arbitro = new Arbitro();
    arbitro.setNombre(dto.getNombre());
    arbitro.setApellidos(dto.getApellidos());
    arbitro.setNumeroIdentificacion(dto.getNumeroIdentificacion());
    arbitro.setEmail(dto.getEmail());
    arbitro.setTelefono(dto.getTelefono());
    arbitro.setEspecialidad(dto.getEspecialidad());
    arbitro.setEscalafon(dto.getEscalafon());
    arbitro.setFechaNacimiento(dto.getFechaNacimiento());
    arbitro.setActivo(true);

    return arbitro;
  }

  private void actualizarDatosArbitro(Arbitro arbitro, ArbitroDto dto) {
    arbitro.setNombre(dto.getNombre());
    arbitro.setApellidos(dto.getApellidos());
    arbitro.setNumeroIdentificacion(dto.getNumeroIdentificacion());
    arbitro.setEmail(dto.getEmail());
    arbitro.setTelefono(dto.getTelefono());
    arbitro.setEspecialidad(dto.getEspecialidad());
    arbitro.setEscalafon(dto.getEscalafon());
    arbitro.setFechaNacimiento(dto.getFechaNacimiento());
  }
}
