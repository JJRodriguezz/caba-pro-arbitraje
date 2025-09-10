/**
 * Archivo: DisponibilidadService.java Autores: JJRodriguezz Fecha última modificación: 10.09.2025
 * Descripción: Servicio para la gestión de disponibilidad de árbitros Proyecto: CABA Pro - Sistema
 * de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caba.caba_pro.DTOs.DisponibilidadDto;
import com.caba.caba_pro.enums.TipoDisponibilidad;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.models.Disponibilidad;
import com.caba.caba_pro.repositories.ArbitroRepository;
import com.caba.caba_pro.repositories.DisponibilidadRepository;

@Service
@Transactional
public class DisponibilidadService {

  private static final Logger logger = LoggerFactory.getLogger(DisponibilidadService.class);

  private final DisponibilidadRepository disponibilidadRepository;
  private final ArbitroRepository arbitroRepository;

  public DisponibilidadService(
      DisponibilidadRepository disponibilidadRepository, ArbitroRepository arbitroRepository) {
    this.disponibilidadRepository = disponibilidadRepository;
    this.arbitroRepository = arbitroRepository;
  }

  /** Obtiene la disponibilidad de un árbitro por su username */
  public Optional<DisponibilidadDto> obtenerDisponibilidadPorUsername(String username) {
    logger.info("Obteniendo disponibilidad del árbitro: {}", username);

    Arbitro arbitro = arbitroRepository.findByUsername(username);
    if (arbitro == null) {
      throw new BusinessException("Árbitro no encontrado");
    }

    Optional<Disponibilidad> disponibilidad = disponibilidadRepository.findByArbitro(arbitro);
    return disponibilidad.map(this::mapearADto);
  }

  /** Guarda o actualiza la disponibilidad de un árbitro */
  public DisponibilidadDto guardarDisponibilidad(String username, DisponibilidadDto dto) {
    logger.info("Guardando disponibilidad para el árbitro: {}", username);

    // Validar DTO
    validarDisponibilidadDto(dto);

    Arbitro arbitro = arbitroRepository.findByUsername(username);
    if (arbitro == null) {
      throw new BusinessException("Árbitro no encontrado");
    }

    // Buscar disponibilidad existente o crear nueva
    Disponibilidad disponibilidad =
        disponibilidadRepository
            .findByArbitro(arbitro)
            .orElse(new Disponibilidad(arbitro, dto.getTipoDisponibilidad()));

    // Actualizar datos
    disponibilidad.setTipoDisponibilidad(dto.getTipoDisponibilidad());
    disponibilidad.setObservaciones(dto.getObservaciones());

    // Si es horario específico, validar y guardar horas
    if (dto.getTipoDisponibilidad() == TipoDisponibilidad.HORARIO_ESPECIFICO) {
      if (dto.getHoraInicio() == null || dto.getHoraFin() == null) {
        throw new BusinessException("Para horario específico debe indicar hora de inicio y fin");
      }
      if (!dto.getHoraInicio().isBefore(dto.getHoraFin())) {
        throw new BusinessException("La hora de inicio debe ser anterior a la hora de fin");
      }
      disponibilidad.setHoraInicio(dto.getHoraInicio());
      disponibilidad.setHoraFin(dto.getHoraFin());
    } else {
      // Para otros tipos, limpiar horas
      disponibilidad.setHoraInicio(null);
      disponibilidad.setHoraFin(null);
    }

    Disponibilidad guardada = disponibilidadRepository.save(disponibilidad);
    logger.info("Disponibilidad guardada exitosamente para el árbitro: {}", username);

    return mapearADto(guardada);
  }

  /** Obtiene disponibilidad con valores por defecto si no existe */
  public DisponibilidadDto obtenerOCrearDisponibilidadPorDefecto(String username) {
    Optional<DisponibilidadDto> disponibilidad = obtenerDisponibilidadPorUsername(username);
    return disponibilidad.orElse(new DisponibilidadDto(TipoDisponibilidad.SIEMPRE));
  }

  /** Verifica si un árbitro está disponible */
  public boolean esArbitroDisponible(String username) {
    Optional<DisponibilidadDto> disponibilidad = obtenerDisponibilidadPorUsername(username);
    return disponibilidad
        .map(dto -> dto.getTipoDisponibilidad() != TipoDisponibilidad.NUNCA)
        .orElse(true); // Por defecto disponible si no tiene configuración
  }

  /** Verifica si un árbitro está disponible en una fecha y hora específica */
  public boolean esArbitroDisponibleEnFechaHora(
      String username, java.time.LocalDateTime fechaHora) {
    logger.info(
        "Verificando disponibilidad del árbitro {} para la fecha/hora: {}", username, fechaHora);

    Optional<DisponibilidadDto> disponibilidad = obtenerDisponibilidadPorUsername(username);

    if (disponibilidad.isEmpty()) {
      // Si no tiene configuración, está disponible por defecto
      logger.info(
          "Árbitro {} no tiene configuración de disponibilidad, se asume disponible", username);
      return true;
    }

    DisponibilidadDto dto = disponibilidad.get();

    switch (dto.getTipoDisponibilidad()) {
      case NUNCA:
        logger.info("Árbitro {} configurado como NUNCA disponible", username);
        return false;

      case SIEMPRE:
        logger.info("Árbitro {} configurado como SIEMPRE disponible", username);
        return true;

      case HORARIO_ESPECIFICO:
        if (dto.getHoraInicio() == null || dto.getHoraFin() == null) {
          logger.warn(
              "Árbitro {} configurado para horario específico pero sin horas definidas", username);
          return false;
        }

        java.time.LocalTime horaPartido = fechaHora.toLocalTime();
        boolean disponibleEnHorario =
            !horaPartido.isBefore(dto.getHoraInicio()) && !horaPartido.isAfter(dto.getHoraFin());

        logger.info(
            "Árbitro {} - Horario partido: {}, Horario disponible: {} - {}, Resultado: {}",
            username,
            horaPartido,
            dto.getHoraInicio(),
            dto.getHoraFin(),
            disponibleEnHorario);

        return disponibleEnHorario;

      default:
        logger.warn(
            "Tipo de disponibilidad desconocido para árbitro {}: {}",
            username,
            dto.getTipoDisponibilidad());
        return false;
    }
  }

  // Métodos privados
  private void validarDisponibilidadDto(DisponibilidadDto dto) {
    if (dto.getTipoDisponibilidad() == null) {
      throw new BusinessException("Debe seleccionar un tipo de disponibilidad");
    }
  }

  private DisponibilidadDto mapearADto(Disponibilidad disponibilidad) {
    DisponibilidadDto dto = new DisponibilidadDto();
    dto.setTipoDisponibilidad(disponibilidad.getTipoDisponibilidad());
    dto.setHoraInicio(disponibilidad.getHoraInicio());
    dto.setHoraFin(disponibilidad.getHoraFin());
    dto.setObservaciones(disponibilidad.getObservaciones());
    return dto;
  }
}
