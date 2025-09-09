/**
 * Archivo: ExcelExportService.java Autores: Isabella.Idarraga Fecha última modificación:
 * [08.09.2025] Descripción: Servicio para exportar datos a archivos Excel Proyecto: CABA Pro -
 * Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.models.Partido;
import com.caba.caba_pro.models.Torneo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExcelExportService {

  private static final Logger logger = LoggerFactory.getLogger(ExcelExportService.class);
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final DateTimeFormatter DATETIME_FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

  public byte[] exportarArbitros(List<Arbitro> arbitros) throws IOException {
    logger.info("Exportando {} árbitros a Excel", arbitros.size());

    if (arbitros == null || arbitros.isEmpty()) {
      logger.warn("Lista de árbitros está vacía o es null");
      throw new IllegalArgumentException("No hay árbitros para exportar");
    }

    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Árbitros");
      logger.debug("Hoja de Excel 'Árbitros' creada");

      // Crear estilos
      CellStyle headerStyle = createHeaderStyle(workbook);
      CellStyle dataStyle = createDataStyle(workbook);

      // Crear encabezados
      Row headerRow = sheet.createRow(0);
      String[] headers = {
        "ID",
        "Nombre",
        "Apellidos",
        "Número Identificación",
        "Email",
        "Teléfono",
        "Especialidad",
        "Escalafón",
        "Fecha Nacimiento",
        "Fecha Creación",
        "Activo"
      };

      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
        cell.setCellStyle(headerStyle);
      }

      // Llenar datos
      int rowNum = 1;
      for (Arbitro arbitro : arbitros) {
        Row row = sheet.createRow(rowNum++);

        createCell(row, 0, arbitro.getId().toString(), dataStyle);
        createCell(row, 1, arbitro.getNombre(), dataStyle);
        createCell(row, 2, arbitro.getApellidos(), dataStyle);
        createCell(row, 3, arbitro.getNumeroIdentificacion(), dataStyle);
        createCell(row, 4, arbitro.getEmail(), dataStyle);
        createCell(row, 5, arbitro.getTelefono() != null ? arbitro.getTelefono() : "", dataStyle);
        createCell(
            row,
            6,
            arbitro.getEspecialidad() != null ? arbitro.getEspecialidad().toString() : "",
            dataStyle);
        createCell(row, 7, arbitro.getEscalafon() != null ? arbitro.getEscalafon() : "", dataStyle);
        createCell(
            row,
            8,
            arbitro.getFechaNacimiento() != null
                ? arbitro.getFechaNacimiento().format(DATE_FORMATTER)
                : "",
            dataStyle);
        createCell(
            row,
            9,
            arbitro.getFechaCreacion() != null
                ? arbitro.getFechaCreacion().format(DATETIME_FORMATTER)
                : "",
            dataStyle);
        createCell(row, 10, arbitro.isActivo() ? "Sí" : "No", dataStyle);
      }

      // Ajustar ancho de columnas
      for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
      }

      // Convertir a bytes
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return outputStream.toByteArray();
    }
  }

  public byte[] exportarPartidos(List<Partido> partidos) throws IOException {
    logger.info("Exportando {} partidos a Excel", partidos.size());

    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Partidos");

      // Crear estilos
      CellStyle headerStyle = createHeaderStyle(workbook);
      CellStyle dataStyle = createDataStyle(workbook);

      // Crear encabezados
      Row headerRow = sheet.createRow(0);
      String[] headers = {
        "ID",
        "Nombre",
        "Fecha y Hora",
        "Sede",
        "Equipo Local",
        "Equipo Visitante",
        "Estado",
        "Torneo"
      };

      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
        cell.setCellStyle(headerStyle);
      }

      // Llenar datos
      int rowNum = 1;
      for (Partido partido : partidos) {
        Row row = sheet.createRow(rowNum++);

        createCell(row, 0, partido.getId().toString(), dataStyle);
        createCell(row, 1, partido.getNombre(), dataStyle);
        createCell(
            row,
            2,
            partido.getFechaHora() != null ? partido.getFechaHora().format(DATETIME_FORMATTER) : "",
            dataStyle);
        createCell(row, 3, partido.getSede() != null ? partido.getSede() : "", dataStyle);
        createCell(
            row, 4, partido.getEquipoLocal() != null ? partido.getEquipoLocal() : "", dataStyle);
        createCell(
            row,
            5,
            partido.getEquipoVisitante() != null ? partido.getEquipoVisitante() : "",
            dataStyle);
        createCell(
            row, 6, partido.getEstado() != null ? partido.getEstado().toString() : "", dataStyle);
        createCell(
            row, 7, partido.getTorneo() != null ? partido.getTorneo().getNombre() : "", dataStyle);
      }

      // Ajustar ancho de columnas
      for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
      }

      // Convertir a bytes
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return outputStream.toByteArray();
    }
  }

  public byte[] exportarTorneos(List<Torneo> torneos) throws IOException {
    logger.info("Exportando {} torneos a Excel", torneos.size());

    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Torneos");

      // Crear estilos
      CellStyle headerStyle = createHeaderStyle(workbook);
      CellStyle dataStyle = createDataStyle(workbook);

      // Crear encabezados
      Row headerRow = sheet.createRow(0);
      String[] headers = {
        "ID",
        "Nombre",
        "Descripción",
        "Fecha Inicio",
        "Fecha Fin",
        "Estado",
        "Ubicación",
        "Total Partidos",
        "Fecha Creación"
      };

      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
        cell.setCellStyle(headerStyle);
      }

      // Llenar datos
      int rowNum = 1;
      for (Torneo torneo : torneos) {
        Row row = sheet.createRow(rowNum++);

        createCell(row, 0, torneo.getId().toString(), dataStyle);
        createCell(row, 1, torneo.getNombre(), dataStyle);
        createCell(
            row, 2, torneo.getDescripcion() != null ? torneo.getDescripcion() : "", dataStyle);
        createCell(
            row,
            3,
            torneo.getFechaInicio() != null ? torneo.getFechaInicio().format(DATE_FORMATTER) : "",
            dataStyle);
        createCell(
            row,
            4,
            torneo.getFechaFin() != null ? torneo.getFechaFin().format(DATE_FORMATTER) : "",
            dataStyle);
        createCell(
            row,
            5,
            torneo.getEstado() != null ? torneo.getEstado().getDescripcion() : "",
            dataStyle);
        createCell(row, 6, torneo.getUbicacion() != null ? torneo.getUbicacion() : "", dataStyle);
        createCell(row, 7, String.valueOf(torneo.getTotalPartidos()), dataStyle);
        createCell(
            row,
            8,
            torneo.getFechaCreacion() != null
                ? torneo.getFechaCreacion().format(DATETIME_FORMATTER)
                : "",
            dataStyle);
      }

      // Ajustar ancho de columnas
      for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
      }

      // Convertir a bytes
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return outputStream.toByteArray();
    }
  }

  private CellStyle createHeaderStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setBold(true);
    font.setColor(IndexedColors.WHITE.getIndex());
    style.setFont(font);
    style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setAlignment(HorizontalAlignment.CENTER);
    return style;
  }

  private CellStyle createDataStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    return style;
  }

  private void createCell(Row row, int columnIndex, String value, CellStyle style) {
    Cell cell = row.createCell(columnIndex);
    cell.setCellValue(value != null ? value : "");
    cell.setCellStyle(style);
  }

  public String generateFilename(String type) {
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    return String.format("CABA_Pro_%s_%s.xlsx", type, timestamp);
  }
}
