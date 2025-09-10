/**
 * Archivo: JsonStorageService.java Autores: Sistema de Chat Fecha última modificación: 09.09.2025
 * Descripción: Servicio para manejar el almacenamiento de mensajes en archivos JSON Proyecto: CABA
 * Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.DTOs.ChatMessageDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JsonStorageService {

  private static final Logger logger = LoggerFactory.getLogger(JsonStorageService.class);
  private final ObjectMapper objectMapper;
  private final String chatDirectory = "data/chats";

  public JsonStorageService() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());

    // Crear el directorio de chats si no existe
    File directory = new File(chatDirectory);
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }

  /** Genera el nombre del archivo basado en el admin y árbitro */
  private String generateFileName(Long adminId, Long arbitroId) {
    return String.format("chat_admin%d_arbitro%d.json", adminId, arbitroId);
  }

  /** Obtiene la ruta completa del archivo de chat */
  private String getFilePath(Long adminId, Long arbitroId) {
    return chatDirectory + "/" + generateFileName(adminId, arbitroId);
  }

  /** Guarda un mensaje en el archivo JSON correspondiente */
  public void saveMessage(Long adminId, Long arbitroId, ChatMessageDto message) {
    try {
      String filePath = getFilePath(adminId, arbitroId);
      List<ChatMessageDto> messages = loadMessages(adminId, arbitroId);

      messages.add(message);

      File file = new File(filePath);
      objectMapper.writeValue(file, messages);

      logger.info("Mensaje guardado en archivo: {}", filePath);
    } catch (IOException e) {
      logger.error("Error al guardar mensaje en archivo JSON", e);
    }
  }

  /** Carga todos los mensajes de un chat específico */
  public List<ChatMessageDto> loadMessages(Long adminId, Long arbitroId) {
    try {
      String filePath = getFilePath(adminId, arbitroId);
      File file = new File(filePath);

      if (!file.exists()) {
        logger.info("Archivo de chat no existe, creando lista vacía: {}", filePath);
        return new ArrayList<>();
      }

      List<ChatMessageDto> messages =
          objectMapper.readValue(file, new TypeReference<List<ChatMessageDto>>() {});
      logger.info("Cargados {} mensajes desde archivo: {}", messages.size(), filePath);
      return messages;

    } catch (IOException e) {
      logger.error("Error al cargar mensajes desde archivo JSON", e);
      return new ArrayList<>();
    }
  }

  /** Verifica si existe un chat entre el admin y árbitro */
  public boolean chatExists(Long adminId, Long arbitroId) {
    String filePath = getFilePath(adminId, arbitroId);
    return new File(filePath).exists();
  }

  /** Obtiene la lista de chats existentes para un administrador */
  public List<String> getExistingChatsForAdmin(Long adminId) {
    List<String> chats = new ArrayList<>();
    File directory = new File(chatDirectory);

    if (directory.exists() && directory.isDirectory()) {
      File[] files =
          directory.listFiles(
              (dir, name) ->
                  name.startsWith("chat_admin" + adminId + "_") && name.endsWith(".json"));

      if (files != null) {
        for (File file : files) {
          chats.add(file.getName());
        }
      }
    }

    return chats;
  }
}
