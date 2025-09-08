/**
 * Archivo: AsignacionDto.java Autores: JJRodriguezz Fecha última modificación: 05.09.2025
 * Descripción: DTO para asignar un árbitro a un partido. Proyecto: CABA Pro - Sistema de Gestión
 * Integral de Arbitraje
 */
package com.caba.caba_pro.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AsignacionDto {

  @NotNull(message = "El árbitro es obligatorio")
  private Long arbitroId;

  @NotBlank(message = "La posición es obligatoria")
  private String posicion;

  public AsignacionDto() {}

  public Long getArbitroId() {
    return arbitroId;
  }

  public void setArbitroId(Long arbitroId) {
    this.arbitroId = arbitroId;
  }

  public String getPosicion() {
    return posicion;
  }

  public void setPosicion(String posicion) {
    this.posicion = posicion;
  }
}
