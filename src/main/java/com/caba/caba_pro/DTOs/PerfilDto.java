/**
 * Archivo: PerfilDto.java Autores: Isabella.Idarraga Fecha última modificación: [06.09.2025]
 * Descripción: DTO para la gestión de perfiles de usuarios en la aplicación Proyecto: CABA Pro -
 * Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.DTOs;

import com.caba.caba_pro.enums.Especialidad;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PerfilDto {

  private Long id;
  private String username;
  private String nombre;
  private String apellidos;
  private String nombreCompleto;
  private String numeroIdentificacion;
  private String email;
  private String telefono;
  private Especialidad especialidad;
  private String escalafon;
  private LocalDate fechaNacimiento;
  private String fotoPerfil;
  // URL de la foto de perfil
  private String urlFotoPerfil;
  private String role;
  private LocalDateTime fechaCreacion;
  private Boolean activo;

  // Constructores
  public PerfilDto() {}

  // Getters y Setters

  public String getUrlFotoPerfil() {
    return urlFotoPerfil;
  }

  public void setUrlFotoPerfil(String urlFotoPerfil) {
    this.urlFotoPerfil = urlFotoPerfil;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public String getFotoPerfil() {
    return fotoPerfil;
  }

  public void setFotoPerfil(String fotoPerfil) {
    this.fotoPerfil = fotoPerfil;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  // Métodos de utilidad
  public boolean isArbitro() {
    return "ROLE_ARBITRO".equals(role);
  }

  public boolean isAdmin() {
    return "ROLE_ADMIN".equals(role);
  }
}
