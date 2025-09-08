/**
 * Archivo: TopArbitrosDto.java Autores: JJRodriguezz Fecha última modificación: 07.09.2025
 * Descripción: DTO para el top de árbitros más activos en un período. Proyecto: CABA Pro - Sistema
 * de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.DTOs;

import java.util.List;

public class TopArbitrosDto {
  // Lista de árbitros más activos
  private List<ArbitroActividadDto> topArbitros;

  public TopArbitrosDto() {}

  public List<ArbitroActividadDto> getTopArbitros() {
    return topArbitros;
  }

  public void setTopArbitros(List<ArbitroActividadDto> topArbitros) {
    this.topArbitros = topArbitros;
  }

  // DTO interno para datos de actividad de árbitro
  public static class ArbitroActividadDto {
    private Long idArbitro;
    private String nombreCompleto;
    private int cantidadAsignaciones;

    public ArbitroActividadDto() {}

    public Long getIdArbitro() {
      return idArbitro;
    }

    public void setIdArbitro(Long idArbitro) {
      this.idArbitro = idArbitro;
    }

    public String getNombreCompleto() {
      return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
      this.nombreCompleto = nombreCompleto;
    }

    public int getCantidadAsignaciones() {
      return cantidadAsignaciones;
    }

    public void setCantidadAsignaciones(int cantidadAsignaciones) {
      this.cantidadAsignaciones = cantidadAsignaciones;
    }
  }
}
