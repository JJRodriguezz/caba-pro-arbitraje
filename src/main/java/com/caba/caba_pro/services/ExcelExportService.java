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

  /**
   * Exporta un reporte de liquidación a Excel
   *
   * @param liquidacion DTO con los datos de liquidación
   * @return Bytes del archivo Excel
   * @throws IOException Si hay error al generar el archivo
   */
  public byte[] exportarLiquidacion(com.caba.caba_pro.dto.LiquidacionDTO liquidacion)
      throws IOException {
    logger.info("Exportando liquidación de {} árbitros a Excel", liquidacion.getArbitros().size());

    if (liquidacion == null || liquidacion.getArbitros().isEmpty()) {
      throw new IllegalArgumentException("No hay datos de liquidación para exportar");
    }

    try (Workbook workbook = new XSSFWorkbook()) {
      // Crear estilos
      CellStyle headerStyle = createHeaderStyle(workbook);
      CellStyle dataStyle = createDataStyle(workbook);
      CellStyle moneyStyle = createDataStyle(workbook);
      moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));

      CellStyle subtotalStyle = workbook.createCellStyle();
      subtotalStyle.cloneStyleFrom(dataStyle);
      Font boldFont = workbook.createFont();
      boldFont.setBold(true);
      subtotalStyle.setFont(boldFont);
      subtotalStyle.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));

      // Hoja de resumen
      Sheet resumenSheet = workbook.createSheet("Resumen");
      crearHojaResumenLiquidacion(resumenSheet, liquidacion, headerStyle, dataStyle, moneyStyle);

      // Hoja detallada por árbitro
      Sheet detalleSheet = workbook.createSheet("Detalle por Árbitro");
      crearHojaDetalleLiquidacion(
          detalleSheet, liquidacion, headerStyle, dataStyle, moneyStyle, subtotalStyle);

      // Convertir a bytes
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return outputStream.toByteArray();
    }
  }

  private void crearHojaResumenLiquidacion(
      Sheet sheet,
      com.caba.caba_pro.dto.LiquidacionDTO liquidacion,
      CellStyle headerStyle,
      CellStyle dataStyle,
      CellStyle moneyStyle) {

    int rowNum = 0;

    // Título y período
    Row titleRow = sheet.createRow(rowNum++);
    createCell(titleRow, 0, "REPORTE DE LIQUIDACIÓN DE ÁRBITROS", headerStyle);
    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 4));

    rowNum++; // Espacio

    Row periodoRow = sheet.createRow(rowNum++);
    createCell(periodoRow, 0, "Período:", dataStyle);
    createCell(
        periodoRow,
        1,
        liquidacion.getFechaInicio().format(DATETIME_FORMATTER)
            + " - "
            + liquidacion.getFechaFin().format(DATETIME_FORMATTER),
        dataStyle);

    Row generacionRow = sheet.createRow(rowNum++);
    createCell(generacionRow, 0, "Fecha de generación:", dataStyle);
    createCell(
        generacionRow, 1, liquidacion.getFechaGeneracion().format(DATETIME_FORMATTER), dataStyle);

    rowNum++; // Espacio

    // Estadísticas generales
    Row statsHeaderRow = sheet.createRow(rowNum++);
    createCell(statsHeaderRow, 0, "Concepto", headerStyle);
    createCell(statsHeaderRow, 1, "Cantidad/Valor", headerStyle);

    Row totalArbitrosRow = sheet.createRow(rowNum++);
    createCell(totalArbitrosRow, 0, "Total de árbitros", dataStyle);
    createCell(totalArbitrosRow, 1, String.valueOf(liquidacion.getArbitros().size()), dataStyle);

    Row totalPartidosRow = sheet.createRow(rowNum++);
    createCell(totalPartidosRow, 0, "Total de partidos", dataStyle);
    createCell(totalPartidosRow, 1, String.valueOf(liquidacion.getTotalPartidos()), dataStyle);

    Row totalAsignacionesRow = sheet.createRow(rowNum++);
    createCell(totalAsignacionesRow, 0, "Total de asignaciones", dataStyle);
    createCell(
        totalAsignacionesRow, 1, String.valueOf(liquidacion.getTotalAsignaciones()), dataStyle);

    rowNum++; // Espacio

    Row totalGeneralRow = sheet.createRow(rowNum++);
    createCell(totalGeneralRow, 0, "TOTAL A PAGAR", headerStyle);
    Cell totalCell = totalGeneralRow.createCell(1);
    totalCell.setCellValue(liquidacion.getTotalGeneral().doubleValue());
    totalCell.setCellStyle(moneyStyle);

    rowNum += 2; // Espacio

    // Resumen por árbitro
    Row arbitrosHeaderRow = sheet.createRow(rowNum++);
    createCell(arbitrosHeaderRow, 0, "Árbitro", headerStyle);
    createCell(arbitrosHeaderRow, 1, "Identificación", headerStyle);
    createCell(arbitrosHeaderRow, 2, "Escalafón", headerStyle);
    createCell(arbitrosHeaderRow, 3, "Cant. Partidos", headerStyle);
    createCell(arbitrosHeaderRow, 4, "Total a Pagar", headerStyle);

    for (com.caba.caba_pro.dto.LiquidacionDTO.LiquidacionArbitroDTO arbitro :
        liquidacion.getArbitros()) {
      Row row = sheet.createRow(rowNum++);
      createCell(row, 0, arbitro.getNombreCompleto(), dataStyle);
      createCell(row, 1, arbitro.getNumeroIdentificacion(), dataStyle);
      createCell(row, 2, arbitro.getEscalafon(), dataStyle);
      createCell(row, 3, String.valueOf(arbitro.getCantidadPartidos()), dataStyle);

      Cell moneyCell = row.createCell(4);
      moneyCell.setCellValue(arbitro.getTotalAPagar().doubleValue());
      moneyCell.setCellStyle(moneyStyle);
    }

    // Ajustar anchos
    for (int i = 0; i < 5; i++) {
      sheet.autoSizeColumn(i);
    }
  }

  private void crearHojaDetalleLiquidacion(
      Sheet sheet,
      com.caba.caba_pro.dto.LiquidacionDTO liquidacion,
      CellStyle headerStyle,
      CellStyle dataStyle,
      CellStyle moneyStyle,
      CellStyle subtotalStyle) {

    int rowNum = 0;

    // Título
    Row titleRow = sheet.createRow(rowNum++);
    createCell(titleRow, 0, "DETALLE DE LIQUIDACIÓN POR ÁRBITRO", headerStyle);
    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

    rowNum++; // Espacio

    // Encabezados de columnas
    String[] headers = {"Árbitro", "Fecha Partido", "Partido", "Torneo", "Posición", "Monto"};

    Row headerRow = sheet.createRow(rowNum++);
    for (int i = 0; i < headers.length; i++) {
      createCell(headerRow, i, headers[i], headerStyle);
    }

    // Iterar por árbitro
    for (com.caba.caba_pro.dto.LiquidacionDTO.LiquidacionArbitroDTO arbitro :
        liquidacion.getArbitros()) {

      // Nombre del árbitro (fila de título)
      Row arbitroRow = sheet.createRow(rowNum++);
      Cell arbitroCell = arbitroRow.createCell(0);
      arbitroCell.setCellValue(
          arbitro.getNombreCompleto()
              + " - "
              + arbitro.getNumeroIdentificacion()
              + " ("
              + arbitro.getEscalafon()
              + ")");
      arbitroCell.setCellStyle(headerStyle);
      sheet.addMergedRegion(
          new org.apache.poi.ss.util.CellRangeAddress(rowNum - 1, rowNum - 1, 0, 5));

      // Partidos del árbitro
      for (com.caba.caba_pro.dto.LiquidacionDTO.DetallePartidoDTO partido : arbitro.getPartidos()) {
        Row row = sheet.createRow(rowNum++);
        createCell(row, 0, "", dataStyle); // Espacio bajo nombre de árbitro
        createCell(row, 1, partido.getFechaPartido().format(DATETIME_FORMATTER), dataStyle);
        createCell(row, 2, partido.getNombrePartido(), dataStyle);
        createCell(row, 3, partido.getTorneo(), dataStyle);
        createCell(row, 4, partido.getPosicion(), dataStyle);

        Cell moneyCell = row.createCell(5);
        moneyCell.setCellValue(partido.getMontoPago().doubleValue());
        moneyCell.setCellStyle(moneyStyle);
      }

      // Subtotal del árbitro
      Row subtotalRow = sheet.createRow(rowNum++);
      createCell(subtotalRow, 4, "Subtotal:", subtotalStyle);
      Cell subtotalCell = subtotalRow.createCell(5);
      subtotalCell.setCellValue(arbitro.getTotalAPagar().doubleValue());
      subtotalCell.setCellStyle(subtotalStyle);

      rowNum++; // Espacio entre árbitros
    }

    // Ajustar anchos
    for (int i = 0; i < headers.length; i++) {
      sheet.autoSizeColumn(i);
    }
  }
}
