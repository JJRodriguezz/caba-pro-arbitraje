/**
 * Archivo: TarifaItemDto.java Autores: JJRodriguezz Fecha última modificación: 06.09.2025 Descripción:
 * Ítem de tarifa (escalafón + monto) para el formulario. Proyecto: CABA Pro - Sistema de Gestión
 * Integral de Arbitraje
 */
package com.caba.caba_pro.DTOs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TarifaItemDto {

  @NotBlank(message = "El escalafón es obligatorio")
  private String escalafon;

  @NotNull(message = "El monto es obligatorio")
  @DecimalMin(value = "0.0", inclusive = true, message = "El monto debe ser mayor o igual a 0")
  private BigDecimal monto;

  public TarifaItemDto() {}

  public TarifaItemDto(String escalafon, BigDecimal monto) {
    this.escalafon = escalafon;
    this.monto = monto;
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
}
