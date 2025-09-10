/**
 * Archivo: TipoDisponibilidad.java Autores: JJRodriguezz Fecha última modificación: 10.09.2025
 * Descripción: Enum para tipos de disponibilidad de árbitros Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.enums;

public enum TipoDisponibilidad {
  SIEMPRE("Siempre disponible"),
  NUNCA("No disponible"),
  HORARIO_ESPECIFICO("Horario específico");

  private final String descripcion;

  TipoDisponibilidad(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }
}
