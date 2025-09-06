/**
 * Archivo: Especialidad.java Autores: Isabella.Idarraga Fecha última modificación: [04.09.2025]
 * Descripción: Enum para las especialidades de los árbitros en la aplicación Proyecto: CABA Pro -
 * Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.enums;

public enum Especialidad {
  CAMPO("Árbitro de Campo"),
  MESA("Árbitro de Mesa");

  private final String descripcion;

  Especialidad(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }
}
