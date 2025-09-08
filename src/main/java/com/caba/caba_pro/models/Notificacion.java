/**
 * Archivo: Notificacion.java Autores: JJRodriguezz Fecha última modificación: 07.09.2025
 * Descripción: Entidad para gestionar notificaciones de asignaciones y respuestas. Proyecto: CABA
 * Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
public class Notificacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String mensaje;

  @Column(nullable = false)
  private Boolean leida = false;

  @Column(nullable = false)
  private LocalDateTime fechaCreacion = LocalDateTime.now();

  @Column(nullable = false)
  private String tipo; // "ARBITRO" o "ADMIN"

  @Column(nullable = false)
  private Long usuarioId; // arbitro o admin

  @Column(nullable = false)
  private Long asignacionId;

  public Notificacion() {}

  // Getters y setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  public Boolean getLeida() {
    return leida;
  }

  public void setLeida(Boolean leida) {
    this.leida = leida;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public Long getUsuarioId() {
    return usuarioId;
  }

  public void setUsuarioId(Long usuarioId) {
    this.usuarioId = usuarioId;
  }

  public Long getAsignacionId() {
    return asignacionId;
  }

  public void setAsignacionId(Long asignacionId) {
    this.asignacionId = asignacionId;
  }
}
