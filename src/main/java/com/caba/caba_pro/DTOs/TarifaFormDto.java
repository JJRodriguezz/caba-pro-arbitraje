/**
 * Archivo: TarifaFormDto.java Autores: JJRodriguezz Fecha última modificación: 06.09.2025 Descripción:
 * Formulario con listado de tarifas a actualizar. Proyecto: CABA Pro - Sistema de Gestión Integral
 * de Arbitraje
 */
package com.caba.caba_pro.DTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class TarifaFormDto {

  @NotEmpty(message = "Debe ingresar al menos una tarifa")
  @Valid
  private List<TarifaItemDto> tarifas = new ArrayList<>();

  public TarifaFormDto() {}

  public List<TarifaItemDto> getTarifas() {
    return tarifas;
  }

  public void setTarifas(List<TarifaItemDto> tarifas) {
    this.tarifas = tarifas;
  }
}
