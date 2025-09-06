/**
 * Archivo: EditarPerfilDto.java Autores: Isabella.Idarraga Fecha última modificación: [06.09.2025]
 * Descripción: DTO para la edición de perfiles de usuarios en la aplicación Proyecto: CABA Pro -
 * Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.DTOs;

import com.caba.caba_pro.enums.Especialidad;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class EditarPerfilDto {

  // Campos para administrador
  @Size(min = 3, max = 20, message = "El usuario debe tener entre 3 y 20 caracteres")
  private String username;

  // Campos para árbitro
  @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
  private String nombre;

  @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
  private String apellidos;

  private String numeroIdentificacion;

  @Email(message = "El email debe tener un formato válido")
  private String email;

  private String telefono;
  private Especialidad especialidad;
  private String escalafon;
  private LocalDate fechaNacimiento;

  // Campos comunes
  private Boolean activo;

  // Constructores
  public EditarPerfilDto() {}

  // Getters y Setters
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
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

  public String getNumeroIdentificacion() {
    return numeroIdentificacion;
  }

  public void setNumeroIdentificacion(String numeroIdentificacion) {
    this.numeroIdentificacion = numeroIdentificacion;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTelefono() {
    return telefono;
  }

  public void setTelefono(String telefono) {
    this.telefono = telefono;
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

  public LocalDate getFechaNacimiento() {
    return fechaNacimiento;
  }

  public void setFechaNacimiento(LocalDate fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }
}
