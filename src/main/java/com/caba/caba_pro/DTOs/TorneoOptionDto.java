/**
 * Archivo: TorneoOptionDto.java Autores: Diego.Gonzalez Fecha última modificación: [07.09.2025]
 * Descripción: DTO liviano para exponer torneos en selects/filtros Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.DTOs;

public class TorneoOptionDto {

  private Long id;
  private String nombre;
  private String estado; // opcional, texto del enum

  public TorneoOptionDto() {}

  public TorneoOptionDto(Long id, String nombre, String estado) {
    this.id = id;
    this.nombre = nombre;
    this.estado = estado;
  }

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

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }
}
