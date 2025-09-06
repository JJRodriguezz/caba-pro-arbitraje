/**
 * Archivo: Partido.java Autores: Juan José Fecha última modificación: 05.09.2025 Descripción:
 * Entidad JPA que representa un partido programado. Proyecto: CABA Pro - Sistema de Gestión
 * Integral de Arbitraje
 */
package com.caba.caba_pro.models;

import com.caba.caba_pro.enums.PartidoEstado;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "partidos")
public class Partido {

  // 1. Constantes estáticas
  private static final int NOMBRE_MAX = 80;

  // 2. Variables de instancia
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = NOMBRE_MAX, nullable = false)
  private String nombre;

  @Column(length = 150)
  private String descripcion;

  @Column(nullable = false)
  private LocalDateTime fechaHora;

  @Column(nullable = false, length = 100)
  private String sede;

  @Column(nullable = false, length = 80)
  private String equipoLocal;

  @Column(nullable = false, length = 80)
  private String equipoVisitante;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PartidoEstado estado;

  @Column(nullable = false)
  private Boolean activo;

  @OneToMany(mappedBy = "partido")
  private List<Asignacion> asignaciones = new ArrayList<>();

  // 3. Constructores
  public Partido() {
    this.activo = true;
    this.estado = PartidoEstado.PROGRAMADO;
  }

  // 4. Métodos públicos
  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public LocalDateTime getFechaHora() {
    return fechaHora;
  }

  public void setFechaHora(LocalDateTime fechaHora) {
    this.fechaHora = fechaHora;
  }

  public String getSede() {
    return sede;
  }

  public void setSede(String sede) {
    this.sede = sede;
  }

  public String getEquipoLocal() {
    return equipoLocal;
  }

  public void setEquipoLocal(String equipoLocal) {
    this.equipoLocal = equipoLocal;
  }

  public String getEquipoVisitante() {
    return equipoVisitante;
  }

  public void setEquipoVisitante(String equipoVisitante) {
    this.equipoVisitante = equipoVisitante;
  }

  public PartidoEstado getEstado() {
    return estado;
  }

  public void setEstado(PartidoEstado estado) {
    this.estado = estado;
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  public List<Asignacion> getAsignaciones() {
    return asignaciones;
  }

  // 5. Métodos privados
}
