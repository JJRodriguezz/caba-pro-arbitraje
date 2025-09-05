/**
 * Archivo: RegistroForm.java Autores: Isabella.Idarraga Fecha última modificación: [04.09.2025]
 * Descripción: Clase que representa el formulario de registro de usuarios Proyecto: CABA Pro -
 * Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistroForm {

  @NotBlank(message = "El nombre de usuario no puede estar vacío")
  @Size(min = 3, max = 20, message = "El usuario debe tener entre 3 y 20 caracteres")
  private String username;

  @NotBlank(message = "La contraseña no puede estar vacía")
  @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
  private String password;

  @NotBlank(message = "Debe confirmar la contraseña")
  private String confirmPassword;

  // Constructores
  public RegistroForm() {}

  public RegistroForm(String username, String password, String confirmPassword) {
    this.username = username;
    this.password = password;
    this.confirmPassword = confirmPassword;
  }

  // Getters y Setters
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }
}
