/**
 * Archivo: TorneoEstado.java Autores: Diego.Gonzalez Fecha última modificación: 06.09.2025
 * Descripción: Enumeración para los estados de un torneo Proyecto: CABA Pro - Sistema de Gestión
 * Integral de Arbitraje
 */
package com.caba.caba_pro.enums;

public enum TorneoEstado {
  ACTIVO("Activo"),
  FINALIZADO("Finalizado"),
  PROXIMO("Próximo");

  private final String descripcion;

  TorneoEstado(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }
}
