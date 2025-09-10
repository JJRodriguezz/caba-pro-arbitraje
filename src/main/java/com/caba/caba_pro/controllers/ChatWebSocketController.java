/**
 * Archivo: ChatWebSocketController.java Autores: Diego.Gonzalez Fecha última modificación:
 * [10.09.2025] Descripción: Controlador WebSocket para manejar mensajes de chat en tiempo real
 * Proyecto: CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.DTOs.ChatMessageDto;
import com.caba.caba_pro.services.ChatService;
import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

  private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);

  private final ChatService chatService;

  public ChatWebSocketController(ChatService chatService) {
    this.chatService = chatService;
  }

  // Maneja mensajes enviados por administradores a árbitros

  @MessageMapping("/chat/arbitro/{arbitroId}")
  public void handleAdminMessage(
      @DestinationVariable String arbitroId,
      @Payload ChatMessageDto message,
      SimpMessageHeaderAccessor headerAccessor,
      Principal principal) {

    logger.info(
        "Mensaje recibido de admin {} para árbitro {}: {}",
        principal.getName(),
        arbitroId,
        message.getContent());

    try {
      // Procesar el mensaje con el ID del árbitro
      chatService.processMessage(message, principal.getName(), Long.parseLong(arbitroId));

    } catch (NumberFormatException e) {
      logger.error("Error al convertir arbitroId a Long: {}", arbitroId, e);
    } catch (Exception e) {
      logger.error("Error al procesar mensaje de admin", e);
    }
  }

  // Maneja mensajes enviados por árbitros a administradores

  @MessageMapping("/chat/admin/{adminId}")
  public void handleArbitroMessage(
      @DestinationVariable String adminId,
      @Payload ChatMessageDto message,
      SimpMessageHeaderAccessor headerAccessor,
      Principal principal) {

    logger.info(
        "Mensaje recibido de árbitro {} para admin {}: {}",
        principal.getName(),
        adminId,
        message.getContent());

    try {

      // Procesar el mensaje con el ID del admin

      chatService.processMessage(message, principal.getName(), Long.parseLong(adminId));

    } catch (NumberFormatException e) {
      logger.error("Error al convertir adminId a Long: {}", adminId, e);
    } catch (Exception e) {
      logger.error("Error al procesar mensaje de árbitro", e);
    }
  }
}
