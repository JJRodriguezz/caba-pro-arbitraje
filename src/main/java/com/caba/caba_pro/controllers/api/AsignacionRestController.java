/**
 * Archivo: AsignacionRestController.java Autores: JJRodriguezz Fecha última modificación:
 * 11.11.2025 Descripción: Controlador REST para gestión de asignaciones (API JSON) Proyecto: CABA
 * Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers.api;

import com.caba.caba_pro.models.Asignacion;
import com.caba.caba_pro.repositories.AsignacionRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/asignaciones")
@Tag(name = "Asignaciones", description = "Endpoints para gestión de asignaciones de árbitros")
public class AsignacionRestController {

  // 2. Variables de instancia
  private final AsignacionRepository asignacionRepository;

  // 3. Constructores
  public AsignacionRestController(AsignacionRepository asignacionRepository) {
    this.asignacionRepository = asignacionRepository;
  }

  // 4. Métodos públicos

  @Operation(
      summary = "Listar todas las asignaciones activas",
      description = "Obtiene la lista completa de asignaciones activas en el sistema")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de asignaciones obtenida exitosamente",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content)
      })
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<Map<String, Object>>> listarAsignaciones() {
    List<Asignacion> asignaciones = asignacionRepository.findByActivoTrue();

    List<Map<String, Object>> asignacionesData =
        asignaciones.stream()
            .map(
                asignacion -> {
                  Map<String, Object> data = new HashMap<>();
                  data.put("id", asignacion.getId());
                  data.put("posicion", asignacion.getPosicion());
                  data.put("estado", asignacion.getEstado().name());
                  data.put("asignadoEn", asignacion.getAsignadoEn());
                  data.put("respondidoEn", asignacion.getRespondidoEn());
                  data.put("montoPago", asignacion.getMontoPago());
                  data.put("notasAdmin", asignacion.getNotasAdmin());
                  data.put("adminUsername", asignacion.getAdminUsername());

                  // Datos del árbitro
                  Map<String, Object> arbitroData = new HashMap<>();
                  arbitroData.put("id", asignacion.getArbitro().getId());
                  arbitroData.put("nombre", asignacion.getArbitro().getNombreCompleto());
                  arbitroData.put("username", asignacion.getArbitro().getUsername());
                  arbitroData.put("especialidad", asignacion.getArbitro().getEspecialidad().name());
                  data.put("arbitro", arbitroData);

                  // Datos del partido
                  Map<String, Object> partidoData = new HashMap<>();
                  partidoData.put("id", asignacion.getPartido().getId());
                  partidoData.put("nombre", asignacion.getPartido().getNombre());
                  partidoData.put("fechaHora", asignacion.getPartido().getFechaHora());
                  partidoData.put("sede", asignacion.getPartido().getSede());
                  partidoData.put("equipoLocal", asignacion.getPartido().getEquipoLocal());
                  partidoData.put("equipoVisitante", asignacion.getPartido().getEquipoVisitante());
                  data.put("partido", partidoData);

                  return data;
                })
            .collect(Collectors.toList());

    return ResponseEntity.ok(asignacionesData);
  }

  @Operation(
      summary = "Obtener asignaciones por árbitro",
      description = "Obtiene todas las asignaciones activas de un árbitro específico")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de asignaciones obtenida exitosamente",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content)
      })
  @GetMapping("/arbitro/{arbitroId}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('ARBITRO')")
  public ResponseEntity<List<Map<String, Object>>> obtenerAsignacionesPorArbitro(
      @Parameter(description = "ID del árbitro", required = true) @PathVariable Long arbitroId) {
    List<Asignacion> asignaciones = asignacionRepository.findByArbitroIdAndActivoTrue(arbitroId);

    List<Map<String, Object>> asignacionesData =
        asignaciones.stream()
            .map(
                asignacion -> {
                  Map<String, Object> data = new HashMap<>();
                  data.put("id", asignacion.getId());
                  data.put("posicion", asignacion.getPosicion());
                  data.put("estado", asignacion.getEstado().name());
                  data.put("asignadoEn", asignacion.getAsignadoEn());
                  data.put("respondidoEn", asignacion.getRespondidoEn());
                  data.put("montoPago", asignacion.getMontoPago());
                  data.put("notasAdmin", asignacion.getNotasAdmin());

                  // Datos del partido
                  Map<String, Object> partidoData = new HashMap<>();
                  partidoData.put("id", asignacion.getPartido().getId());
                  partidoData.put("nombre", asignacion.getPartido().getNombre());
                  partidoData.put("fechaHora", asignacion.getPartido().getFechaHora());
                  partidoData.put("sede", asignacion.getPartido().getSede());
                  partidoData.put("equipoLocal", asignacion.getPartido().getEquipoLocal());
                  partidoData.put("equipoVisitante", asignacion.getPartido().getEquipoVisitante());
                  partidoData.put("estado", asignacion.getPartido().getEstado().name());
                  data.put("partido", partidoData);

                  return data;
                })
            .collect(Collectors.toList());

    return ResponseEntity.ok(asignacionesData);
  }
}
