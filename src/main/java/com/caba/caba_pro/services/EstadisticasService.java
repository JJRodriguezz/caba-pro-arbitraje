/**
 * Archivo: EstadisticasService.java Autores: JJRodriguezz Fecha última modificación: 07.09.2025
 * Descripción: Servicio para cálculo de estadísticas de desempeño de árbitros. Proyecto: CABA Pro -
 * Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.DTOs.EstadisticasArbitrosDto;
import com.caba.caba_pro.DTOs.EstadisticasAsignacionesDto;
import com.caba.caba_pro.DTOs.TopArbitrosDto;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.models.Asignacion;
import com.caba.caba_pro.repositories.ArbitroRepository;
import com.caba.caba_pro.repositories.AsignacionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EstadisticasService {

  private final ArbitroRepository arbitroRepository;
  private final AsignacionRepository asignacionRepository;

  @Autowired
  public EstadisticasService(
      ArbitroRepository arbitroRepository, AsignacionRepository asignacionRepository) {
    this.arbitroRepository = arbitroRepository;
    this.asignacionRepository = asignacionRepository;
  }

  // Estadísticas generales de árbitros
  public EstadisticasArbitrosDto obtenerEstadisticasArbitros(LocalDate desde, LocalDate hasta) {
    List<Arbitro> activos = arbitroRepository.findByActivoTrue();
    EstadisticasArbitrosDto dto = new EstadisticasArbitrosDto();
    dto.setTotalActivos(activos.size());

    // Por especialidad
    Map<String, Integer> especialidadMap = new HashMap<>();
    Map<String, Integer> escalafonMap = new HashMap<>();
    for (Arbitro a : activos) {
      String esp = a.getEspecialidad() != null ? a.getEspecialidad().name() : "Sin especialidad";
      especialidadMap.put(esp, especialidadMap.getOrDefault(esp, 0) + 1);
      String esc = a.getEscalafon() != null ? a.getEscalafon() : "Sin escalafón";
      escalafonMap.put(esc, escalafonMap.getOrDefault(esc, 0) + 1);
    }
    dto.setCantidadPorEspecialidad(especialidadMap);
    dto.setCantidadPorEscalafon(escalafonMap);
    return dto;
  }

  // Estadísticas de asignaciones aceptadas/rechazadas
  public EstadisticasAsignacionesDto obtenerEstadisticasAsignaciones(
      LocalDate desde, LocalDate hasta) {
    List<Asignacion> asignaciones = asignacionRepository.findAll();
    if (desde != null && hasta != null) {
      LocalDateTime inicio = desde.atStartOfDay();
      LocalDateTime fin = hasta.atTime(23, 59, 59);
      asignaciones = filtrarPorFecha(asignaciones, inicio, fin);
    }
    int total = asignaciones.size();
    int aceptadas = 0;
    int rechazadas = 0;
    for (Asignacion a : asignaciones) {
      if (a.isAceptada()) {
        aceptadas++;
      } else {
        rechazadas++;
      }
    }
    EstadisticasAsignacionesDto dto = new EstadisticasAsignacionesDto();
    dto.setTotalAsignaciones(total);
    dto.setAceptadas(aceptadas);
    dto.setRechazadas(rechazadas);
    dto.setPorcentajeAceptadas(total > 0 ? (aceptadas * 100.0 / total) : 0);
    dto.setPorcentajeRechazadas(total > 0 ? (rechazadas * 100.0 / total) : 0);
    return dto;
  }

  // Top 5 árbitros más activos del mes
  public TopArbitrosDto obtenerTopArbitros(LocalDate desde, LocalDate hasta) {
    List<Asignacion> asignaciones = asignacionRepository.findAll();
    LocalDateTime inicio;
    LocalDateTime fin;
    if (desde != null && hasta != null) {
      inicio = desde.atStartOfDay();
      fin = hasta.atTime(23, 59, 59);
    } else {
      YearMonth mesActual = YearMonth.now();
      inicio = mesActual.atDay(1).atStartOfDay();
      fin = mesActual.atEndOfMonth().atTime(23, 59, 59);
    }
    asignaciones = filtrarPorFecha(asignaciones, inicio, fin);
    // Solo contar asignaciones aceptadas
    Map<Long, Integer> conteoPorArbitro = new HashMap<>();
    Map<Long, String> nombres = new HashMap<>();
    for (Asignacion a : asignaciones) {
      if (a.isAceptada()) {
        Long id = a.getArbitro().getId();
        conteoPorArbitro.put(id, conteoPorArbitro.getOrDefault(id, 0) + 1);
        nombres.put(id, a.getArbitro().getNombreCompleto());
      }
    }
    List<TopArbitrosDto.ArbitroActividadDto> lista = new ArrayList<>();
    for (Map.Entry<Long, Integer> entry : conteoPorArbitro.entrySet()) {
      TopArbitrosDto.ArbitroActividadDto dto = new TopArbitrosDto.ArbitroActividadDto();
      dto.setIdArbitro(entry.getKey());
      dto.setNombreCompleto(nombres.get(entry.getKey()));
      dto.setCantidadAsignaciones(entry.getValue());
      lista.add(dto);
    }
    lista.sort((a, b) -> Integer.compare(b.getCantidadAsignaciones(), a.getCantidadAsignaciones()));
    TopArbitrosDto topDto = new TopArbitrosDto();
    topDto.setTopArbitros(lista.size() > 5 ? lista.subList(0, 5) : lista);
    return topDto;
  }

  // Utilidad para filtrar asignaciones por fecha
  private List<Asignacion> filtrarPorFecha(
      List<Asignacion> asignaciones, LocalDateTime inicio, LocalDateTime fin) {
    List<Asignacion> filtradas = new ArrayList<>();
    for (Asignacion a : asignaciones) {
      if (a.getPartido().getFechaHora().isAfter(inicio.minusSeconds(1))
          && a.getPartido().getFechaHora().isBefore(fin.plusSeconds(1))) {
        filtradas.add(a);
      }
    }
    return filtradas;
  }
}
