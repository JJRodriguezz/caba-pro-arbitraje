/**
 * Archivo: ChatController.java Autores: Diego.Gonzalez Fecha última modificación: [10.09.2025]
 * Descripción: Controlador HTTP para el sistema de chat Proyecto: CABA Pro - Sistema de Gestión
 * Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.DTOs.ChatMessageDto;
import com.caba.caba_pro.models.Administrador;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.repositories.AdministradorRepository;
import com.caba.caba_pro.services.ArbitroService;
import com.caba.caba_pro.services.ChatService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

  private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

  private final ChatService chatService;
  private final ArbitroService arbitroService;
  private final AdministradorRepository administradorRepository;

  public ChatController(
      ChatService chatService,
      ArbitroService arbitroService,
      AdministradorRepository administradorRepository) {
    this.chatService = chatService;
    this.arbitroService = arbitroService;
    this.administradorRepository = administradorRepository;
  }

  //
  @GetMapping("/chat/history/{userId}")
  @ResponseBody
  public ResponseEntity<List<ChatMessageDto>> getChatHistory(
      @PathVariable Long userId, Authentication authentication) {
    try {
      String currentUsername = authentication.getName();
      List<ChatMessageDto> messages = chatService.getChatHistory(currentUsername, userId);

      logger.info(
          "Historial de chat obtenido para usuario {} con {}: {} mensajes",
          currentUsername,
          userId,
          messages.size());

      return ResponseEntity.ok(messages);
    } catch (Exception e) {
      logger.error(
          "Error obteniendo historial de chat para usuario {}: {}", userId, e.getMessage());
      return ResponseEntity.internalServerError().build();
    }
  }

  // Endpoint para obtener conversaciones activas (solo para administradores)

  @GetMapping("/chat/conversations")
  @ResponseBody
  public ResponseEntity<List<Map<String, Object>>> getConversations(Authentication authentication) {
    try {
      // Por ahora devolvemos lista vacía
      return ResponseEntity.ok(List.of());
    } catch (Exception e) {
      logger.error("Error obteniendo conversaciones: {}", e.getMessage());
      return ResponseEntity.internalServerError().build();
    }
  }

  // Endpoint para obtener lista de administradores activos (para árbitros)

  @GetMapping("/chat/admins")
  public ResponseEntity<List<Map<String, Object>>> getAdminsList() {
    try {
      // Obtener todos los administradores activos de la base de datos
      List<Administrador> administradores =
          administradorRepository.findAll().stream()
              .filter(Administrador::isActivo)
              .collect(Collectors.toList());

      // Convertir a formato Map para el frontend
      List<Map<String, Object>> adminsList =
          administradores.stream()
              .map(
                  admin -> {
                    Map<String, Object> adminData = new HashMap<>();
                    adminData.put("id", admin.getId());
                    adminData.put("username", admin.getUsername());
                    adminData.put("nombre", admin.getUsername()); // Usando username como nombre
                    adminData.put("nombreCompleto", admin.getUsername());
                    adminData.put("role", "ADMIN");
                    return adminData;
                  })
              .collect(Collectors.toList());

      logger.info("Devolviendo {} administradores activos", adminsList.size());
      return ResponseEntity.ok(adminsList);
    } catch (Exception e) {
      logger.error("Error obteniendo lista de administradores: {}", e.getMessage());
      return ResponseEntity.internalServerError().build();
    }
  }

  // Endpoint para obtener lista de árbitros activos (para administradores)

  @GetMapping("/chat/arbitros")
  public ResponseEntity<List<Map<String, Object>>> getArbitrosList() {
    try {
      // Obtener todos los árbitros activos
      List<Arbitro> arbitros =
          arbitroService.buscarTodosActivos().stream()
              .filter(Arbitro::isActivo)
              .collect(Collectors.toList());

      List<Map<String, Object>> arbitrosList =
          arbitros.stream()
              .map(
                  arbitro -> {
                    Map<String, Object> arbitroData = new HashMap<>();
                    arbitroData.put("id", arbitro.getId());
                    arbitroData.put("username", arbitro.getUsername());
                    arbitroData.put("nombre", arbitro.getNombre() + " " + arbitro.getApellidos());
                    arbitroData.put(
                        "nombreCompleto", arbitro.getNombre() + " " + arbitro.getApellidos());
                    arbitroData.put("role", "ARBITRO");
                    return arbitroData;
                  })
              .collect(Collectors.toList());

      logger.info("Devolviendo {} árbitros activos", arbitrosList.size());
      return ResponseEntity.ok(arbitrosList);
    } catch (Exception e) {
      logger.error("Error obteniendo lista de árbitros: {}", e.getMessage());
      return ResponseEntity.internalServerError().build();
    }
  }
}
