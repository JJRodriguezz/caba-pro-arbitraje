/**
 * Archivo: CambiarPasswordDto.java Autores: Isabella.Idarraga Fecha última modificación:
 * [06.09.2025] Descripción: DTO para cambio de contraseña de usuarios en la aplicación Proyecto:
 * CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CambiarPasswordDto {

  @NotBlank(message = "La contraseña actual es obligatoria")
  private String passwordActual;

  @NotBlank(message = "La nueva contraseña es obligatoria")
  @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
  private String passwordNueva;

  @NotBlank(message = "Debe confirmar la nueva contraseña")
  private String confirmarPassword;

  // Constructores
  public CambiarPasswordDto() {}

  // Getters y Setters
  public String getPasswordActual() {
    return passwordActual;
  }

  public void setPasswordActual(String passwordActual) {
    this.passwordActual = passwordActual;
  }

  public String getPasswordNueva() {
    return passwordNueva;
  }

  public void setPasswordNueva(String passwordNueva) {
    this.passwordNueva = passwordNueva;
  }

  public String getConfirmarPassword() {
    return confirmarPassword;
  }

  public void setConfirmarPassword(String confirmarPassword) {
    this.confirmarPassword = confirmarPassword;
  }
}
