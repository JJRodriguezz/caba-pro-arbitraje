/**
 * Archivo: Asignacion.java Autores: JJRodriguezz Fecha última modificación: 05.09.2025 Descripción:
 * Entidad JPA para la asignación de árbitros a un partido. Proyecto: CABA Pro - Sistema de Gestión
 * Integral de Arbitraje
 */
package com.caba.caba_pro.models;

import com.caba.caba_pro.enums.AsignacionEstado;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asignacion")
public class Asignacion {

  // 1. Constantes estáticas
  private static final int POSICION_MAX = 30;

  // 2. Variables de instancia
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "partido_id", nullable = false)
  private Partido partido;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "arbitro_id", nullable = false)
  private Arbitro arbitro;

  @Column(nullable = false, length = POSICION_MAX)
  private String posicion;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private AsignacionEstado estado;

  @Column(nullable = false)
  private LocalDateTime asignadoEn;

  private LocalDateTime respondidoEn;

  private BigDecimal montoPago;

  @Column(length = 250)
  private String notasAdmin;

  @Column(nullable = false)
  private Boolean activo;

  // 3. Constructores
  public Asignacion() {
    this.activo = true;
    this.estado = AsignacionEstado.PENDIENTE;
    this.asignadoEn = LocalDateTime.now();
  }

  // 4. Métodos públicos
  public Long getId() {
    return id;
  }

  public Partido getPartido() {
    return partido;
  }

  public void setPartido(Partido partido) {
    this.partido = partido;
  }

  public Arbitro getArbitro() {
    return arbitro;
  }

  public void setArbitro(Arbitro arbitro) {
    this.arbitro = arbitro;
  }

  public String getPosicion() {
    return posicion;
  }

  public void setPosicion(String posicion) {
    this.posicion = posicion;
  }

  public AsignacionEstado getEstado() {
    return estado;
  }

  public void setEstado(AsignacionEstado estado) {
    this.estado = estado;
  }

  public LocalDateTime getAsignadoEn() {
    return asignadoEn;
  }

  public LocalDateTime getRespondidoEn() {
    return respondidoEn;
  }

  public void setRespondidoEn(LocalDateTime respondidoEn) {
    this.respondidoEn = respondidoEn;
  }

  public BigDecimal getMontoPago() {
    return montoPago;
  }

  public void setMontoPago(BigDecimal montoPago) {
    this.montoPago = montoPago;
  }

  public String getNotasAdmin() {
    return notasAdmin;
  }

  public void setNotasAdmin(String notasAdmin) {
    this.notasAdmin = notasAdmin;
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  // 5. Métodos privados
}
