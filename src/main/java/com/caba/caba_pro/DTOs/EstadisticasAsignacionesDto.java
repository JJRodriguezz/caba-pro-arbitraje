/**
 * Archivo: EstadisticasAsignacionesDto.java Autores: JJRodriguezz Fecha última modificación:
 * 07.09.2025 Descripción: DTO para estadísticas de asignaciones aceptadas y rechazadas. Proyecto:
 * CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.DTOs;

public class EstadisticasAsignacionesDto {
  // Total de asignaciones
  private int totalAsignaciones;
  // Asignaciones aceptadas
  private int aceptadas;
  // Asignaciones rechazadas
  private int rechazadas;
  // Porcentaje aceptadas
  private double porcentajeAceptadas;
  // Porcentaje rechazadas
  private double porcentajeRechazadas;

  public EstadisticasAsignacionesDto() {}

  public int getTotalAsignaciones() {
    return totalAsignaciones;
  }

  public void setTotalAsignaciones(int totalAsignaciones) {
    this.totalAsignaciones = totalAsignaciones;
  }

  public int getAceptadas() {
    return aceptadas;
  }

  public void setAceptadas(int aceptadas) {
    this.aceptadas = aceptadas;
  }

  public int getRechazadas() {
    return rechazadas;
  }

  public void setRechazadas(int rechazadas) {
    this.rechazadas = rechazadas;
  }

  public double getPorcentajeAceptadas() {
    return porcentajeAceptadas;
  }

  public void setPorcentajeAceptadas(double porcentajeAceptadas) {
    this.porcentajeAceptadas = porcentajeAceptadas;
  }

  public double getPorcentajeRechazadas() {
    return porcentajeRechazadas;
  }

  public void setPorcentajeRechazadas(double porcentajeRechazadas) {
    this.porcentajeRechazadas = porcentajeRechazadas;
  }
}
