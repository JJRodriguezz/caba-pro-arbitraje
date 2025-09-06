/**
 * Archivo: Administrador.java Autores: Isabella.Idarraga & Diego.Gonzalez Fecha última
 * modificación: [06.09.2025] Descripción: Modelo para la gestión de administradores en la
 * aplicación Proyecto: CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "administrador",
    uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class Administrador {

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
  private String role = "ROLE_ADMIN";

  @Column(name = "fecha_creacion", nullable = false)
  private LocalDateTime fechaCreacion;

  @Column(nullable = false)
  private Boolean activo = true;

  // Constructor por defecto
  public Administrador() {}

  // Constructor con parámetros
  public Administrador(String username, String password) {
    this.username = username;
    this.password = password;
    this.role = "ROLE_ADMIN";
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
