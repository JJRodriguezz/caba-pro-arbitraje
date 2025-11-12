package com.caba.caba_pro.controllers.api;

import com.caba.caba_pro.enums.AsignacionEstado;
import com.caba.caba_pro.models.Asignacion;
import com.caba.caba_pro.repositories.AsignacionRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRestController {

  private final AsignacionRepository asignacionRepository;

  public DashboardRestController(AsignacionRepository asignacionRepository) {
    this.asignacionRepository = asignacionRepository;
  }

  @GetMapping("/arbitro/{arbitroId}")
  public ResponseEntity<Map<String, Object>> getDashboardData(@PathVariable Long arbitroId) {
    Map<String, Object> response = new HashMap<>();
    try {
      List<Asignacion> todasAsignaciones =
          asignacionRepository.findByArbitroIdAndActivoTrue(arbitroId);

      // Contar por estado
      long pendientes =
          todasAsignaciones.stream()
              .filter(a -> a.getEstado() == AsignacionEstado.PENDIENTE)
              .count();
      long aceptadas =
          todasAsignaciones.stream()
              .filter(a -> a.getEstado() == AsignacionEstado.ACEPTADA)
              .count();
      long completadas =
          todasAsignaciones.stream()
              .filter(a -> a.getEstado() == AsignacionEstado.COMPLETADA)
              .count();

      // Calcular total ganado
      BigDecimal totalGanado =
          todasAsignaciones.stream()
              .filter(a -> a.getEstado() == AsignacionEstado.COMPLETADA)
              .map(Asignacion::getMontoPago)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

      Map<String, Object> estadisticas = new HashMap<>();
      estadisticas.put("totalAsignaciones", todasAsignaciones.size());
      estadisticas.put("asignacionesPendientes", pendientes);
      estadisticas.put("asignacionesAceptadas", aceptadas);
      estadisticas.put("asignacionesCompletadas", completadas);
      estadisticas.put("totalGanado", totalGanado);

      response.put("success", true);
      response.put("message", "Estadísticas obtenidas correctamente");
      response.put("data", estadisticas);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "Error al obtener estadísticas: " + e.getMessage());
      return ResponseEntity.status(500).body(response);
    }
  }
}
