/**
 * Archivo: LiquidacionDTO.java Autores: JJRodriguezz Fecha última modificación: 11.11.2025
 * Descripción: DTO para manejar datos de liquidación de árbitros Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** DTO que representa la liquidación completa de pagos a árbitros */
public class LiquidacionDTO {

  private LocalDateTime fechaGeneracion;
  private LocalDateTime fechaInicio;
  private LocalDateTime fechaFin;
  private List<LiquidacionArbitroDTO> arbitros;
  private BigDecimal totalGeneral;
  private Integer totalPartidos;
  private Integer totalAsignaciones;

  public LiquidacionDTO() {
    this.arbitros = new ArrayList<>();
    this.fechaGeneracion = LocalDateTime.now();
    this.totalGeneral = BigDecimal.ZERO;
    this.totalPartidos = 0;
    this.totalAsignaciones = 0;
  }

  // Getters y Setters
  public LocalDateTime getFechaGeneracion() {
    return fechaGeneracion;
  }

  public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
    this.fechaGeneracion = fechaGeneracion;
  }

  public LocalDateTime getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(LocalDateTime fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public LocalDateTime getFechaFin() {
    return fechaFin;
  }

  public void setFechaFin(LocalDateTime fechaFin) {
    this.fechaFin = fechaFin;
  }

  public List<LiquidacionArbitroDTO> getArbitros() {
    return arbitros;
  }

  public void setArbitros(List<LiquidacionArbitroDTO> arbitros) {
    this.arbitros = arbitros;
  }

  public BigDecimal getTotalGeneral() {
    return totalGeneral;
  }

  public void setTotalGeneral(BigDecimal totalGeneral) {
    this.totalGeneral = totalGeneral;
  }

  public Integer getTotalPartidos() {
    return totalPartidos;
  }

  public void setTotalPartidos(Integer totalPartidos) {
    this.totalPartidos = totalPartidos;
  }

  public Integer getTotalAsignaciones() {
    return totalAsignaciones;
  }

  public void setTotalAsignaciones(Integer totalAsignaciones) {
    this.totalAsignaciones = totalAsignaciones;
  }

  /** DTO que representa la liquidación de un árbitro específico */
  public static class LiquidacionArbitroDTO {
    private Long arbitroId;
    private String nombreCompleto;
    private String numeroIdentificacion;
    private String escalafon;
    private List<DetallePartidoDTO> partidos;
    private BigDecimal totalAPagar;
    private Integer cantidadPartidos;

    public LiquidacionArbitroDTO() {
      this.partidos = new ArrayList<>();
      this.totalAPagar = BigDecimal.ZERO;
      this.cantidadPartidos = 0;
    }

    // Getters y Setters
    public Long getArbitroId() {
      return arbitroId;
    }

    public void setArbitroId(Long arbitroId) {
      this.arbitroId = arbitroId;
    }

    public String getNombreCompleto() {
      return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
      this.nombreCompleto = nombreCompleto;
    }

    public String getNumeroIdentificacion() {
      return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
      this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getEscalafon() {
      return escalafon;
    }

    public void setEscalafon(String escalafon) {
      this.escalafon = escalafon;
    }

    public List<DetallePartidoDTO> getPartidos() {
      return partidos;
    }

    public void setPartidos(List<DetallePartidoDTO> partidos) {
      this.partidos = partidos;
    }

    public BigDecimal getTotalAPagar() {
      return totalAPagar;
    }

    public void setTotalAPagar(BigDecimal totalAPagar) {
      this.totalAPagar = totalAPagar;
    }

    public Integer getCantidadPartidos() {
      return cantidadPartidos;
    }

    public void setCantidadPartidos(Integer cantidadPartidos) {
      this.cantidadPartidos = cantidadPartidos;
    }
  }

  /** DTO que representa el detalle de un partido en la liquidación */
  public static class DetallePartidoDTO {
    private Long partidoId;
    private String nombrePartido;
    private String torneo;
    private LocalDateTime fechaPartido;
    private String posicion;
    private BigDecimal montoPago;
    private String estado;

    // Getters y Setters
    public Long getPartidoId() {
      return partidoId;
    }

    public void setPartidoId(Long partidoId) {
      this.partidoId = partidoId;
    }

    public String getNombrePartido() {
      return nombrePartido;
    }

    public void setNombrePartido(String nombrePartido) {
      this.nombrePartido = nombrePartido;
    }

    public String getTorneo() {
      return torneo;
    }

    public void setTorneo(String torneo) {
      this.torneo = torneo;
    }

    public LocalDateTime getFechaPartido() {
      return fechaPartido;
    }

    public void setFechaPartido(LocalDateTime fechaPartido) {
      this.fechaPartido = fechaPartido;
    }

    public String getPosicion() {
      return posicion;
    }

    public void setPosicion(String posicion) {
      this.posicion = posicion;
    }

    public BigDecimal getMontoPago() {
      return montoPago;
    }

    public void setMontoPago(BigDecimal montoPago) {
      this.montoPago = montoPago;
    }

    public String getEstado() {
      return estado;
    }

    public void setEstado(String estado) {
      this.estado = estado;
    }
  }
}
