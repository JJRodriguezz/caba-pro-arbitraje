/**
 * Archivo: TorneoDto.java Autores: Diego.Gonzalez Fecha última modificación: 06.09.2025
 * Descripción: DTO para la gestión de torneos en la aplicación Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.DTOs;

import com.caba.caba_pro.enums.TorneoEstado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class TorneoDto {

  @NotBlank(message = "El nombre del torneo es obligatorio")
  @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
  private String nombre;

  @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
  private String descripcion;

  @NotNull(message = "La fecha de inicio es obligatoria")
  private LocalDate fechaInicio;

  @NotNull(message = "La fecha de fin es obligatoria")
  private LocalDate fechaFin;

  @NotNull(message = "El estado del torneo es obligatorio")
  private TorneoEstado estado;

  @Size(max = 200, message = "La ubicación no puede exceder 200 caracteres")
  private String ubicacion;

  // Constructores
  public TorneoDto() {
    this.estado = TorneoEstado.PROXIMO;
  }

  // Getters y Setters
  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public LocalDate getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(LocalDate fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public LocalDate getFechaFin() {
    return fechaFin;
  }

  public void setFechaFin(LocalDate fechaFin) {
    this.fechaFin = fechaFin;
  }

  public TorneoEstado getEstado() {
    return estado;
  }

  public void setEstado(TorneoEstado estado) {
    this.estado = estado;
  }

  public String getUbicacion() {
    return ubicacion;
  }

  public void setUbicacion(String ubicacion) {
    this.ubicacion = ubicacion;
  }
}
