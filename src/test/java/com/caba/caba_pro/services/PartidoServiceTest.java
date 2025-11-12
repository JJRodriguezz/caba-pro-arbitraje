/**
 * Archivo: PartidoServiceTest.java Autores: JJrodriguezz Fecha última modificación: 11.11.2025
 * Descripción: Pruebas unitarias para PartidoService Proyecto: CABA Pro - Sistema de Gestión
 * Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.caba.caba_pro.DTOs.AsignacionDto;
import com.caba.caba_pro.DTOs.PartidoDto;
import com.caba.caba_pro.enums.Especialidad;
import com.caba.caba_pro.enums.PartidoEstado;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.models.Asignacion;
import com.caba.caba_pro.models.Partido;
import com.caba.caba_pro.models.Torneo;
import com.caba.caba_pro.repositories.AdministradorRepository;
import com.caba.caba_pro.repositories.ArbitroRepository;
import com.caba.caba_pro.repositories.AsignacionRepository;
import com.caba.caba_pro.repositories.PartidoRepository;
import com.caba.caba_pro.repositories.TorneoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - PartidoService")
class PartidoServiceTest {

  @Mock private PartidoRepository partidoRepository;

  @Mock private ArbitroRepository arbitroRepository;

  @Mock private AsignacionRepository asignacionRepository;

  @Mock private TarifaService tarifaService;

  @Mock private TorneoRepository torneoRepository;

  @Mock private NotificacionService notificacionService;

  @Mock private DisponibilidadService disponibilidadService;

  @Mock private AdministradorRepository administradorRepository;

  @InjectMocks private PartidoService partidoService;

  private Partido partidoMock;
  private PartidoDto partidoDtoMock;
  private Arbitro arbitroMock;
  private Torneo torneoMock;

  @BeforeEach
  void setUp() {
    // Configurar partido mock
    partidoMock = new Partido();
    partidoMock.setNombre("Final Liga");
    partidoMock.setDescripcion("Partido final de liga");
    partidoMock.setFechaHora(LocalDateTime.now().plusDays(7));
    partidoMock.setSede("Estadio Principal");
    partidoMock.setEquipoLocal("Equipo A");
    partidoMock.setEquipoVisitante("Equipo B");
    partidoMock.setEstado(PartidoEstado.PROGRAMADO);
    partidoMock.setActivo(true);

    // Configurar DTO mock
    partidoDtoMock = new PartidoDto();
    partidoDtoMock.setNombre("Final Liga");
    partidoDtoMock.setDescripcion("Partido final de liga");
    partidoDtoMock.setFechaHora(LocalDateTime.now().plusDays(7));
    partidoDtoMock.setSede("Estadio Principal");
    partidoDtoMock.setEquipoLocal("Equipo A");
    partidoDtoMock.setEquipoVisitante("Equipo B");

    // Configurar árbitro mock
    arbitroMock = new Arbitro();
    arbitroMock.setId(1L);
    arbitroMock.setNombre("Juan");
    arbitroMock.setApellidos("Pérez");
    arbitroMock.setUsername("juan.perez");
    arbitroMock.setEspecialidad(Especialidad.CAMPO);
    arbitroMock.setEscalafon("A");
    arbitroMock.setActivo(true);

    // Configurar torneo mock
    torneoMock = new Torneo();
    torneoMock.setNombre("Liga Nacional");
    torneoMock.setActivo(true);
  }

  @Test
  @DisplayName("Debe buscar partido por ID exitosamente")
  void testBuscarPorId_Exitoso() {
    // Arrange
    when(partidoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(partidoMock));

    // Act
    Partido resultado = partidoService.buscarPorId(1L);

    // Assert
    assertNotNull(resultado);
    assertEquals("Final Liga", resultado.getNombre());
    assertEquals("Estadio Principal", resultado.getSede());
    assertTrue(resultado.getActivo());

    verify(partidoRepository, times(1)).findByIdAndActivoTrue(1L);
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando partido no existe")
  void testBuscarPorId_NoExiste() {
    // Arrange
    when(partidoRepository.findByIdAndActivoTrue(999L)).thenReturn(Optional.empty());

    // Act & Assert
    BusinessException exception =
        assertThrows(BusinessException.class, () -> partidoService.buscarPorId(999L));

    assertEquals("Partido no encontrado", exception.getMessage());
    verify(partidoRepository, times(1)).findByIdAndActivoTrue(999L);
  }

  @Test
  @DisplayName("Debe listar todos los partidos activos")
  void testBuscarActivos() {
    // Arrange
    Partido partido2 = new Partido();
    partido2.setNombre("Semifinal");
    partido2.setActivo(true);

    List<Partido> partidosActivos = Arrays.asList(partidoMock, partido2);
    when(partidoRepository.findByActivoTrue()).thenReturn(partidosActivos);

    // Act
    List<Partido> resultado = partidoService.buscarActivos();

    // Assert
    assertNotNull(resultado);
    assertEquals(2, resultado.size());
    assertTrue(resultado.stream().allMatch(Partido::getActivo));

    verify(partidoRepository, times(1)).findByActivoTrue();
  }

  @Test
  @DisplayName("Debe crear partido exitosamente sin torneo")
  void testCrear_SinTorneo_Exitoso() {
    // Arrange
    when(partidoRepository.save(any(Partido.class))).thenReturn(partidoMock);

    // Act
    Partido resultado = partidoService.crear(partidoDtoMock);

    // Assert
    assertNotNull(resultado);
    verify(partidoRepository, times(1)).save(any(Partido.class));
  }

  @Test
  @DisplayName("Debe crear partido exitosamente con torneo")
  void testCrear_ConTorneo_Exitoso() {
    // Arrange
    partidoDtoMock.setTorneoId(1L);
    when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneoMock));
    when(partidoRepository.save(any(Partido.class))).thenReturn(partidoMock);

    // Act
    Partido resultado = partidoService.crear(partidoDtoMock);

    // Assert
    assertNotNull(resultado);
    verify(torneoRepository, times(1)).findById(1L);
    verify(partidoRepository, times(1)).save(any(Partido.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando equipos son iguales")
  void testCrear_EquiposIguales() {
    // Arrange
    partidoDtoMock.setEquipoLocal("Equipo A");
    partidoDtoMock.setEquipoVisitante("Equipo A");

    // Act & Assert
    BusinessException exception =
        assertThrows(BusinessException.class, () -> partidoService.crear(partidoDtoMock));

    assertEquals("Los equipos deben ser distintos", exception.getMessage());
    verify(partidoRepository, never()).save(any(Partido.class));
  }

  @Test
  @DisplayName("Debe asignar árbitro exitosamente")
  void testAsignarArbitro_Exitoso() {
    // Arrange
    AsignacionDto asignacionDto = new AsignacionDto();
    asignacionDto.setArbitroId(1L);
    asignacionDto.setPosicion("Principal");
    asignacionDto.setAdminUsername("admin");

    when(partidoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(partidoMock));
    when(arbitroRepository.findById(1L)).thenReturn(Optional.of(arbitroMock));
    when(disponibilidadService.esArbitroDisponibleEnFechaHora(anyString(), any())).thenReturn(true);
    when(asignacionRepository.existsByPartidoIdAndArbitroIdAndActivoTrue(anyLong(), anyLong()))
        .thenReturn(false);
    when(asignacionRepository.existsByPartidoIdAndPosicionAndActivoTrue(anyLong(), anyString()))
        .thenReturn(false);
    when(asignacionRepository.existsByArbitroIdAndActivoTrueAndPartido_FechaHoraBetween(
            anyLong(), any(), any()))
        .thenReturn(false);
    when(tarifaService.obtenerMontoPorEscalafon(anyString())).thenReturn(new BigDecimal("150000"));
    when(asignacionRepository.save(any(Asignacion.class))).thenReturn(new Asignacion());

    // Act
    Asignacion resultado = partidoService.asignarArbitro(1L, asignacionDto);

    // Assert
    assertNotNull(resultado);
    verify(asignacionRepository, times(1)).save(any(Asignacion.class));
    verify(notificacionService, times(1)).crearNotificacion(any());
  }

  @Test
  @DisplayName("Debe lanzar excepción al asignar árbitro a partido cancelado")
  void testAsignarArbitro_PartidoCancelado() {
    // Arrange
    partidoMock.setEstado(PartidoEstado.CANCELADO);
    AsignacionDto asignacionDto = new AsignacionDto();
    asignacionDto.setArbitroId(1L);

    when(partidoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(partidoMock));

    // Act & Assert
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> partidoService.asignarArbitro(1L, asignacionDto));

    assertEquals("No es posible asignar árbitros a un partido cancelado", exception.getMessage());
    verify(asignacionRepository, never()).save(any(Asignacion.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando árbitro no está disponible")
  void testAsignarArbitro_ArbitroNoDisponible() {
    // Arrange
    AsignacionDto asignacionDto = new AsignacionDto();
    asignacionDto.setArbitroId(1L);
    asignacionDto.setPosicion("Principal");

    when(partidoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(partidoMock));
    when(arbitroRepository.findById(1L)).thenReturn(Optional.of(arbitroMock));
    when(disponibilidadService.esArbitroDisponibleEnFechaHora(anyString(), any()))
        .thenReturn(false);

    // Act & Assert
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> partidoService.asignarArbitro(1L, asignacionDto));

    assertEquals("El árbitro no está disponible en el horario del partido", exception.getMessage());
    verify(asignacionRepository, never()).save(any(Asignacion.class));
  }

  @Test
  @DisplayName("Debe eliminar partido (soft delete)")
  void testEliminar() {
    // Arrange
    when(partidoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(partidoMock));
    when(partidoRepository.save(any(Partido.class))).thenReturn(partidoMock);

    // Act
    partidoService.eliminar(1L);

    // Assert
    verify(partidoRepository, times(1)).findByIdAndActivoTrue(1L);
    verify(partidoRepository, times(1)).save(partidoMock);
    assertFalse(partidoMock.getActivo());
  }
}
