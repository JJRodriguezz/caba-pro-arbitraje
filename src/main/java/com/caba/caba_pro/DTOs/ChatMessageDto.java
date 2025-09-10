/**
 * Archivo: ChatMessageDto.java Autores: Sistema de Chat Fecha última modificación: 09.09.2025
 * Descripción: DTO para representar mensajes de chat Proyecto: CABA Pro - Sistema de Gestión
 * Integral de Arbitraje
 */
package com.caba.caba_pro.DTOs;

import java.time.LocalDateTime;

public class ChatMessageDto {
  private String remitente;
  private String destinatario;
  private String content; // Cambié de 'contenido' a 'content' para coincidir con JS
  private LocalDateTime timestamp;
  private Long senderId;
  private String senderUsername;

  // Constructores
  public ChatMessageDto() {}

  public ChatMessageDto(
      String remitente, String destinatario, String content, LocalDateTime timestamp) {
    this.remitente = remitente;
    this.destinatario = destinatario;
    this.content = content;
    this.timestamp = timestamp;
  }

  // Getters y Setters
  public String getRemitente() {
    return remitente;
  }

  public void setRemitente(String remitente) {
    this.remitente = remitente;
  }

  public String getDestinatario() {
    return destinatario;
  }

  public void setDestinatario(String destinatario) {
    this.destinatario = destinatario;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public Long getSenderId() {
    return senderId;
  }

  public void setSenderId(Long senderId) {
    this.senderId = senderId;
  }

  public String getSenderUsername() {
    return senderUsername;
  }

  public void setSenderUsername(String senderUsername) {
    this.senderUsername = senderUsername;
  }

  // Método para compatibilidad con código anterior
  public String getContenido() {
    return content;
  }

  public void setContenido(String contenido) {
    this.content = contenido;
  }
}
