/**
 * Archivo: ArbitroService.java Autores: Isabella.Idarraga, Diego.Gonzalez, JJRodriguezz Fecha
 * última modificación: [10.09.2025] Descripción: Servicio para la gestión de árbitros en la
 * aplicación Proyecto: CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

// 1. Java estándar
import com.caba.caba_pro.DTOs.ArbitroDto;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.repositories.AdministradorRepository;
import com.caba.caba_pro.repositories.ArbitroRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ArbitroService {

  // 1. Constantes estáticas
  private static final Logger logger = LoggerFactory.getLogger(ArbitroService.class);

  // 2. Variables de instancia
  private final ArbitroRepository arbitroRepository;
  private final AdministradorRepository administradorRepository;
  private final PasswordEncoder passwordEncoder;
  private final FileUploadService fileUploadService;

  // 3. Constructor
  public ArbitroService(
      ArbitroRepository arbitroRepository,
      AdministradorRepository administradorRepository,
      PasswordEncoder passwordEncoder,
      FileUploadService fileUploadService) {
    this.arbitroRepository = arbitroRepository;
    this.administradorRepository = administradorRepository;
    this.passwordEncoder = passwordEncoder;
    this.fileUploadService = fileUploadService;
  }

  // 4. Métodos públicos

  // Buscar árbitro por username.
  @Transactional(readOnly = true)
  public Arbitro buscarPorUsername(String username) {
    Arbitro arbitro = arbitroRepository.findByUsername(username);
    if (arbitro == null || Boolean.FALSE.equals(arbitro.getActivo())) {
      throw new BusinessException("Árbitro no encontrado o inactivo");
    }
    return arbitro;
  }

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

  public Arbitro crearArbitro(ArbitroDto arbitroDto, MultipartFile fotoPerfil) {
    logger.info("Creando árbitro: {}", arbitroDto.getUsername());

    // Validaciones de negocio
    validarDatosArbitro(arbitroDto);

    // Crear árbitro
    Arbitro arbitro = mapearDtoAArbitro(arbitroDto);

    // Procesar foto de perfil si se envió (con validaciones de seguridad)
    if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
      String urlFotoPerfil = fileUploadService.guardarFotoPerfil(fotoPerfil);
      arbitro.setUrlFotoPerfil(urlFotoPerfil);
    }

    // Establecer contraseña encriptada y rol
    arbitro.setPassword(passwordEncoder.encode(arbitroDto.getPassword()));
    arbitro.setRole("ROLE_ARBITRO");
    arbitro.setActivo(true);

    arbitro = arbitroRepository.save(arbitro);
    logger.info("Árbitro creado exitosamente con ID: {}", arbitro.getId());

    return arbitro;
  }

  public Arbitro actualizarArbitro(
      Long id, ArbitroDto arbitroDto, org.springframework.web.multipart.MultipartFile fotoPerfil) {
    logger.info("=== SERVICIO: Actualizando árbitro con ID: {} ===", id);
    logger.info(
        "SERVICIO: Datos recibidos - Nombre: {}, Username: {}",
        arbitroDto.getNombre(),
        arbitroDto.getUsername());
    logger.info(
        "SERVICIO: Foto recibida: {}",
        fotoPerfil != null && !fotoPerfil.isEmpty()
            ? fotoPerfil.getOriginalFilename()
            : "Sin foto");

    Arbitro arbitroExistente = buscarPorId(id);
    logger.info("SERVICIO: Árbitro existente encontrado: {}", arbitroExistente.getNombreCompleto());

    // Validar si el username cambió y no existe en otro árbitro/administrador
    if (!arbitroExistente.getUsername().equals(arbitroDto.getUsername())) {
      logger.info(
          "SERVICIO: Username cambió de '{}' a '{}'",
          arbitroExistente.getUsername(),
          arbitroDto.getUsername());
      if (arbitroRepository.existsByUsernameAndActivoTrueAndIdNot(arbitroDto.getUsername(), id)) {
        throw new BusinessException("Ya existe un árbitro con ese nombre de usuario");
      }
      if (administradorRepository.existsByUsername(arbitroDto.getUsername())) {
        throw new BusinessException("Ya existe un administrador con ese nombre de usuario");
      }
    }

    // Validar si el email cambió y no existe en otro árbitro
    if (!arbitroExistente.getEmail().equals(arbitroDto.getEmail())) {
      logger.info(
          "SERVICIO: Email cambió de '{}' a '{}'",
          arbitroExistente.getEmail(),
          arbitroDto.getEmail());
      if (arbitroRepository.existsByEmailAndActivoTrueAndIdNot(arbitroDto.getEmail(), id)) {
        throw new BusinessException("Ya existe un árbitro con ese email");
      }
    }

    // Validar si el número de identificación cambió y no existe en otro árbitro
    if (!arbitroExistente.getNumeroIdentificacion().equals(arbitroDto.getNumeroIdentificacion())) {
      logger.info(
          "SERVICIO: Número identificación cambió de '{}' a '{}'",
          arbitroExistente.getNumeroIdentificacion(),
          arbitroDto.getNumeroIdentificacion());
      if (arbitroRepository.existsByNumeroIdentificacionAndIdNot(
          arbitroDto.getNumeroIdentificacion(), id)) {
        throw new BusinessException("Ya existe un árbitro con ese número de identificación");
      }
    }

    // Actualizar datos del árbitro
    logger.info("SERVICIO: Actualizando datos del árbitro...");
    actualizarDatosArbitro(arbitroExistente, arbitroDto);

    // Procesar foto de perfil si se envió
    if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
      logger.info("SERVICIO: Procesando foto de perfil...");

      // Eliminar foto anterior si existe (usando el servicio)
      if (arbitroExistente.getUrlFotoPerfil() != null
          && !arbitroExistente.getUrlFotoPerfil().isEmpty()) {
        fileUploadService.eliminarFotoPerfil(arbitroExistente.getUrlFotoPerfil());
        logger.info("Foto anterior eliminada: {}", arbitroExistente.getUrlFotoPerfil());
      }

      // Guardar nueva foto con validaciones de seguridad
      String urlFotoPerfil = fileUploadService.guardarFotoPerfil(fotoPerfil);
      arbitroExistente.setUrlFotoPerfil(urlFotoPerfil);
      logger.info("SERVICIO: Nueva foto guardada con URL: {}", urlFotoPerfil);
    } else {
      logger.info(
          "SERVICIO: No se envió nueva foto, manteniendo la actual: {}",
          arbitroExistente.getUrlFotoPerfil());
    }

    Arbitro arbitroActualizado = arbitroRepository.save(arbitroExistente);
    logger.info(
        "SERVICIO: Árbitro guardado exitosamente - Nombre: {}, URL Foto: {}",
        arbitroActualizado.getNombreCompleto(),
        arbitroActualizado.getUrlFotoPerfil());

    return arbitroActualizado;
  }

  public void eliminarArbitro(Long id) {
    Arbitro arbitro = buscarPorId(id);
    arbitro.setActivo(false); // Soft delete
    arbitroRepository.save(arbitro);
    logger.info("Árbitro desactivado: {}", id);
  }

  // Métodos de validación privados

  private void validarDatosArbitro(ArbitroDto dto) {
    // Validar que no exista árbitro con mismo número de identificación
    if (arbitroRepository.existsByNumeroIdentificacion(dto.getNumeroIdentificacion())) {
      throw new BusinessException("Ya existe un árbitro con ese número de identificación");
    }

    // Validar que no exista árbitro con mismo email
    if (arbitroRepository.existsByEmailAndActivoTrue(dto.getEmail())) {
      throw new BusinessException("Ya existe un árbitro con ese email");
    }

    // Validar que no exista árbitro con mismo username
    if (arbitroRepository.existsByUsernameAndActivoTrue(dto.getUsername())) {
      throw new BusinessException("Ya existe un árbitro con ese nombre de usuario");
    }

    // Validar que no exista administrador con mismo username
    if (administradorRepository.existsByUsername(dto.getUsername())) {
      throw new BusinessException("Ya existe un administrador con ese nombre de usuario");
    }
  }

  private Arbitro mapearDtoAArbitro(ArbitroDto dto) {
    Arbitro arbitro = new Arbitro();
    arbitro.setNombre(dto.getNombre());
    arbitro.setApellidos(dto.getApellidos());
    arbitro.setUsername(dto.getUsername());
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
    logger.info(
        "SERVICIO: Actualizando datos - Antes: Nombre={}, Username={}",
        arbitro.getNombre(),
        arbitro.getUsername());
    logger.info(
        "SERVICIO: Nuevos datos - Nombre={}, Username={}", dto.getNombre(), dto.getUsername());

    arbitro.setNombre(dto.getNombre());
    arbitro.setApellidos(dto.getApellidos());
    arbitro.setUsername(dto.getUsername());
    arbitro.setNumeroIdentificacion(dto.getNumeroIdentificacion());
    arbitro.setEmail(dto.getEmail());
    arbitro.setTelefono(dto.getTelefono());
    arbitro.setEspecialidad(dto.getEspecialidad());
    arbitro.setEscalafon(dto.getEscalafon());
    arbitro.setFechaNacimiento(dto.getFechaNacimiento());

    logger.info(
        "SERVICIO: Datos actualizados - Después: Nombre={}, Username={}",
        arbitro.getNombre(),
        arbitro.getUsername());
  }
}
