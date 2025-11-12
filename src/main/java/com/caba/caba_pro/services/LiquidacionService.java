/**
 * Archivo: LiquidacionService.java Autores: JJRodriguezz Fecha última modificación: 11.11.2025
 * Descripción: Servicio para generar reportes de liquidación de pagos a árbitros Proyecto: CABA Pro
 * - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.dto.LiquidacionDTO;
import com.caba.caba_pro.dto.LiquidacionDTO.DetallePartidoDTO;
import com.caba.caba_pro.dto.LiquidacionDTO.LiquidacionArbitroDTO;
import com.caba.caba_pro.enums.AsignacionEstado;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.models.Asignacion;
import com.caba.caba_pro.repositories.ArbitroRepository;
import com.caba.caba_pro.repositories.AsignacionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LiquidacionService {

  private static final Logger logger = LoggerFactory.getLogger(LiquidacionService.class);

  private final AsignacionRepository asignacionRepository;
  private final ArbitroRepository arbitroRepository;

  public LiquidacionService(
      AsignacionRepository asignacionRepository, ArbitroRepository arbitroRepository) {
    this.asignacionRepository = asignacionRepository;
    this.arbitroRepository = arbitroRepository;
  }

  /**
   * Genera un reporte de liquidación para un período específico
   *
   * @param fechaInicio Fecha de inicio del período
   * @param fechaFin Fecha de fin del período
   * @return DTO con la liquidación completa
   */
  @Transactional(readOnly = true)
  public LiquidacionDTO generarLiquidacion(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
    logger.info("Generando liquidación desde {} hasta {}", fechaInicio, fechaFin);

    LiquidacionDTO liquidacion = new LiquidacionDTO();
    liquidacion.setFechaInicio(fechaInicio);
    liquidacion.setFechaFin(fechaFin);

    // Obtener asignaciones completadas en el período
    List<Asignacion> asignaciones =
        asignacionRepository.findByPartidoFechaHoraBetweenAndEstadoAndActivo(
            fechaInicio, fechaFin, AsignacionEstado.COMPLETADA, true);

    logger.info("Se encontraron {} asignaciones completadas en el período", asignaciones.size());

    // Agrupar por árbitro
    Map<Long, LiquidacionArbitroDTO> arbitrosMap = new HashMap<>();

    for (Asignacion asignacion : asignaciones) {
      Long arbitroId = asignacion.getArbitro().getId();

      // Obtener o crear DTO del árbitro
      LiquidacionArbitroDTO arbitroDTO =
          arbitrosMap.computeIfAbsent(
              arbitroId,
              id -> {
                LiquidacionArbitroDTO dto = new LiquidacionArbitroDTO();
                dto.setArbitroId(id);
                dto.setNombreCompleto(asignacion.getArbitro().getNombreCompleto());
                dto.setNumeroIdentificacion(asignacion.getArbitro().getNumeroIdentificacion());
                dto.setEscalafon(asignacion.getArbitro().getEscalafon());
                return dto;
              });

      // Crear detalle del partido
      DetallePartidoDTO detallePartido = new DetallePartidoDTO();
      detallePartido.setPartidoId(asignacion.getPartido().getId());
      detallePartido.setNombrePartido(asignacion.getPartido().getNombre());
      detallePartido.setTorneo(
          asignacion.getPartido().getTorneo() != null
              ? asignacion.getPartido().getTorneo().getNombre()
              : "Sin torneo");
      detallePartido.setFechaPartido(asignacion.getPartido().getFechaHora());
      detallePartido.setPosicion(asignacion.getPosicion());
      detallePartido.setMontoPago(
          asignacion.getMontoPago() != null ? asignacion.getMontoPago() : BigDecimal.ZERO);
      detallePartido.setEstado(asignacion.getEstado().name());

      arbitroDTO.getPartidos().add(detallePartido);

      // Actualizar totales del árbitro
      arbitroDTO.setTotalAPagar(arbitroDTO.getTotalAPagar().add(detallePartido.getMontoPago()));
      arbitroDTO.setCantidadPartidos(arbitroDTO.getCantidadPartidos() + 1);
    }

    // Convertir map a lista y calcular totales generales
    List<LiquidacionArbitroDTO> listaArbitros = new ArrayList<>(arbitrosMap.values());
    liquidacion.setArbitros(listaArbitros);

    BigDecimal totalGeneral = BigDecimal.ZERO;

    for (LiquidacionArbitroDTO arbitro : listaArbitros) {
      totalGeneral = totalGeneral.add(arbitro.getTotalAPagar());
    }

    // Contar partidos únicos
    long partidosUnicos = asignaciones.stream().map(a -> a.getPartido().getId()).distinct().count();

    liquidacion.setTotalGeneral(totalGeneral);
    liquidacion.setTotalPartidos((int) partidosUnicos);
    liquidacion.setTotalAsignaciones(asignaciones.size());

    logger.info(
        "Liquidación generada: {} árbitros, {} partidos, ${} total",
        listaArbitros.size(),
        partidosUnicos,
        totalGeneral);

    return liquidacion;
  }

  /**
   * Genera liquidación para un árbitro específico en un período
   *
   * @param arbitroId ID del árbitro
   * @param fechaInicio Fecha de inicio del período
   * @param fechaFin Fecha de fin del período
   * @return DTO con la liquidación del árbitro
   */
  @Transactional(readOnly = true)
  public LiquidacionArbitroDTO generarLiquidacionPorArbitro(
      Long arbitroId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
    logger.info(
        "Generando liquidación para árbitro {} desde {} hasta {}",
        arbitroId,
        fechaInicio,
        fechaFin);

    List<Asignacion> asignaciones =
        asignacionRepository.findByArbitroIdAndPartidoFechaHoraBetweenAndEstadoAndActivo(
            arbitroId, fechaInicio, fechaFin, AsignacionEstado.COMPLETADA, true);

    if (asignaciones.isEmpty()) {
      logger.warn("No se encontraron asignaciones completadas para el árbitro {}", arbitroId);
      return null;
    }

    LiquidacionArbitroDTO arbitroDTO = new LiquidacionArbitroDTO();
    Asignacion primera = asignaciones.get(0);

    arbitroDTO.setArbitroId(arbitroId);
    arbitroDTO.setNombreCompleto(primera.getArbitro().getNombreCompleto());
    arbitroDTO.setNumeroIdentificacion(primera.getArbitro().getNumeroIdentificacion());
    arbitroDTO.setEscalafon(primera.getArbitro().getEscalafon());

    BigDecimal total = BigDecimal.ZERO;

    for (Asignacion asignacion : asignaciones) {
      DetallePartidoDTO detallePartido = new DetallePartidoDTO();
      detallePartido.setPartidoId(asignacion.getPartido().getId());
      detallePartido.setNombrePartido(asignacion.getPartido().getNombre());
      detallePartido.setTorneo(
          asignacion.getPartido().getTorneo() != null
              ? asignacion.getPartido().getTorneo().getNombre()
              : "Sin torneo");
      detallePartido.setFechaPartido(asignacion.getPartido().getFechaHora());
      detallePartido.setPosicion(asignacion.getPosicion());
      detallePartido.setMontoPago(
          asignacion.getMontoPago() != null ? asignacion.getMontoPago() : BigDecimal.ZERO);
      detallePartido.setEstado(asignacion.getEstado().name());

      arbitroDTO.getPartidos().add(detallePartido);
      total = total.add(detallePartido.getMontoPago());
    }

    arbitroDTO.setTotalAPagar(total);
    arbitroDTO.setCantidadPartidos(asignaciones.size());

    logger.info(
        "Liquidación generada para árbitro {}: {} partidos, ${} total",
        arbitroId,
        asignaciones.size(),
        total);

    return arbitroDTO;
  }

  /**
   * Genera liquidación para un árbitro específico por username en un período
   *
   * @param username Username del árbitro
   * @param fechaInicio Fecha de inicio del período
   * @param fechaFin Fecha de fin del período
   * @return DTO con la liquidación del árbitro
   */
  @Transactional(readOnly = true)
  public LiquidacionArbitroDTO generarLiquidacionPorArbitro(
      String username, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
    logger.info(
        "Generando liquidación para árbitro con username {} desde {} hasta {}",
        username,
        fechaInicio,
        fechaFin);

    Arbitro arbitro = arbitroRepository.findByUsername(username);

    if (arbitro == null) {
      logger.error("No se encontró el árbitro con username: {}", username);
      throw new IllegalArgumentException("Árbitro no encontrado");
    }

    return generarLiquidacionPorArbitro(arbitro.getId(), fechaInicio, fechaFin);
  }
}
