/**
 * Archivo: ArbitroRestController.java Autores: JJRodriguezz Fecha última modificación: 11.11.2025
 * Descripción: Controlador REST para gestión de árbitros (API JSON) Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers.api;

import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.services.ArbitroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/arbitros")
@Tag(name = "Árbitros", description = "Endpoints para gestión de árbitros")
public class ArbitroRestController {

  // 2. Variables de instancia
  private final ArbitroService arbitroService;

  // 3. Constructores
  public ArbitroRestController(ArbitroService arbitroService) {
    this.arbitroService = arbitroService;
  }

  // 4. Métodos públicos

  @Operation(
      summary = "Listar todos los árbitros activos",
      description = "Obtiene la lista completa de árbitros activos en el sistema")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de árbitros obtenida exitosamente",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content)
      })
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<Map<String, Object>>> listarArbitros() {
    List<Arbitro> arbitros = arbitroService.buscarTodosActivos();

    List<Map<String, Object>> arbitrosData =
        arbitros.stream()
            .map(
                arbitro -> {
                  Map<String, Object> data = new HashMap<>();
                  data.put("id", arbitro.getId());
                  data.put("nombre", arbitro.getNombre());
                  data.put("apellidos", arbitro.getApellidos());
                  data.put("nombreCompleto", arbitro.getNombreCompleto());
                  data.put("email", arbitro.getEmail());
                  data.put("telefono", arbitro.getTelefono());
                  data.put("especialidad", arbitro.getEspecialidad().name());
                  data.put("escalafon", arbitro.getEscalafon());
                  data.put("username", arbitro.getUsername());
                  data.put("activo", arbitro.getActivo());
                  data.put("urlFotoPerfil", arbitro.getUrlFotoPerfil());
                  return data;
                })
            .collect(Collectors.toList());

    return ResponseEntity.ok(arbitrosData);
  }

  @Operation(
      summary = "Obtener árbitro por ID",
      description = "Obtiene los datos completos de un árbitro específico por su ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Árbitro encontrado exitosamente",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "404",
            description = "Árbitro no encontrado",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content)
      })
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('ARBITRO')")
  public ResponseEntity<Map<String, Object>> obtenerArbitroPorId(
      @Parameter(description = "ID del árbitro a buscar", required = true) @PathVariable Long id) {
    try {
      Arbitro arbitro = arbitroService.buscarPorId(id);

      Map<String, Object> data = new HashMap<>();
      data.put("id", arbitro.getId());
      data.put("nombre", arbitro.getNombre());
      data.put("apellidos", arbitro.getApellidos());
      data.put("nombreCompleto", arbitro.getNombreCompleto());
      data.put("numeroIdentificacion", arbitro.getNumeroIdentificacion());
      data.put("email", arbitro.getEmail());
      data.put("telefono", arbitro.getTelefono());
      data.put("especialidad", arbitro.getEspecialidad().name());
      data.put("especialidadDescripcion", arbitro.getEspecialidad().getDescripcion());
      data.put("escalafon", arbitro.getEscalafon());
      data.put("fechaNacimiento", arbitro.getFechaNacimiento());
      data.put("username", arbitro.getUsername());
      data.put("activo", arbitro.getActivo());
      data.put("urlFotoPerfil", arbitro.getUrlFotoPerfil());
      data.put("fechaCreacion", arbitro.getFechaCreacion());

      return ResponseEntity.ok(data);
    } catch (BusinessException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
  }

  @Operation(
      summary = "Obtener árbitro por username",
      description = "Obtiene los datos de un árbitro usando su nombre de usuario")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Árbitro encontrado exitosamente",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "404",
            description = "Árbitro no encontrado",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content)
      })
  @GetMapping("/username/{username}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('ARBITRO')")
  public ResponseEntity<Map<String, Object>> obtenerArbitroPorUsername(
      @Parameter(description = "Username del árbitro a buscar", required = true) @PathVariable
          String username) {
    try {
      Arbitro arbitro = arbitroService.buscarPorUsername(username);

      Map<String, Object> data = new HashMap<>();
      data.put("id", arbitro.getId());
      data.put("nombre", arbitro.getNombre());
      data.put("apellidos", arbitro.getApellidos());
      data.put("nombreCompleto", arbitro.getNombreCompleto());
      data.put("email", arbitro.getEmail());
      data.put("telefono", arbitro.getTelefono());
      data.put("especialidad", arbitro.getEspecialidad().name());
      data.put("escalafon", arbitro.getEscalafon());
      data.put("username", arbitro.getUsername());
      data.put("activo", arbitro.getActivo());
      data.put("urlFotoPerfil", arbitro.getUrlFotoPerfil());

      return ResponseEntity.ok(data);
    } catch (BusinessException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
  }
}
