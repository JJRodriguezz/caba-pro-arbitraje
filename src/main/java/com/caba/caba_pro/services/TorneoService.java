/**
 * Archivo: TorneoService.java Autores: Diego.Gonzalez Fecha última modificación: 06.09.2025
 * Descripción: Servicio para la gestión de torneos en la aplicación Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.DTOs.TorneoDto;
import com.caba.caba_pro.enums.TorneoEstado;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Torneo;
import com.caba.caba_pro.repositories.TorneoRepository;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TorneoService {

  private static final Logger logger = LoggerFactory.getLogger(TorneoService.class);

  private final TorneoRepository torneoRepository;

  public TorneoService(TorneoRepository torneoRepository) {
    this.torneoRepository = torneoRepository;
  }

  @Transactional(readOnly = true)
  public List<Torneo> buscarTodosActivos() {
    return torneoRepository.findByActivoTrueOrderByFechaInicioDesc();
  }

  @Transactional(readOnly = true)
  public Torneo buscarPorId(Long id) {
    return torneoRepository
        .findById(id)
        .orElseThrow(() -> new BusinessException("Torneo no encontrado con ID: " + id));
  }

  @Transactional(readOnly = true)
  public List<Torneo> buscarPorEstado(TorneoEstado estado) {
    return torneoRepository.findByEstadoAndActivoTrue(estado);
  }

  // Métodos de creación y actualización
  public Torneo crearTorneo(TorneoDto torneoDto) {
    logger.info("Creando torneo: {}", torneoDto.getNombre());

    // Validaciones de negocio
    validarDatosTorneo(torneoDto);

    // Crear torneo
    Torneo torneo = mapearDtoATorneo(torneoDto);

    torneo = torneoRepository.save(torneo);
    logger.info("Torneo creado exitosamente con ID: {}", torneo.getId());

    return torneo;
  }

  public Torneo actualizarTorneo(Long id, TorneoDto torneoDto) {
    logger.info("Actualizando torneo con ID: {}", id);

    Torneo torneoExistente = buscarPorId(id);

    // Validar si el nombre cambió y no existe en otro torneo
    if (!torneoExistente.getNombre().equals(torneoDto.getNombre())) {
      if (torneoRepository.existsByNombreAndActivoTrue(torneoDto.getNombre())) {
        throw new BusinessException("Ya existe un torneo con ese nombre");
      }
    }

    // Validar fechas
    validarFechas(torneoDto.getFechaInicio(), torneoDto.getFechaFin());

    // Actualizar datos del torneo
    actualizarDatosTorneo(torneoExistente, torneoDto);

    Torneo torneoActualizado = torneoRepository.save(torneoExistente);
    logger.info("Torneo actualizado exitosamente con ID: {}", torneoActualizado.getId());

    return torneoActualizado;
  }

  public void eliminarTorneo(Long id) {
    Torneo torneo = buscarPorId(id);

    // Verificar si tiene partidos asociados
    if (torneo.getTotalPartidos() > 0) {
      throw new BusinessException("No se puede eliminar un torneo que tiene partidos asociados");
    }

    torneo.setActivo(false); // Soft delete
    torneoRepository.save(torneo);
    logger.info("Torneo desactivado: {}", id);
  }

  public void cambiarEstadoTorneo(Long id, TorneoEstado nuevoEstado) {
    logger.info("Cambiando estado del torneo {} a {}", id, nuevoEstado);

    Torneo torneo = buscarPorId(id);
    validarCambioEstado(torneo.getEstado(), nuevoEstado);

    torneo.setEstado(nuevoEstado);
    torneoRepository.save(torneo);

    logger.info("Estado del torneo {} actualizado a {}", id, nuevoEstado);
  }

  // Métodos de validación privados
  private void validarDatosTorneo(TorneoDto dto) {
    // Validar que no exista torneo con mismo nombre
    if (torneoRepository.existsByNombreAndActivoTrue(dto.getNombre())) {
      throw new BusinessException("Ya existe un torneo con ese nombre");
    }

    // Validar fechas para nuevo torneo
    validarFechasParaNuevoTorneo(dto.getFechaInicio(), dto.getFechaFin());
  }

  private void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
    if (fechaInicio == null || fechaFin == null) {
      throw new BusinessException("Las fechas de inicio y fin son obligatorias");
    }

    if (fechaInicio.isAfter(fechaFin)) {
      throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
    }
  }

  private void validarFechasParaNuevoTorneo(LocalDate fechaInicio, LocalDate fechaFin) {
    validarFechas(fechaInicio, fechaFin);

    // Para nuevos torneos, la fecha de inicio no puede ser anterior a hoy
    if (fechaInicio.isBefore(LocalDate.now())) {
      throw new BusinessException("La fecha de inicio no puede ser anterior a la fecha actual");
    }
  }

  private void validarCambioEstado(TorneoEstado estadoActual, TorneoEstado nuevoEstado) {
    // Lógica de validación para cambios de estado válidos con los nuevos estados
    if (estadoActual == TorneoEstado.FINALIZADO) {
      throw new BusinessException("No se puede cambiar el estado de un torneo finalizado");
    }

    // Validaciones de transiciones lógicas
    if (estadoActual == TorneoEstado.PROXIMO && nuevoEstado == TorneoEstado.FINALIZADO) {
      throw new BusinessException(
          "Un torneo próximo no puede pasar directamente a finalizado. Debe estar activo primero.");
    }
  }

  private Torneo mapearDtoATorneo(TorneoDto dto) {
    Torneo torneo = new Torneo();
    torneo.setNombre(dto.getNombre());
    torneo.setDescripcion(dto.getDescripcion());
    torneo.setFechaInicio(dto.getFechaInicio());
    torneo.setFechaFin(dto.getFechaFin());
    torneo.setEstado(dto.getEstado());
    torneo.setUbicacion(dto.getUbicacion());
    torneo.setActivo(true);

    return torneo;
  }

  private void actualizarDatosTorneo(Torneo torneo, TorneoDto dto) {
    torneo.setNombre(dto.getNombre());
    torneo.setDescripcion(dto.getDescripcion());
    torneo.setFechaInicio(dto.getFechaInicio());
    torneo.setFechaFin(dto.getFechaFin());
    torneo.setEstado(dto.getEstado());
    torneo.setUbicacion(dto.getUbicacion());
  }
}
