/**
 * Archivo: CalendarioService.java Autores: Sistema CABA Pro Fecha última modificación: 07.09.2025
 * Descripción: Servicio para generar eventos del calendario según el rol del usuario Proyecto: CABA
 * Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.DTOs.CalendarioDto;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.models.Partido;
import com.caba.caba_pro.repositories.ArbitroRepository;
import com.caba.caba_pro.repositories.PartidoRepository;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalendarioService {

  @Autowired private PartidoRepository partidoRepository;

  @Autowired private ArbitroRepository arbitroRepository;

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

  /** Obtiene todos los eventos para el administrador */
  public List<CalendarioDto> obtenerEventosAdmin() {
    List<Partido> partidos = partidoRepository.findByActivoTrue();
    return partidos.stream().map(this::convertirPartidoAEvento).collect(Collectors.toList());
  }

  /** Obtiene solo los eventos asignados a un árbitro específico */
  public List<CalendarioDto> obtenerEventosArbitro(String username) {
    // Buscar árbitro por username
    Arbitro arbitro = arbitroRepository.findByUsername(username);
    if (arbitro == null) {
      throw new RuntimeException("Árbitro no encontrado: " + username);
    }

    // Obtener todos los partidos y filtrar por asignaciones del árbitro
    List<Partido> todosLosPartidos = partidoRepository.findByActivoTrue();
    List<Partido> partidosAsignados =
        todosLosPartidos.stream()
            .filter(
                partido ->
                    partido.getAsignaciones().stream()
                        .anyMatch(
                            asignacion -> asignacion.getArbitro().getId().equals(arbitro.getId())))
            .collect(Collectors.toList());

    return partidosAsignados.stream()
        .map(this::convertirPartidoAEvento)
        .collect(Collectors.toList());
  }

  /** Convierte un Partido en CalendarioDto para FullCalendar */
  private CalendarioDto convertirPartidoAEvento(Partido partido) {
    String id = "partido_" + partido.getId();
    String title = partido.getEquipoLocal() + " vs " + partido.getEquipoVisitante();
    String start = partido.getFechaHora().format(FORMATTER);
    String end =
        partido.getFechaHora().plusHours(2).format(FORMATTER); // Asumimos 2 horas de duración

    // Color fijo para todos los partidos
    String color = "#4ea1a5";

    // Propiedades extendidas para FullCalendar
    Map<String, Object> extendedProps = new HashMap<>();
    extendedProps.put("tipo", "PARTIDO");
    extendedProps.put("sede", partido.getSede());
    extendedProps.put("estado", partido.getEstado().toString());
    extendedProps.put("descripcion", partido.getDescripcion());

    if (partido.getTorneo() != null) {
      extendedProps.put("torneo", partido.getTorneo().getNombre());
      extendedProps.put("torneoId", partido.getTorneo().getId());
    }

    return new CalendarioDto(id, title, start, end, color, extendedProps);
  }

  /** Obtiene eventos filtrados por fecha y torneo */
  public List<CalendarioDto> obtenerEventosConFiltros(
      String username, String rol, String fechaInicio, String fechaFin, Long torneoId) {
    List<CalendarioDto> eventos;

    // Obtener eventos según el rol
    if ("ADMIN".equals(rol)) {
      eventos = obtenerEventosAdmin();
    } else {
      eventos = obtenerEventosArbitro(username);
    }

    // Aplicar filtros si se proporcionan
    return eventos.stream()
        .filter(evento -> filtrarPorFecha(evento, fechaInicio, fechaFin))
        .filter(evento -> filtrarPorTorneo(evento, torneoId))
        .collect(Collectors.toList());
  }

  private boolean filtrarPorFecha(CalendarioDto evento, String fechaInicio, String fechaFin) {
    if ((fechaInicio == null || fechaInicio.isBlank())
        && (fechaFin == null || fechaFin.isBlank())) {
      return true;
    }

    try {
      // Inputs: yyyy-MM-dd
      java.time.LocalDate inicio =
          (fechaInicio == null || fechaInicio.isBlank())
              ? null
              : java.time.LocalDate.parse(fechaInicio);
      java.time.LocalDate fin =
          (fechaFin == null || fechaFin.isBlank()) ? null : java.time.LocalDate.parse(fechaFin);

      // Evento.start: yyyy-MM-dd'T'HH:mm:ss
      String start = evento.getStart();
      java.time.LocalDate fechaEvento =
          java.time.LocalDate.parse(start.substring(0, Math.min(start.length(), 10)));

      return ((inicio == null) || !fechaEvento.isBefore(inicio))
          && ((fin == null) || !fechaEvento.isAfter(fin));
    } catch (Exception e) {
      // Si hay cualquier problema de parseo, no filtrar ese evento
      return true;
    }
  }

  private boolean filtrarPorTorneo(CalendarioDto evento, Long torneoId) {
    if (torneoId == null) return true;

    Map<String, Object> props = evento.getExtendedProps();
    if (props != null && props.containsKey("torneoId")) {
      Object valor = props.get("torneoId");
      if (valor == null) return false;
      if (valor instanceof Number n) {
        return torneoId.equals(n.longValue());
      }
      if (valor instanceof String s) {
        try {
          return torneoId.equals(Long.valueOf(s));
        } catch (NumberFormatException ignored) {
          return false;
        }
      }
      return false;
    }
    return false;
  }
}
