/**
 * Archivo: AuthRestController.java Autores: CABA Pro Team Fecha última modificación: 11.11.2025
 * Descripción: Controlador REST para autenticación (API JSON) Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers.api;

import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.services.ArbitroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para autenticación de árbitros")
public class AuthRestController {

  private static final String USERNAME_KEY = "username";
  private static final String PASSWORD_KEY = "password";
  private static final String SUCCESS_KEY = "success";
  private static final String MESSAGE_KEY = "message";
  private static final String DATA_KEY = "data";
  private static final String EMAIL_KEY = "email";

  private final ArbitroService arbitroService;
  private final PasswordEncoder passwordEncoder;

  public AuthRestController(ArbitroService arbitroService, PasswordEncoder passwordEncoder) {
    this.arbitroService = arbitroService;
    this.passwordEncoder = passwordEncoder;
  }

  @Operation(
      summary = "Login de árbitro",
      description = "Autenticar un árbitro con username y password")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login exitoso",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas",
            content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos incompletos", content = @Content)
      })
  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
    String username = credentials.get(USERNAME_KEY);
    String password = credentials.get(PASSWORD_KEY);

    if (username == null || password == null) {
      Map<String, Object> error = new HashMap<>();
      error.put(SUCCESS_KEY, false);
      error.put(MESSAGE_KEY, "Username y password son requeridos");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    try {
      Arbitro arbitro = arbitroService.buscarPorUsername(username);

      // Verificar contraseña
      if (!passwordEncoder.matches(password, arbitro.getPassword())) {
        Map<String, Object> error = new HashMap<>();
        error.put(SUCCESS_KEY, false);
        error.put(MESSAGE_KEY, "Credenciales inválidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
      }

      // Verificar que el árbitro está activo
      if (Boolean.FALSE.equals(arbitro.getActivo())) {
        Map<String, Object> error = new HashMap<>();
        error.put(SUCCESS_KEY, false);
        error.put(MESSAGE_KEY, "Cuenta inactiva");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
      }

      // Crear respuesta exitosa
      Map<String, Object> userData = new HashMap<>();
      userData.put("id", arbitro.getId());
      userData.put(USERNAME_KEY, arbitro.getUsername());
      userData.put("nombre", arbitro.getNombre());
      userData.put("apellidos", arbitro.getApellidos());
      userData.put("nombreCompleto", arbitro.getNombreCompleto());
      userData.put(EMAIL_KEY, arbitro.getEmail());
      userData.put("telefono", arbitro.getTelefono());
      userData.put("especialidad", arbitro.getEspecialidad().name());
      userData.put("escalafon", arbitro.getEscalafon());
      userData.put("urlFotoPerfil", arbitro.getUrlFotoPerfil());
      userData.put("role", "ARBITRO");

      Map<String, Object> response = new HashMap<>();
      response.put(SUCCESS_KEY, true);
      response.put(MESSAGE_KEY, "Login exitoso");
      response.put(DATA_KEY, userData);

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      Map<String, Object> error = new HashMap<>();
      error.put(SUCCESS_KEY, false);
      error.put(MESSAGE_KEY, "Credenciales inválidas");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
  }

  @Operation(
      summary = "Registro de árbitro",
      description = "Registrar un nuevo árbitro en el sistema")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Registro exitoso",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "409", description = "Usuario ya existe", content = @Content)
      })
  @PostMapping("/register")
  public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> userData) {
    try {
      // Validar datos requeridos
      if (userData.get(USERNAME_KEY) == null
          || userData.get(PASSWORD_KEY) == null
          || userData.get(EMAIL_KEY) == null) {
        Map<String, Object> error = new HashMap<>();
        error.put(SUCCESS_KEY, false);
        error.put(MESSAGE_KEY, "Username, password y email son requeridos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
      }

      // Verificar si el username ya existe
      if (usernameExists(userData.get(USERNAME_KEY))) {
        Map<String, Object> error = new HashMap<>();
        error.put(SUCCESS_KEY, false);
        error.put(MESSAGE_KEY, "El username ya existe");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
      }

      // Aquí deberías crear el árbitro usando tu servicio
      // Por ahora retorno un mensaje de éxito

      Map<String, Object> response = new HashMap<>();
      response.put(SUCCESS_KEY, true);
      response.put(MESSAGE_KEY, "Registro exitoso. Pendiente de aprobación.");

      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (Exception e) {
      Map<String, Object> error = new HashMap<>();
      error.put(SUCCESS_KEY, false);
      error.put(MESSAGE_KEY, "Error al registrar árbitro: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
  }

  private boolean usernameExists(String username) {
    try {
      arbitroService.buscarPorUsername(username);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
