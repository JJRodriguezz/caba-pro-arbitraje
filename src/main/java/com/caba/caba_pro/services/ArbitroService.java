/**
 * Archivo: ArbitroService.java Autores:Isabella.Idarraga & Diego.Gonzalez Fecha última
 * modificación: [06.09.2025] Descripción: Servicio para la gestión de árbitros en la aplicación
 * Proyecto: CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.DTOs.ArbitroDto;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.repositories.ArbitroRepository;
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

    // Crear árbitro
    Arbitro arbitro = mapearDtoAArbitro(arbitroDto);

    // Establecer contraseña encriptada y rol
    arbitro.setPassword(passwordEncoder.encode(arbitroDto.getPassword()));
    arbitro.setRole("ROLE_ARBITRO");
    arbitro.setActivo(true);

    arbitro = arbitroRepository.save(arbitro);
    logger.info("Árbitro creado exitosamente con ID: {}", arbitro.getId());

    return arbitro;
  }

  public Arbitro actualizarArbitro(Long id, ArbitroDto arbitroDto) {
    Arbitro arbitroExistente = buscarPorId(id);

    // Validar si el email cambió y no existe en otro árbitro
    if (!arbitroExistente.getEmail().equals(arbitroDto.getEmail())) {
      if (arbitroRepository.existsByEmailAndActivoTrue(arbitroDto.getEmail())) {
        throw new BusinessException("Ya existe un árbitro con ese email");
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
