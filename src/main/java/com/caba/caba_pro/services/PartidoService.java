/**
 * Archivo: PartidoService.java Autores: Juan José Fecha última modificación: 05.09.2025
 * Descripción: Lógica de negocio para gestión de partidos y asignaciones. Proyecto: CABA Pro -
 * Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.DTOs.AsignacionDto;
import com.caba.caba_pro.DTOs.PartidoDto;
import com.caba.caba_pro.enums.PartidoEstado;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.models.Asignacion;
import com.caba.caba_pro.models.Partido;
import com.caba.caba_pro.repositories.ArbitroRepository;
import com.caba.caba_pro.repositories.AsignacionRepository;
import com.caba.caba_pro.repositories.PartidoRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PartidoService {

  // 1. Constantes estáticas
  private static final Logger logger = LoggerFactory.getLogger(PartidoService.class);

  // 2. Variables de instancia
  private final PartidoRepository partidoRepository;
  private final ArbitroRepository arbitroRepository;
  private final AsignacionRepository asignacionRepository;
  private final TarifaService tarifaService;

  // 3. Constructores
  public PartidoService(
      PartidoRepository partidoRepository,
      ArbitroRepository arbitroRepository,
      AsignacionRepository asignacionRepository,
      TarifaService tarifaService) {
    this.partidoRepository = partidoRepository;
    this.arbitroRepository = arbitroRepository;
    this.asignacionRepository = asignacionRepository;
    this.tarifaService = tarifaService;
  }

  // 4. Métodos públicos

  @Transactional(readOnly = true)
  public List<Partido> buscarActivos() {
    return partidoRepository.findByActivoTrue();
  }

  @Transactional(readOnly = true)
  public Partido buscarPorId(Long id) {
    return partidoRepository
        .findByIdAndActivoTrue(id)
        .orElseThrow(() -> new BusinessException("Partido no encontrado"));
  }

  public Partido crear(PartidoDto dto) {
    validarDatos(dto);

    Partido partido = new Partido();
    partido.setNombre(dto.getNombre());
    partido.setDescripcion(dto.getDescripcion());
    partido.setFechaHora(dto.getFechaHora());
    partido.setSede(dto.getSede());
    partido.setEquipoLocal(dto.getEquipoLocal());
    partido.setEquipoVisitante(dto.getEquipoVisitante());
    partido.setEstado(PartidoEstado.PROGRAMADO);
    partido.setActivo(true);

    Partido guardado = partidoRepository.save(partido);
    logger.info("Partido creado: {} (id={})", guardado.getNombre(), guardado.getId());
    return guardado;
  }

  public Partido actualizar(Long id, PartidoDto dto) {
    Partido partido = buscarPorId(id);
    validarDatos(dto);

    partido.setNombre(dto.getNombre());
    partido.setDescripcion(dto.getDescripcion());
    partido.setFechaHora(dto.getFechaHora());
    partido.setSede(dto.getSede());
    partido.setEquipoLocal(dto.getEquipoLocal());
    partido.setEquipoVisitante(dto.getEquipoVisitante());

    logger.info("Partido actualizado (id={}): {}", id, partido.getNombre());
    return partidoRepository.save(partido);
  }

  public void eliminar(Long id) {
    Partido partido = buscarPorId(id);
    partido.setActivo(false);
    partidoRepository.save(partido);
    logger.warn("Partido eliminado (soft delete) id={}", id);
  }

  public Asignacion asignarArbitro(Long partidoId, AsignacionDto dto) {
    Partido partido = buscarPorId(partidoId);

    if (partido.getEstado() == PartidoEstado.CANCELADO) {
      throw new BusinessException("No es posible asignar árbitros a un partido cancelado");
    }

    Arbitro arbitro =
        arbitroRepository
            .findById(dto.getArbitroId())
            .orElseThrow(() -> new BusinessException("Árbitro no encontrado"));

    if (Boolean.FALSE.equals(arbitro.getActivo())) {
      throw new BusinessException("El árbitro no está activo");
    }

    // Regla previa: no duplicar árbitro ni posición en el mismo partido
    boolean yaAsignado =
        asignacionRepository.existsByPartidoIdAndArbitroIdAndActivoTrue(
            partidoId, dto.getArbitroId());
    if (yaAsignado) {
      throw new BusinessException("El árbitro ya está asignado a este partido");
    }

    boolean posicionOcupada =
        asignacionRepository.existsByPartidoIdAndPosicionAndActivoTrue(
            partidoId, dto.getPosicion());
    if (posicionOcupada) {
      throw new BusinessException("La posición ya está asignada para este partido");
    }

    // ─────────────────────────────────────────────────────────────
    // 1 partido por día por árbitro (realismo operativo)
    var fecha = partido.getFechaHora().toLocalDate();
    var inicio = fecha.atStartOfDay();
    var fin = fecha.plusDays(1).atStartOfDay().minusNanos(1);

    boolean yaTieneOtroEseDia =
        asignacionRepository.existsByArbitroIdAndActivoTrueAndPartido_FechaHoraBetween(
            dto.getArbitroId(), inicio, fin);

    if (yaTieneOtroEseDia) {
      throw new BusinessException("El árbitro ya tiene una asignación para ese día");
    }
    // ─────────────────────────────────────────────────────────────

    // Obtener tarifa por escalafón del árbitro y dejarla registrada en la asignación
    java.math.BigDecimal monto = tarifaService.obtenerMontoPorEscalafon(arbitro.getEscalafon());

    Asignacion asignacion = new Asignacion();
    asignacion.setPartido(partido);
    asignacion.setArbitro(arbitro);
    asignacion.setPosicion(dto.getPosicion());
    asignacion.setMontoPago(monto); // ← AQUÍ aplicamos la tarifa

    Asignacion guardada = asignacionRepository.save(asignacion);
    logger.info(
        "Asignación creada: partido={} árbitro={} posición={} monto={}",
        partido.getId(),
        arbitro.getId(),
        dto.getPosicion(),
        monto);
    return guardada;
  }

  // 5. Métodos privados

  private void validarDatos(PartidoDto dto) {
    // Por ahora no hay reglas adicionales aparte del DTO.
    // Aquí es el lugar para reglas de negocio cruzadas (p.ej., equipos distintos).
    if (dto.getEquipoLocal().equalsIgnoreCase(dto.getEquipoVisitante())) {
      throw new BusinessException("Los equipos deben ser distintos");
    }
  }
}
