/**
 * Archivo: EstadisticasArbitrosDto.java Autores: JJRodriguezz Fecha última modificación: 07.09.2025
 * Descripción: DTO para estadísticas generales de árbitros. Proyecto: CABA Pro - Sistema de Gestión
 * Integral de Arbitraje
 */
package com.caba.caba_pro.DTOs;

import java.util.Map;

public class EstadisticasArbitrosDto {
  // Total de árbitros activos
  private int totalActivos;
  // Cantidad por especialidad
  private Map<String, Integer> cantidadPorEspecialidad;
  // Cantidad por escalafón
  private Map<String, Integer> cantidadPorEscalafon;

  public EstadisticasArbitrosDto() {}

  public int getTotalActivos() {
    return totalActivos;
  }

  public void setTotalActivos(int totalActivos) {
    this.totalActivos = totalActivos;
  }

  public Map<String, Integer> getCantidadPorEspecialidad() {
    return cantidadPorEspecialidad;
  }

  public void setCantidadPorEspecialidad(Map<String, Integer> cantidadPorEspecialidad) {
    this.cantidadPorEspecialidad = cantidadPorEspecialidad;
  }

  public Map<String, Integer> getCantidadPorEscalafon() {
    return cantidadPorEscalafon;
  }

  public void setCantidadPorEscalafon(Map<String, Integer> cantidadPorEscalafon) {
    this.cantidadPorEscalafon = cantidadPorEscalafon;
  }
}
