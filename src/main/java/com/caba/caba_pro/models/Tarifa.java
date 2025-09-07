/**
 * Archivo: Tarifa.java Autores: JJRodriguezz Fecha última modificación: 06.09.2025 Descripción: Tarifa
 * por partido según escalafón de árbitro. Proyecto: CABA Pro - Sistema de Gestión Integral de
 * Arbitraje
 */
package com.caba.caba_pro.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "tarifa")
public class Tarifa {

  // 1. Constantes estáticas

  // 2. Variables de instancia
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Usamos String para no romper el modelo actual de árbitros
  @NotBlank(message = "El escalafón es obligatorio")
  @Column(nullable = false, length = 20, unique = true)
  private String escalafon;

  @NotNull(message = "El monto es obligatorio")
  @DecimalMin(value = "0.0", inclusive = true, message = "El monto debe ser mayor o igual a 0")
  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal monto;

  @Column(nullable = false)
  private Boolean activo;

  // 3. Constructores
  public Tarifa() {
    this.activo = true;
  }

  // 4. Métodos públicos
  public Long getId() {
    return id;
  }

  public String getEscalafon() {
    return escalafon;
  }

  public void setEscalafon(String escalafon) {
    this.escalafon = escalafon;
  }

  public BigDecimal getMonto() {
    return monto;
  }

  public void setMonto(BigDecimal monto) {
    this.monto = monto;
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  // 5. Métodos privados
}
