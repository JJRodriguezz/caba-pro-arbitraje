/**
 * Archivo: CalendarioDto.java Autores: Diego.Gonzalez Sistema CABA Pro Fecha última modificación:
 * 07.09.2025 Descripción: DTO para representar eventos en el calendario FullCalendar.js Proyecto:
 * CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.DTOs;

import java.util.Map;

public class CalendarioDto {

  private String id;
  private String title;
  private String start;
  private String end;
  private String backgroundColor;
  private String borderColor;
  private String textColor;
  private Map<String, Object> extendedProps;

  // Constructores
  public CalendarioDto() {}

  public CalendarioDto(
      String id,
      String title,
      String start,
      String end,
      String backgroundColor,
      Map<String, Object> extendedProps) {
    this.id = id;
    this.title = title;
    this.start = start;
    this.end = end;
    this.backgroundColor = backgroundColor;
    this.borderColor = backgroundColor;
    this.textColor = "#ffffff";
    this.extendedProps = extendedProps;
  }

  // Getters y Setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public String getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor;
    this.borderColor = backgroundColor; // Mantener coherencia
  }

  public String getBorderColor() {
    return borderColor;
  }

  public void setBorderColor(String borderColor) {
    this.borderColor = borderColor;
  }

  public String getTextColor() {
    return textColor;
  }

  public void setTextColor(String textColor) {
    this.textColor = textColor;
  }

  public Map<String, Object> getExtendedProps() {
    return extendedProps;
  }

  public void setExtendedProps(Map<String, Object> extendedProps) {
    this.extendedProps = extendedProps;
  }

  @Override
  public String toString() {
    return "CalendarioDto{"
        + "id='"
        + id
        + '\''
        + ", title='"
        + title
        + '\''
        + ", start='"
        + start
        + '\''
        + ", end='"
        + end
        + '\''
        + ", backgroundColor='"
        + backgroundColor
        + '\''
        + '}';
  }
}
