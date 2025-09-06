/**
 * Archivo: Torneo.java Autores: Diego.Gonzalez Fecha última modificación: 06.09.2025 Descripción:
 * Entidad JPA que representa un torneo de baloncesto Proyecto: CABA Pro - Sistema de Gestión
 * Integral de Arbitraje
 */
package com.caba.caba_pro.models;

import com.caba.caba_pro.enums.TorneoEstado;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "torneo")
public class Torneo {

  // 1. Constantes estáticas
  private static final int NOMBRE_MAX = 100;
  private static final int DESCRIPCION_MAX = 500;
  private static final int UBICACION_MAX = 200;

  // 2. Variables de instancia
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = NOMBRE_MAX, nullable = false)
  private String nombre;

  @Column(length = DESCRIPCION_MAX)
  private String descripcion;

  @Column(nullable = false)
  private LocalDate fechaInicio;

  @Column(nullable = false)
  private LocalDate fechaFin;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TorneoEstado estado;

  @Column(length = UBICACION_MAX)
  private String ubicacion;

  @Column(nullable = false)
  private Boolean activo;

  @Column(nullable = false)
  private LocalDateTime fechaCreacion;

  @OneToMany(mappedBy = "torneo")
  private List<Partido> partidos = new ArrayList<>();

  // 3. Constructores
  public Torneo() {
    this.activo = true;
    this.estado = TorneoEstado.PROXIMO;
    this.fechaCreacion = LocalDateTime.now();
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

  public LocalDate getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(LocalDate fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public LocalDate getFechaFin() {
    return fechaFin;
  }

  public void setFechaFin(LocalDate fechaFin) {
    this.fechaFin = fechaFin;
  }

  public TorneoEstado getEstado() {
    return estado;
  }

  public void setEstado(TorneoEstado estado) {
    this.estado = estado;
  }

  public String getUbicacion() {
    return ubicacion;
  }

  public void setUbicacion(String ubicacion) {
    this.ubicacion = ubicacion;
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  public List<Partido> getPartidos() {
    return partidos;
  }

  public void setPartidos(List<Partido> partidos) {
    this.partidos = partidos;
  }

  public Boolean isActive() {
    // Un torneo está activo si no está finalizado y su campo activo es true
    return this.activo && this.estado != TorneoEstado.FINALIZADO;
  }

  public Long getDuration() {
    if (fechaInicio == null || fechaFin == null) {
      return 0L;
    }
    return ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
  }

  public void addMatch(Partido partido) {
    if (partido != null) {
      this.partidos.add(partido);
      partido.setTorneo(this);
    }
  }

  public int getTotalPartidos() {
    return this.partidos.size();
  }

  public int getPartidosActivos() {
    return (int) this.partidos.stream().filter(partido -> partido.getActivo()).count();
  }
}
