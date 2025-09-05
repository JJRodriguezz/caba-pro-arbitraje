/**
 * Archivo: Usuario.java Autores: Isabella.Idarraga Fecha última modificación: [04.09.2025]
 * Descripción: Clase que representa a un usuario en el sistema Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "usuarios",
    uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "El nombre de usuario es obligatorio")
  @Size(min = 3, max = 20, message = "El usuario debe tener entre 3 y 20 caracteres")
  @Column(unique = true, nullable = false)
  private String username;

  @NotBlank(message = "La contraseña es obligatoria")
  @Column(nullable = false)
  private String password;

  @NotBlank(message = "El rol es obligatorio")
  @Column(nullable = false)
  private String role;

  @Column(name = "fecha_creacion", nullable = false)
  private LocalDateTime fechaCreacion;

  @Column(nullable = false)
  private Boolean activo = true;

  // Constructor por defecto
  public Usuario() {}

  // Constructor con parámetros
  public Usuario(String username, String password, String role) {
    this.username = username;
    this.password = password;
    this.role = role;
  }

  @PrePersist
  protected void onCreate() {
    fechaCreacion = LocalDateTime.now();
  }

  // Getters y Setters
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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

  public boolean isActivo() {
    return activo != null && activo;
  }
}
