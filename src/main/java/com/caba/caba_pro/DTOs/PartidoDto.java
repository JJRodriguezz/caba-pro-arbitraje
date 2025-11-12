/**
 * Archivo: PartidoDto.java Autores: JJRodriguezz Fecha última modificación: 05.09.2025 Descripción:
 * DTO para crear/editar partidos con validaciones. Proyecto: CABA Pro - Sistema de Gestión Integral
 * de Arbitraje
 */
package com.caba.caba_pro.DTOs;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PartidoDto {

  // 1. Constantes estáticas
  private static final int NOMBRE_MAX = 80;

  // 2. Variables de instancia
  @NotBlank(message = "El nombre es obligatorio")
  @Size(max = NOMBRE_MAX, message = "Máximo {max} caracteres")
  private String nombre;

  @Size(max = 150, message = "Máximo {max} caracteres")
  private String descripcion;

  @NotNull(message = "La fecha y hora son obligatorias")
  @Future(message = "La fecha y hora deben estar en el futuro")
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime fechaHora;

  @NotBlank(message = "La sede es obligatoria")
  private String sede;

  private String ubicacion; // Dirección completa del mapa (opcional)

  private Double latitud; // Coordenada latitud (opcional)

  private Double longitud; // Coordenada longitud (opcional)

  @NotBlank(message = "El equipo local es obligatorio")
  private String equipoLocal;

  @NotBlank(message = "El equipo visitante es obligatorio")
  private String equipoVisitante;

  // Para asociar el partido a un torneo
  private Long torneoId;

  // 3. Constructores
  public PartidoDto() {}

  // 4. Métodos públicos (getters/setters)
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

  public LocalDateTime getFechaHora() {
    return fechaHora;
  }

  public void setFechaHora(LocalDateTime fechaHora) {
    this.fechaHora = fechaHora;
  }

  public String getSede() {
    return sede;
  }

  public void setSede(String sede) {
    this.sede = sede;
  }

  public String getUbicacion() {
    return ubicacion;
  }

  public void setUbicacion(String ubicacion) {
    this.ubicacion = ubicacion;
  }

  public Double getLatitud() {
    return latitud;
  }

  public void setLatitud(Double latitud) {
    this.latitud = latitud;
  }

  public Double getLongitud() {
    return longitud;
  }

  public void setLongitud(Double longitud) {
    this.longitud = longitud;
  }

  public String getEquipoLocal() {
    return equipoLocal;
  }

  public void setEquipoLocal(String equipoLocal) {
    this.equipoLocal = equipoLocal;
  }

  public String getEquipoVisitante() {
    return equipoVisitante;
  }

  public void setEquipoVisitante(String equipoVisitante) {
    this.equipoVisitante = equipoVisitante;
  }

  public Long getTorneoId() {
    return torneoId;
  }

  public void setTorneoId(Long torneoId) {
    this.torneoId = torneoId;
  }
}
