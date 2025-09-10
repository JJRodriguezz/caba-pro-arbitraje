/**
 * Archivo: Disponibilidad.java Autores: Diego.Gonzalez Fecha última modificación: [10.09.2025]
 * Descripción: Modelo para la gestión de disponibilidad de árbitros Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.models;

import com.caba.caba_pro.enums.TipoDisponibilidad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

@Entity
@Table(name = "disponibilidad")
public class Disponibilidad {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull(message = "El árbitro es obligatorio")
  @OneToOne
  @JoinColumn(name = "arbitro_id", nullable = false)
  private Arbitro arbitro;

  @NotNull(message = "El tipo de disponibilidad es obligatorio")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoDisponibilidad tipoDisponibilidad;

  @Column(name = "hora_inicio")
  private LocalTime horaInicio;

  @Column(name = "hora_fin")
  private LocalTime horaFin;

  @Column(columnDefinition = "TEXT")
  private String observaciones;

  // Constructores
  public Disponibilidad() {}

  public Disponibilidad(Arbitro arbitro, TipoDisponibilidad tipoDisponibilidad) {
    this.arbitro = arbitro;
    this.tipoDisponibilidad = tipoDisponibilidad;
  }

  // Getters y Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Arbitro getArbitro() {
    return arbitro;
  }

  public void setArbitro(Arbitro arbitro) {
    this.arbitro = arbitro;
  }

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

  // Métodos
  public boolean esDisponible() {
    return tipoDisponibilidad == TipoDisponibilidad.SIEMPRE
        || tipoDisponibilidad == TipoDisponibilidad.HORARIO_ESPECIFICO;
  }

  public boolean tieneHorarioEspecifico() {
    return tipoDisponibilidad == TipoDisponibilidad.HORARIO_ESPECIFICO;
  }
}
