/**
 * Archivo: ArbitroServiceTest.java Autores: JJRodriguezz Fecha última modificación: 11.11.2025
 * Descripción: Pruebas unitarias para ArbitroService Proyecto: CABA Pro - Sistema de Gestión
 * Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.caba.caba_pro.DTOs.ArbitroDto;
import com.caba.caba_pro.enums.Especialidad;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.repositories.AdministradorRepository;
import com.caba.caba_pro.repositories.ArbitroRepository;
import java.time.LocalDate;
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
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - ArbitroService")
class ArbitroServiceTest {

  @Mock private ArbitroRepository arbitroRepository;

  @Mock private AdministradorRepository administradorRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private FileUploadService fileUploadService;

  @InjectMocks private ArbitroService arbitroService;

  private Arbitro arbitroMock;
  private ArbitroDto arbitroDtoMock;

  @BeforeEach
  void setUp() {
    // Configurar árbitro mock
    arbitroMock = new Arbitro();
    arbitroMock.setId(1L);
    arbitroMock.setNombre("Juan");
    arbitroMock.setApellidos("Pérez");
    arbitroMock.setUsername("juan.perez");
    arbitroMock.setEmail("juan.perez@example.com");
    arbitroMock.setNumeroIdentificacion("123456789");
    arbitroMock.setTelefono("3001234567");
    arbitroMock.setEspecialidad(Especialidad.CAMPO);
    arbitroMock.setEscalafon("A");
    arbitroMock.setFechaNacimiento(LocalDate.of(1990, 1, 1));
    arbitroMock.setActivo(true);
    arbitroMock.setPassword("password123");

    // Configurar DTO mock
    arbitroDtoMock = new ArbitroDto();
    arbitroDtoMock.setNombre("Juan");
    arbitroDtoMock.setApellidos("Pérez");
    arbitroDtoMock.setUsername("juan.perez");
    arbitroDtoMock.setEmail("juan.perez@example.com");
    arbitroDtoMock.setNumeroIdentificacion("123456789");
    arbitroDtoMock.setTelefono("3001234567");
    arbitroDtoMock.setEspecialidad(Especialidad.CAMPO);
    arbitroDtoMock.setEscalafon("A");
    arbitroDtoMock.setFechaNacimiento(LocalDate.of(1990, 1, 1));
    arbitroDtoMock.setPassword("password123");
  }

  @Test
  @DisplayName("Debe buscar árbitro por ID exitosamente")
  void testBuscarPorId_Exitoso() {
    // Arrange
    when(arbitroRepository.findById(1L)).thenReturn(Optional.of(arbitroMock));

    // Act
    Arbitro resultado = arbitroService.buscarPorId(1L);

    // Assert
    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
    assertEquals("Juan", resultado.getNombre());
    assertEquals("Pérez", resultado.getApellidos());
    assertEquals("juan.perez", resultado.getUsername());

    verify(arbitroRepository, times(1)).findById(1L);
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando árbitro no existe")
  void testBuscarPorId_NoExiste() {
    // Arrange
    when(arbitroRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    BusinessException exception =
        assertThrows(BusinessException.class, () -> arbitroService.buscarPorId(999L));

    assertEquals("Árbitro no encontrado con ID: 999", exception.getMessage());
    verify(arbitroRepository, times(1)).findById(999L);
  }

  @Test
  @DisplayName("Debe buscar árbitro por username exitosamente")
  void testBuscarPorUsername_Exitoso() {
    // Arrange
    when(arbitroRepository.findByUsername("juan.perez")).thenReturn(arbitroMock);

    // Act
    Arbitro resultado = arbitroService.buscarPorUsername("juan.perez");

    // Assert
    assertNotNull(resultado);
    assertEquals("juan.perez", resultado.getUsername());
    assertEquals("Juan", resultado.getNombre());
    assertTrue(resultado.getActivo());

    verify(arbitroRepository, times(1)).findByUsername("juan.perez");
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando árbitro por username no existe")
  void testBuscarPorUsername_NoExiste() {
    // Arrange
    when(arbitroRepository.findByUsername("inexistente")).thenReturn(null);

    // Act & Assert
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> arbitroService.buscarPorUsername("inexistente"));

    assertEquals("Árbitro no encontrado o inactivo", exception.getMessage());
  }

  @Test
  @DisplayName("Debe listar todos los árbitros activos")
  void testBuscarTodosActivos() {
    // Arrange
    Arbitro arbitro2 = new Arbitro();
    arbitro2.setId(2L);
    arbitro2.setNombre("María");
    arbitro2.setActivo(true);

    List<Arbitro> arbitrosActivos = Arrays.asList(arbitroMock, arbitro2);
    when(arbitroRepository.findByActivoTrue()).thenReturn(arbitrosActivos);

    // Act
    List<Arbitro> resultado = arbitroService.buscarTodosActivos();

    // Assert
    assertNotNull(resultado);
    assertEquals(2, resultado.size());
    assertTrue(resultado.stream().allMatch(Arbitro::getActivo));

    verify(arbitroRepository, times(1)).findByActivoTrue();
  }

  @Test
  @DisplayName("Debe crear árbitro exitosamente sin foto de perfil")
  void testCrearArbitro_SinFoto_Exitoso() {
    // Arrange
    when(arbitroRepository.existsByNumeroIdentificacion(anyString())).thenReturn(false);
    when(arbitroRepository.existsByEmailAndActivoTrue(anyString())).thenReturn(false);
    when(arbitroRepository.existsByUsernameAndActivoTrue(anyString())).thenReturn(false);
    when(administradorRepository.existsByUsername(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    when(arbitroRepository.save(any(Arbitro.class))).thenReturn(arbitroMock);

    // Act
    Arbitro resultado = arbitroService.crearArbitro(arbitroDtoMock, null);

    // Assert
    assertNotNull(resultado);
    verify(arbitroRepository, times(1)).existsByNumeroIdentificacion("123456789");
    verify(arbitroRepository, times(1)).existsByEmailAndActivoTrue("juan.perez@example.com");
    verify(arbitroRepository, times(1)).existsByUsernameAndActivoTrue("juan.perez");
    verify(passwordEncoder, times(1)).encode("password123");
    verify(arbitroRepository, times(1)).save(any(Arbitro.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando email ya existe")
  void testCrearArbitro_EmailDuplicado() {
    // Arrange
    when(arbitroRepository.existsByNumeroIdentificacion(anyString())).thenReturn(false);
    when(arbitroRepository.existsByEmailAndActivoTrue("juan.perez@example.com")).thenReturn(true);

    // Act & Assert
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> arbitroService.crearArbitro(arbitroDtoMock, null));

    assertEquals("Ya existe un árbitro con ese email", exception.getMessage());
    verify(arbitroRepository, never()).save(any(Arbitro.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando username ya existe")
  void testCrearArbitro_UsernameDuplicado() {
    // Arrange
    when(arbitroRepository.existsByNumeroIdentificacion(anyString())).thenReturn(false);
    when(arbitroRepository.existsByEmailAndActivoTrue(anyString())).thenReturn(false);
    when(arbitroRepository.existsByUsernameAndActivoTrue("juan.perez")).thenReturn(true);

    // Act & Assert
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> arbitroService.crearArbitro(arbitroDtoMock, null));

    assertEquals("Ya existe un árbitro con ese nombre de usuario", exception.getMessage());
    verify(arbitroRepository, never()).save(any(Arbitro.class));
  }

  @Test
  @DisplayName("Debe eliminar árbitro (soft delete)")
  void testEliminarArbitro() {
    // Arrange
    when(arbitroRepository.findById(1L)).thenReturn(Optional.of(arbitroMock));
    when(arbitroRepository.save(any(Arbitro.class))).thenReturn(arbitroMock);

    // Act
    arbitroService.eliminarArbitro(1L);

    // Assert
    verify(arbitroRepository, times(1)).findById(1L);
    verify(arbitroRepository, times(1)).save(arbitroMock);
    assertFalse(arbitroMock.getActivo());
  }
}
