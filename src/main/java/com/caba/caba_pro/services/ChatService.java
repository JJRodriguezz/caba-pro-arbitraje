/**
 * Archivo: ChatService.java Autores: Sistema de Chat Fecha última modificación: 09.09.2025
 * Descripción: Servicio principal para manejar la lógica del chat Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.DTOs.ChatMessageDto;
import com.caba.caba_pro.models.Administrador;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.repositories.AdministradorRepository;
import com.caba.caba_pro.repositories.ArbitroRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

  private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

  private final JsonStorageService jsonStorageService;
  private final SimpMessagingTemplate messagingTemplate;
  private final AdministradorRepository administradorRepository;
  private final ArbitroRepository arbitroRepository;

  public ChatService(
      JsonStorageService jsonStorageService,
      SimpMessagingTemplate messagingTemplate,
      AdministradorRepository administradorRepository,
      ArbitroRepository arbitroRepository) {
    this.jsonStorageService = jsonStorageService;
    this.messagingTemplate = messagingTemplate;
    this.administradorRepository = administradorRepository;
    this.arbitroRepository = arbitroRepository;
  }

  /** Procesa y envía un mensaje de chat */
  public void processMessage(ChatMessageDto message, String senderUsername, Long targetUserId) {
    try {
      // Establecer timestamp si no está presente
      if (message.getTimestamp() == null) {
        message.setTimestamp(LocalDateTime.now());
      }

      // Normalizar campo de contenido por compatibilidad
      if (message.getContent() == null && message.getContenido() != null) {
        message.setContent(message.getContenido());
      }

      // Determinar IDs de admin y árbitro para el almacenamiento
      Long adminId = null;
      Long arbitroId = null;

      // Obtener información del usuario actual
      Administrador admin = administradorRepository.findByUsername(senderUsername);
      Arbitro arbitro = arbitroRepository.findByUsername(senderUsername);

      if (admin != null) {
        // El remitente es admin
        adminId = admin.getId();
        arbitroId = targetUserId; // El target debe ser un árbitro
        message.setRemitente("ADMIN");
        message.setDestinatario("ARBITRO");
        message.setSenderId(admin.getId());
        message.setSenderUsername(admin.getUsername());

      } else if (arbitro != null) {
        // El remitente es árbitro
        arbitroId = arbitro.getId();
        adminId = targetUserId; // El target debe ser un admin específico
        message.setRemitente("ARBITRO");
        message.setDestinatario("ADMIN");
        message.setSenderId(arbitro.getId());
        message.setSenderUsername(arbitro.getUsername());
      }

      if (adminId != null && arbitroId != null) {
        // Guardar mensaje en JSON
        jsonStorageService.saveMessage(adminId, arbitroId, message);

        // Enviar mensaje al destinatario específico
        sendMessageToUser(message, adminId, arbitroId);

        logger.info(
            "Mensaje procesado exitosamente de {} a {}",
            message.getRemitente(),
            message.getDestinatario());
      } else {
        logger.error("No se pudo determinar adminId o arbitroId para el mensaje");
      }

    } catch (Exception e) {
      logger.error("Error al procesar mensaje de chat", e);
    }
  }

  /** Envía mensaje al usuario específico usando WebSocket */
  private void sendMessageToUser(ChatMessageDto message, Long adminId, Long arbitroId) {
    try {
      if ("ADMIN".equals(message.getRemitente())) {
        // Enviar al árbitro
        Arbitro arbitro = arbitroRepository.findById(arbitroId).orElse(null);
        if (arbitro != null) {
          messagingTemplate.convertAndSendToUser(arbitro.getUsername(), "/queue/private", message);
        }
      } else {
        // Enviar al admin
        Administrador admin = administradorRepository.findById(adminId).orElse(null);
        if (admin != null) {
          messagingTemplate.convertAndSendToUser(admin.getUsername(), "/queue/private", message);
        }
      }
    } catch (Exception e) {
      logger.error("Error al enviar mensaje por WebSocket", e);
    }
  }

  /** Obtiene el historial de mensajes entre un admin y árbitro */
  public List<ChatMessageDto> getChatHistory(String currentUsername, Long userId) {
    try {
      // Determinar quién es el usuario actual y quién es el otro
      Administrador currentAdmin = administradorRepository.findByUsername(currentUsername);
      Arbitro currentArbitro = arbitroRepository.findByUsername(currentUsername);

      Long adminId = null;
      Long arbitroId = null;

      if (currentAdmin != null) {
        // Usuario actual es admin, userId es del árbitro
        adminId = currentAdmin.getId();
        arbitroId = userId;
      } else if (currentArbitro != null) {
        // Usuario actual es árbitro, userId es del admin específico
        arbitroId = currentArbitro.getId();
        adminId = userId; // Usar el ID del admin específico
      }

      if (adminId != null && arbitroId != null) {
        return jsonStorageService.loadMessages(adminId, arbitroId);
      }

      logger.warn("No se pudo determinar adminId o arbitroId para obtener historial");
      return List.of();

    } catch (Exception e) {
      logger.error("Error al obtener historial de chat", e);
      return List.of();
    }
  }

  /** Obtiene información del usuario actual autenticado */
  public String getCurrentUsername() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth != null ? auth.getName() : null;
  }

  /** Verifica si el usuario actual es administrador */
  public boolean isCurrentUserAdmin() {
    String username = getCurrentUsername();
    if (username != null) {
      return administradorRepository.findByUsername(username) != null;
    }
    return false;
  }
}
