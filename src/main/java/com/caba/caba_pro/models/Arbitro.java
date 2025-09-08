/**
 * Archivo: Arbitro.java Autores: Isabella.Idarraga & Diego.Gonzalez Fecha última modificación:
 * [06.09.2025] Descripción: Modelo para la gestión de árbitros en la aplicación Proyecto: CABA Pro
 * - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.models;

import com.caba.caba_pro.enums.Especialidad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "arbitro")
public class Arbitro {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "El nombre es obligatorio")
  @Column(nullable = false)
  private String nombre;

  @NotBlank(message = "Los apellidos son obligatorios")
  @Column(nullable = false)
  private String apellidos;

  @NotBlank(message = "El número de identificación es obligatorio")
  @Column(name = "numero_identificacion", unique = true, nullable = false)
  private String numeroIdentificacion;

  @Email(message = "El email debe tener un formato válido")
  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = true)
  private String telefono;

  @NotNull(message = "La especialidad es obligatoria")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Especialidad especialidad;

  @NotBlank(message = "El escalafón es obligatorio")
  @Column(nullable = false)
  private String escalafon;

  @Column(name = "fecha_nacimiento")
  private LocalDate fechaNacimiento;

  @Column(name = "foto_perfil")
  private String fotoPerfil;

  @Column(name = "fecha_creacion", nullable = false)
  private LocalDateTime fechaCreacion;

  @Column(nullable = false)
  private Boolean activo = true;

  @NotBlank(message = "El nombre de usuario es obligatorio")
  @Column(unique = true, nullable = false)
  private String username;

  @NotBlank(message = "La contraseña es obligatoria")
  @Column(nullable = false)
  private String password;

  @NotBlank(message = "El rol es obligatorio")
  @Column(nullable = false)
  private String role = "ROLE_ARBITRO";

  // Constructor por defecto
  public Arbitro() {}

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
    return nombre + " " + apellidos;
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
}
