/**
 * Archivo: ArbitroService.java Autores: Isabella.Idarraga, Diego.Gonzalez, JJRodriguezz Fecha
 * última modificación: [10.09.2025] Descripción: Servicio para la gestión de árbitros en la
 * aplicación Proyecto: CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

// 1. Java estándar
import com.caba.caba_pro.DTOs.ArbitroDto;
import com.caba.caba_pro.config.FotoPerfilProperties;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.repositories.AdministradorRepository;
import com.caba.caba_pro.repositories.ArbitroRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
  private final FotoPerfilProperties fotoPerfilProperties;

  // 3. Constructor
  public ArbitroService(
      ArbitroRepository arbitroRepository,
      AdministradorRepository administradorRepository,
      PasswordEncoder passwordEncoder,
      FotoPerfilProperties fotoPerfilProperties) {
    this.arbitroRepository = arbitroRepository;
    this.administradorRepository = administradorRepository;
    this.passwordEncoder = passwordEncoder;
    this.fotoPerfilProperties = fotoPerfilProperties;
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

    // Procesar foto de perfil si se envió
    if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
      try {
        String nombreArchivo = System.currentTimeMillis() + "_" + fotoPerfil.getOriginalFilename();
        String rutaBase = fotoPerfilProperties.getFotosPerfilPath();
        Path rutaDirectorio = Paths.get(rutaBase);
        if (!Files.exists(rutaDirectorio)) {
          Files.createDirectories(rutaDirectorio);
        }
        Path rutaArchivo = rutaDirectorio.resolve(nombreArchivo);
        fotoPerfil.transferTo(rutaArchivo.toFile());
        // Guardar la URL relativa para servir la imagen
        arbitro.setUrlFotoPerfil("/uploads/perfiles/" + nombreArchivo);
      } catch (IOException e) {
        logger.error("Error al guardar la foto de perfil: {}", e.getMessage(), e);
        throw new BusinessException("No se pudo guardar la foto de perfil");
      }
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
      try {
        // Eliminar foto anterior si existe
        if (arbitroExistente.getUrlFotoPerfil() != null
            && !arbitroExistente.getUrlFotoPerfil().isEmpty()) {
          String nombreArchivoAnterior =
              arbitroExistente.getUrlFotoPerfil().replace("/uploads/perfiles/", "");
          String rutaArchivoAnterior =
              System.getProperty("user.dir")
                  + java.io.File.separator
                  + "uploads"
                  + java.io.File.separator
                  + "perfiles"
                  + java.io.File.separator
                  + nombreArchivoAnterior;
          java.io.File archivoAnterior = new java.io.File(rutaArchivoAnterior);
          if (archivoAnterior.exists()) {
            archivoAnterior.delete();
            logger.info("Foto anterior eliminada: {}", nombreArchivoAnterior);
          }
        }

        String nombreArchivo = System.currentTimeMillis() + "_" + fotoPerfil.getOriginalFilename();
        String rutaBase = fotoPerfilProperties.getFotosPerfilPath();
        java.nio.file.Path rutaDirectorio = java.nio.file.Paths.get(rutaBase);
        if (!java.nio.file.Files.exists(rutaDirectorio)) {
          java.nio.file.Files.createDirectories(rutaDirectorio);
        }
        java.nio.file.Path rutaArchivo = rutaDirectorio.resolve(nombreArchivo);
        fotoPerfil.transferTo(rutaArchivo.toFile());
        // Guardar la URL relativa para servir la imagen
        arbitroExistente.setUrlFotoPerfil("/uploads/perfiles/" + nombreArchivo);
        logger.info(
            "SERVICIO: Nueva foto guardada con URL: {}", arbitroExistente.getUrlFotoPerfil());
      } catch (java.io.IOException e) {
        logger.error("Error al guardar la foto de perfil: {}", e.getMessage(), e);
        throw new BusinessException("No se pudo guardar la foto de perfil");
      }
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
