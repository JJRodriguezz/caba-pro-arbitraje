/**
 * Archivo: ArbitroDto.java Autores: Isabella.Idarraga Fecha última modificación: [04.09.2025]
 * Descripción: DTO para la gestión de árbitros en la aplicación Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.DTOs;

import com.caba.caba_pro.enums.Especialidad;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class ArbitroDto {

  @NotBlank(message = "El nombre es obligatorio")
  @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
  private String nombre;

  @NotBlank(message = "Los apellidos son obligatorios")
  @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
  private String apellidos;

  @NotBlank(message = "El nombre de usuario es obligatorio")
  @Size(min = 3, max = 20, message = "El usuario debe tener entre 3 y 20 caracteres")
  private String username;

  @NotBlank(message = "El número de identificación es obligatorio")
  private String numeroIdentificacion;

  private String telefono;

  @NotBlank(message = "La contraseña es obligatoria")
  @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
  private String password;

  @NotNull(message = "La especialidad es obligatoria")
  private Especialidad especialidad;

  @NotBlank(message = "El escalafón es obligatorio")
  private String escalafon;

  @Email(message = "El email debe tener un formato válido")
  @NotBlank(message = "El email es obligatorio")
  private String email;

  private LocalDate fechaNacimiento;

  // URL de la foto de perfil (opcional)
  private String urlFotoPerfil;

  // Constructores
  public ArbitroDto() {}

  // Getters y Setters

  public String getUrlFotoPerfil() {
    return urlFotoPerfil;
  }

  public void setUrlFotoPerfil(String urlFotoPerfil) {
    this.urlFotoPerfil = urlFotoPerfil;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getApellidos() {
    return apellidos;
  }

  public void setApellidos(String apellidos) {
    this.apellidos = apellidos;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getNumeroIdentificacion() {
    return numeroIdentificacion;
  }

  public void setNumeroIdentificacion(String numeroIdentificacion) {
    this.numeroIdentificacion = numeroIdentificacion;
  }

  public String getTelefono() {
    return telefono;
  }

  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Especialidad getEspecialidad() {
    return especialidad;
  }

  public void setEspecialidad(Especialidad especialidad) {
    this.especialidad = especialidad;
  }

  public String getEscalafon() {
    return escalafon;
  }

  public void setEscalafon(String escalafon) {
    this.escalafon = escalafon;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LocalDate getFechaNacimiento() {
    return fechaNacimiento;
  }

  public void setFechaNacimiento(LocalDate fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
  }
}
