/**
 * Archivo: DisponibilidadDto.java Autores: Diego.Gonzalez Fecha última modificación: [10.09.2025]
 * Descripción: DTO para la gestión de disponibilidad de árbitros Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.DTOs;

import com.caba.caba_pro.enums.TipoDisponibilidad;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public class DisponibilidadDto {

  @NotNull(message = "El tipo de disponibilidad es obligatorio")
  private TipoDisponibilidad tipoDisponibilidad;

  private LocalTime horaInicio;

  private LocalTime horaFin;

  private String observaciones;

  // Constructores
  public DisponibilidadDto() {}

  public DisponibilidadDto(TipoDisponibilidad tipoDisponibilidad) {
    this.tipoDisponibilidad = tipoDisponibilidad;
  }

  // Getters y Setters
  public TipoDisponibilidad getTipoDisponibilidad() {
    return tipoDisponibilidad;
  }

  public void setTipoDisponibilidad(TipoDisponibilidad tipoDisponibilidad) {
    this.tipoDisponibilidad = tipoDisponibilidad;
  }

  public LocalTime getHoraInicio() {
    return horaInicio;
  }

  public void setHoraInicio(LocalTime horaInicio) {
    this.horaInicio = horaInicio;
  }

  public LocalTime getHoraFin() {
    return horaFin;
  }

  public void setHoraFin(LocalTime horaFin) {
    this.horaFin = horaFin;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  // Métodos de utilidad
  public boolean esHorarioEspecifico() {
    return tipoDisponibilidad == TipoDisponibilidad.HORARIO_ESPECIFICO;
  }

  public boolean esValido() {
    if (tipoDisponibilidad == TipoDisponibilidad.HORARIO_ESPECIFICO) {
      return horaInicio != null && horaFin != null && horaInicio.isBefore(horaFin);
    }
    return tipoDisponibilidad != null;
  }
}
