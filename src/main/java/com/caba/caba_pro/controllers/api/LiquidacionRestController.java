/**
 * Archivo: LiquidacionRestController.java Autores: JJRodriguezz Fecha última modificación:
 * 11.11.2025 Descripción: Controlador REST para generación de liquidaciones de pago a árbitros
 * Proyecto: CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers.api;

import com.caba.caba_pro.dto.LiquidacionDTO;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.services.ExcelExportService;
import com.caba.caba_pro.services.LiquidacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/liquidaciones")
@Tag(name = "Liquidaciones", description = "Endpoints para generación de liquidaciones de pago")
public class LiquidacionRestController {

  private static final Logger logger = LoggerFactory.getLogger(LiquidacionRestController.class);

  // 2. Variables de instancia
  private final LiquidacionService liquidacionService;
  private final ExcelExportService excelExportService;

  // 3. Constructores
  public LiquidacionRestController(
      LiquidacionService liquidacionService, ExcelExportService excelExportService) {
    this.liquidacionService = liquidacionService;
    this.excelExportService = excelExportService;
  }

  // 4. Métodos públicos

  @Operation(
      summary = "Generar liquidación para todos los árbitros",
      description =
          "Genera un reporte de liquidación para todos los árbitros en un rango de fechas"
              + " específico")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liquidación generada exitosamente",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "400",
            description = "Parámetros inválidos (fechas incorrectas)",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content)
      })
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<LiquidacionDTO> generarLiquidacion(
      @Parameter(description = "Fecha de inicio del período (formato: yyyy-MM-dd)", required = true)
          @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fechaInicio,
      @Parameter(description = "Fecha de fin del período (formato: yyyy-MM-dd)", required = true)
          @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fechaFin) {

    try {
      logger.info("Generando liquidación para período {} - {}", fechaInicio, fechaFin);

      // Validar que fechaInicio sea anterior a fechaFin
      if (fechaInicio.isAfter(fechaFin)) {
        logger.error("Fecha de inicio posterior a fecha de fin: {} > {}", fechaInicio, fechaFin);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
      }

      LiquidacionDTO liquidacion =
          liquidacionService.generarLiquidacion(
              fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59));

      logger.info(
          "Liquidación generada exitosamente con {} árbitros", liquidacion.getArbitros().size());

      return ResponseEntity.ok(liquidacion);

    } catch (Exception e) {
      logger.error("Error al generar liquidación: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @Operation(
      summary = "Generar liquidación para un árbitro específico",
      description = "Genera un reporte de liquidación para un árbitro en un rango de fechas")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liquidación generada exitosamente",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "400",
            description = "Parámetros inválidos",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Árbitro no encontrado",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content)
      })
  @GetMapping("/arbitro/{arbitroId}")
  @PreAuthorize(
      "hasRole('ADMIN') or (hasRole('ARBITRO') and #arbitroId == authentication.principal.id)")
  public ResponseEntity<LiquidacionDTO.LiquidacionArbitroDTO> generarLiquidacionPorArbitro(
      @Parameter(description = "ID del árbitro", required = true) @PathVariable Long arbitroId,
      @Parameter(description = "Fecha de inicio del período (formato: yyyy-MM-dd)", required = true)
          @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fechaInicio,
      @Parameter(description = "Fecha de fin del período (formato: yyyy-MM-dd)", required = true)
          @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fechaFin) {

    try {
      logger.info(
          "Generando liquidación para árbitro {} en período {} - {}",
          arbitroId,
          fechaInicio,
          fechaFin);

      // Validar que fechaInicio sea anterior a fechaFin
      if (fechaInicio.isAfter(fechaFin)) {
        logger.error("Fecha de inicio posterior a fecha de fin: {} > {}", fechaInicio, fechaFin);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
      }

      LiquidacionDTO.LiquidacionArbitroDTO liquidacion =
          liquidacionService.generarLiquidacionPorArbitro(
              arbitroId, fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59));

      logger.info("Liquidación generada exitosamente para árbitro {}", arbitroId);

      return ResponseEntity.ok(liquidacion);

    } catch (BusinessException e) {
      logger.error("Error de negocio al generar liquidación: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } catch (Exception e) {
      logger.error(
          "Error al generar liquidación para árbitro {}: {}", arbitroId, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @Operation(
      summary = "Descargar liquidación en formato Excel",
      description = "Genera y descarga un archivo Excel con la liquidación de todos los árbitros")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Archivo Excel generado exitosamente",
            content =
                @Content(
                    mediaType =
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
        @ApiResponse(
            responseCode = "400",
            description = "Parámetros inválidos",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Error al generar el archivo",
            content = @Content)
      })
  @GetMapping("/excel")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<byte[]> descargarLiquidacionExcel(
      @Parameter(description = "Fecha de inicio del período (formato: yyyy-MM-dd)", required = true)
          @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fechaInicio,
      @Parameter(description = "Fecha de fin del período (formato: yyyy-MM-dd)", required = true)
          @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fechaFin) {

    try {
      logger.info(
          "Generando archivo Excel de liquidación para período {} - {}", fechaInicio, fechaFin);

      // Validar que fechaInicio sea anterior a fechaFin
      if (fechaInicio.isAfter(fechaFin)) {
        logger.error("Fecha de inicio posterior a fecha de fin: {} > {}", fechaInicio, fechaFin);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
      }

      // Generar liquidación
      LiquidacionDTO liquidacion =
          liquidacionService.generarLiquidacion(
              fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59));

      // Exportar a Excel (ya retorna bytes)
      byte[] bytes = excelExportService.exportarLiquidacion(liquidacion);

      // Generar nombre de archivo
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      String filename =
          String.format(
              "liquidacion_%s_%s.xlsx", fechaInicio.format(formatter), fechaFin.format(formatter));

      logger.info("Archivo Excel generado exitosamente: {}", filename);

      // Configurar headers para descarga
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      headers.setContentDispositionFormData("attachment", filename);
      headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

      return ResponseEntity.ok().headers(headers).body(bytes);

    } catch (IOException e) {
      logger.error("Error de I/O al generar archivo Excel: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    } catch (Exception e) {
      logger.error("Error al generar archivo Excel de liquidación: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @Operation(
      summary = "Descargar liquidación de un árbitro en formato Excel",
      description =
          "Genera y descarga un archivo Excel con la liquidación de un árbitro específico")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Archivo Excel generado exitosamente",
            content =
                @Content(
                    mediaType =
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
        @ApiResponse(
            responseCode = "400",
            description = "Parámetros inválidos",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Árbitro no encontrado",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Error al generar el archivo",
            content = @Content)
      })
  @GetMapping("/arbitro/{arbitroId}/excel")
  @PreAuthorize(
      "hasRole('ADMIN') or (hasRole('ARBITRO') and #arbitroId == authentication.principal.id)")
  public ResponseEntity<byte[]> descargarLiquidacionArbitroExcel(
      @Parameter(description = "ID del árbitro", required = true) @PathVariable Long arbitroId,
      @Parameter(description = "Fecha de inicio del período (formato: yyyy-MM-dd)", required = true)
          @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fechaInicio,
      @Parameter(description = "Fecha de fin del período (formato: yyyy-MM-dd)", required = true)
          @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fechaFin) {

    try {
      logger.info(
          "Generando archivo Excel de liquidación para árbitro {} en período {} - {}",
          arbitroId,
          fechaInicio,
          fechaFin);

      // Validar que fechaInicio sea anterior a fechaFin
      if (fechaInicio.isAfter(fechaFin)) {
        logger.error("Fecha de inicio posterior a fecha de fin: {} > {}", fechaInicio, fechaFin);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
      }

      // Generar liquidación del árbitro
      LiquidacionDTO.LiquidacionArbitroDTO arbitroLiquidacion =
          liquidacionService.generarLiquidacionPorArbitro(
              arbitroId, fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59));

      if (arbitroLiquidacion == null) {
        logger.warn("No hay liquidación para el árbitro {} en el período especificado", arbitroId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }

      // Crear LiquidacionDTO con un solo árbitro para el export
      LiquidacionDTO liquidacion = new LiquidacionDTO();
      liquidacion.setFechaInicio(fechaInicio.atStartOfDay());
      liquidacion.setFechaFin(fechaFin.atTime(23, 59, 59));
      liquidacion.getArbitros().add(arbitroLiquidacion);
      liquidacion.setTotalGeneral(arbitroLiquidacion.getTotalAPagar());
      liquidacion.setTotalAsignaciones(arbitroLiquidacion.getCantidadPartidos());

      // Exportar a Excel (ya retorna bytes)
      byte[] bytes = excelExportService.exportarLiquidacion(liquidacion);

      // Generar nombre de archivo
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      String filename =
          String.format(
              "liquidacion_arbitro_%d_%s_%s.xlsx",
              arbitroId, fechaInicio.format(formatter), fechaFin.format(formatter));

      logger.info("Archivo Excel generado exitosamente: {}", filename);

      // Configurar headers para descarga
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      headers.setContentDispositionFormData("attachment", filename);
      headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

      return ResponseEntity.ok().headers(headers).body(bytes);

    } catch (BusinessException e) {
      logger.error("Error de negocio al generar liquidación: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } catch (IOException e) {
      logger.error("Error de I/O al generar archivo Excel: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    } catch (Exception e) {
      logger.error(
          "Error al generar archivo Excel de liquidación para árbitro {}: {}",
          arbitroId,
          e.getMessage(),
          e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
