/**
 * Archivo: ExcelExportController.java Autores: Isabella.Idarraga Fecha última modificación:
 * [09.09.2025] Descripción: Controlador para manejar exportaciones a Excel Proyecto: CABA Pro -
 * Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.services.ArbitroService;
import com.caba.caba_pro.services.ExcelExportService;
import com.caba.caba_pro.services.PartidoService;
import com.caba.caba_pro.services.TorneoService;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/export")
@PreAuthorize("hasRole('ADMIN')")
public class ExcelExportController {

  private static final Logger logger = LoggerFactory.getLogger(ExcelExportController.class);

  private final ExcelExportService excelExportService;
  private final ArbitroService arbitroService;
  private final PartidoService partidoService;
  private final TorneoService torneoService;
  private final MessageSource messageSource;

  public ExcelExportController(
      ExcelExportService excelExportService,
      ArbitroService arbitroService,
      PartidoService partidoService,
      TorneoService torneoService,
      MessageSource messageSource) {
    this.excelExportService = excelExportService;
    this.arbitroService = arbitroService;
    this.partidoService = partidoService;
    this.torneoService = torneoService;
    this.messageSource = messageSource;
  }

  @GetMapping("/arbitros")
  public ResponseEntity<byte[]> exportarArbitros(RedirectAttributes redirectAttributes) {
    try {
      logger.info("Iniciando exportación de árbitros a Excel");

      var arbitros = arbitroService.buscarTodosActivos();
      byte[] excelData = excelExportService.exportarArbitros(arbitros);
      String filename = excelExportService.generateFilename("Arbitros");

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(
          MediaType.parseMediaType(
              "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
      headers.setContentDispositionFormData("attachment", filename);
      headers.setContentLength(excelData.length);

      logger.info("Exportación de árbitros completada exitosamente. Archivo: {}", filename);
      return new ResponseEntity<>(excelData, headers, HttpStatus.OK);

    } catch (IOException e) {
      logger.error("Error al exportar árbitros a Excel: {}", e.getMessage(), e);
      String mensaje =
          messageSource.getMessage("excel.error.generar", null, LocaleContextHolder.getLocale());
      redirectAttributes.addFlashAttribute("error", mensaje);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error inesperado al exportar árbitros: {}", e.getMessage(), e);
      String mensaje =
          messageSource.getMessage("excel.error.inesperado", null, LocaleContextHolder.getLocale());
      redirectAttributes.addFlashAttribute("error", mensaje);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/partidos")
  public ResponseEntity<byte[]> exportarPartidos(RedirectAttributes redirectAttributes) {
    try {
      logger.info("Iniciando exportación de partidos a Excel");

      var partidos = partidoService.buscarTodos();
      byte[] excelData = excelExportService.exportarPartidos(partidos);
      String filename = excelExportService.generateFilename("Partidos");

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(
          MediaType.parseMediaType(
              "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
      headers.setContentDispositionFormData("attachment", filename);
      headers.setContentLength(excelData.length);

      logger.info("Exportación de partidos completada exitosamente. Archivo: {}", filename);
      return new ResponseEntity<>(excelData, headers, HttpStatus.OK);

    } catch (IOException e) {
      logger.error("Error al exportar partidos a Excel: {}", e.getMessage(), e);
      String mensaje =
          messageSource.getMessage("excel.error.generar", null, LocaleContextHolder.getLocale());
      redirectAttributes.addFlashAttribute("error", mensaje);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error inesperado al exportar partidos: {}", e.getMessage(), e);
      String mensaje =
          messageSource.getMessage("excel.error.inesperado", null, LocaleContextHolder.getLocale());
      redirectAttributes.addFlashAttribute("error", mensaje);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/torneos")
  public ResponseEntity<byte[]> exportarTorneos(RedirectAttributes redirectAttributes) {
    try {
      logger.info("Iniciando exportación de torneos a Excel");

      var torneos = torneoService.buscarTodos();
      byte[] excelData = excelExportService.exportarTorneos(torneos);
      String filename = excelExportService.generateFilename("Torneos");

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(
          MediaType.parseMediaType(
              "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
      headers.setContentDispositionFormData("attachment", filename);
      headers.setContentLength(excelData.length);

      logger.info("Exportación de torneos completada exitosamente. Archivo: {}", filename);
      return new ResponseEntity<>(excelData, headers, HttpStatus.OK);

    } catch (IOException e) {
      logger.error("Error al exportar torneos a Excel: {}", e.getMessage(), e);
      String mensaje =
          messageSource.getMessage("excel.error.generar", null, LocaleContextHolder.getLocale());
      redirectAttributes.addFlashAttribute("error", mensaje);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error inesperado al exportar torneos: {}", e.getMessage(), e);
      String mensaje =
          messageSource.getMessage("excel.error.inesperado", null, LocaleContextHolder.getLocale());
      redirectAttributes.addFlashAttribute("error", mensaje);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
