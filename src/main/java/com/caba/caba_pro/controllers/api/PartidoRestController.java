/**
 * Archivo: PartidoRestController.java Autores: JJRodriguezz Fecha última modificación: 11.11.2025
 * Descripción: Controlador REST para gestión de partidos (API JSON) Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers.api;

import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Partido;
import com.caba.caba_pro.services.PartidoService;
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
@RequestMapping("/api/partidos")
@Tag(name = "Partidos", description = "Endpoints para gestión de partidos")
public class PartidoRestController {

  // 2. Variables de instancia
  private final PartidoService partidoService;

  // 3. Constructores
  public PartidoRestController(PartidoService partidoService) {
    this.partidoService = partidoService;
  }

  // 4. Métodos públicos

  @Operation(
      summary = "Listar todos los partidos activos",
      description = "Obtiene la lista completa de partidos activos en el sistema")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de partidos obtenida exitosamente",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content)
      })
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<Map<String, Object>>> listarPartidos() {
    List<Partido> partidos = partidoService.buscarActivos();

    List<Map<String, Object>> partidosData =
        partidos.stream()
            .map(
                partido -> {
                  Map<String, Object> data = new HashMap<>();
                  data.put("id", partido.getId());
                  data.put("nombre", partido.getNombre());
                  data.put("descripcion", partido.getDescripcion());
                  data.put("fechaHora", partido.getFechaHora());
                  data.put("sede", partido.getSede());
                  data.put("equipoLocal", partido.getEquipoLocal());
                  data.put("equipoVisitante", partido.getEquipoVisitante());
                  data.put("estado", partido.getEstado().name());
                  data.put("activo", partido.getActivo());
                  data.put(
                      "torneo",
                      partido.getTorneo() != null ? partido.getTorneo().getNombre() : null);
                  data.put(
                      "torneoId", partido.getTorneo() != null ? partido.getTorneo().getId() : null);
                  data.put("numeroAsignaciones", partido.getAsignaciones().size());
                  return data;
                })
            .collect(Collectors.toList());

    return ResponseEntity.ok(partidosData);
  }

  @Operation(
      summary = "Obtener partido por ID",
      description = "Obtiene los datos completos de un partido específico por su ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Partido encontrado exitosamente",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "404",
            description = "Partido no encontrado",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content)
      })
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('ARBITRO')")
  public ResponseEntity<Map<String, Object>> obtenerPartidoPorId(
      @Parameter(description = "ID del partido a buscar", required = true) @PathVariable Long id) {
    try {
      Partido partido = partidoService.buscarPorId(id);

      Map<String, Object> data = new HashMap<>();
      data.put("id", partido.getId());
      data.put("nombre", partido.getNombre());
      data.put("descripcion", partido.getDescripcion());
      data.put("fechaHora", partido.getFechaHora());
      data.put("sede", partido.getSede());
      data.put("equipoLocal", partido.getEquipoLocal());
      data.put("equipoVisitante", partido.getEquipoVisitante());
      data.put("estado", partido.getEstado().name());
      data.put("activo", partido.getActivo());

      // Información del torneo
      if (partido.getTorneo() != null) {
        Map<String, Object> torneoData = new HashMap<>();
        torneoData.put("id", partido.getTorneo().getId());
        torneoData.put("nombre", partido.getTorneo().getNombre());
        torneoData.put("estado", partido.getTorneo().getEstado().name());
        data.put("torneo", torneoData);
      }

      // Información de asignaciones
      List<Map<String, Object>> asignacionesData =
          partido.getAsignaciones().stream()
              .filter(asignacion -> asignacion.getActivo())
              .map(
                  asignacion -> {
                    Map<String, Object> asigData = new HashMap<>();
                    asigData.put("id", asignacion.getId());
                    asigData.put("posicion", asignacion.getPosicion());
                    asigData.put("estado", asignacion.getEstado().name());
                    asigData.put("montoPago", asignacion.getMontoPago());
                    asigData.put("arbitroId", asignacion.getArbitro().getId());
                    asigData.put("arbitroNombre", asignacion.getArbitro().getNombreCompleto());
                    return asigData;
                  })
              .collect(Collectors.toList());

      data.put("asignaciones", asignacionesData);

      return ResponseEntity.ok(data);
    } catch (BusinessException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
  }
}
