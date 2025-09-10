/**
 * Archivo: PerfilController.java Autores: Isabella.Idarraga Fecha última modificación: [06.09.2025]
 * Descripción: Controlador para la gestión de perfiles de usuarios en la aplicación Proyecto: CABA
 * Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import com.caba.caba_pro.DTOs.CambiarPasswordDto;
import com.caba.caba_pro.DTOs.EditarPerfilDto;
import com.caba.caba_pro.DTOs.PerfilDto;
import com.caba.caba_pro.enums.Especialidad;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Administrador;
import com.caba.caba_pro.models.Arbitro;
import com.caba.caba_pro.repositories.AdministradorRepository;
import com.caba.caba_pro.repositories.ArbitroRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilController {

  // 1. Constantes estáticas
  private static final Logger logger = LoggerFactory.getLogger(PerfilController.class);

  // 2. Variables de instancia
  private final AdministradorRepository administradorRepository;
  private final ArbitroRepository arbitroRepository;
  private final PasswordEncoder passwordEncoder;

  // 3. Constructores
  public PerfilController(
      AdministradorRepository administradorRepository,
      ArbitroRepository arbitroRepository,
      PasswordEncoder passwordEncoder) {
    this.administradorRepository = administradorRepository;
    this.arbitroRepository = arbitroRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // 4. Métodos públicos

  @GetMapping("/admin/perfil")
  public String mostrarPerfilAdmin(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    logger.info("Mostrando perfil de administrador: {}", username);

    try {
      Administrador administrador = administradorRepository.findByUsername(username);
      if (administrador == null || !administrador.isActivo()) {
        throw new BusinessException("Administrador no encontrado");
      }

      PerfilDto perfilDto = mapearAdministradorADto(administrador);
      model.addAttribute("perfil", perfilDto);
      model.addAttribute("esAdmin", true);

      return "perfil";

    } catch (Exception e) {
      logger.error("Error al cargar perfil de administrador: {}", e.getMessage());
      model.addAttribute("error", "Error al cargar el perfil");
      return "redirect:/admin/dashboard";
    }
  }

  @GetMapping("/arbitro/perfil")
  public String mostrarPerfilArbitro(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    logger.info("Mostrando perfil de árbitro: {}", username);

    try {
      Arbitro arbitro = arbitroRepository.findByUsername(username);
      if (arbitro == null || !arbitro.isActivo()) {
        throw new BusinessException("Árbitro no encontrado");
      }

      PerfilDto perfilDto = mapearArbitroADto(arbitro);
      model.addAttribute("perfil", perfilDto);
      model.addAttribute("esAdmin", false);

      return "perfil";

    } catch (Exception e) {
      logger.error("Error al cargar perfil de árbitro: {}", e.getMessage());
      model.addAttribute("error", "Error al cargar el perfil");
      return "redirect:/arbitro/dashboard";
    }
  }

  @GetMapping("/admin/perfil/editar")
  public String mostrarFormularioEditarAdmin(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    try {
      Administrador administrador = administradorRepository.findByUsername(username);
      if (administrador == null || !administrador.isActivo()) {
        throw new BusinessException("Administrador no encontrado");
      }

      EditarPerfilDto editarDto = mapearAdministradorAEditarDto(administrador);
      model.addAttribute("editarPerfil", editarDto);
      model.addAttribute("esAdmin", true);

      return "editar-perfil";

    } catch (Exception e) {
      logger.error("Error al cargar formulario de edición de administrador: {}", e.getMessage());
      return "redirect:/admin/perfil";
    }
  }

  @GetMapping("/arbitro/perfil/editar")
  public String mostrarFormularioEditarArbitro(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    try {
      Arbitro arbitro = arbitroRepository.findByUsername(username);
      if (arbitro == null || !arbitro.isActivo()) {
        throw new BusinessException("Árbitro no encontrado");
      }

      EditarPerfilDto editarDto = mapearArbitroAEditarDto(arbitro);
      model.addAttribute("editarPerfil", editarDto);
      model.addAttribute("especialidades", Especialidad.values());
      model.addAttribute("esAdmin", false);

      return "editar-perfil";

    } catch (Exception e) {
      logger.error("Error al cargar formulario de edición de árbitro: {}", e.getMessage());
      return "redirect:/arbitro/perfil";
    }
  }

  @PostMapping("/admin/perfil/editar")
  public String actualizarPerfilAdmin(
      @Valid @ModelAttribute("editarPerfil") EditarPerfilDto editarDto,
      BindingResult result,
      RedirectAttributes flash,
      HttpServletRequest request,
      @RequestParam(value = "fotoPerfil", required = false) MultipartFile fotoPerfil) {

    if (result.hasErrors()) {
      return "editar-perfil";
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    try {
      Administrador administrador = administradorRepository.findByUsername(username);
      if (administrador == null) {
        throw new BusinessException("Administrador no encontrado");
      }

      boolean usernameChanged = validarYActualizarUsername(editarDto, administrador, flash);
      if (flash.containsAttribute("error")) {
        return "redirect:/admin/perfil/editar";
      }

      administrador.setActivo(editarDto.getActivo());

      // Procesar foto de perfil si se envió
      procesarFotoPerfil(
          fotoPerfil, administrador.getUrlFotoPerfil(), url -> administrador.setUrlFotoPerfil(url));

      administradorRepository.save(administrador);

      // Si cambió el username, invalidar sesión para forzar nuevo login
      if (usernameChanged) {
        return invalidarSesionYRedireccionar(request, flash);
      }

      flash.addFlashAttribute("success", "Perfil actualizado exitosamente");
      return "redirect:/admin/perfil";

    } catch (Exception e) {
      logger.error("Error al actualizar perfil de administrador: {}", e.getMessage());
      flash.addFlashAttribute("error", "Error al actualizar el perfil");
      return "redirect:/admin/perfil";
    }
  }

  @PostMapping("/arbitro/perfil/editar")
  public String actualizarPerfilArbitro(
      @ModelAttribute("editarPerfil") EditarPerfilDto editarDto,
      BindingResult result,
      RedirectAttributes flash,
      Model model,
      HttpServletRequest request,
      @RequestParam(value = "fotoPerfil", required = false) MultipartFile fotoPerfil) {

    // Validación manual para edición de árbitros
    boolean hasValidationErrors = false;

    // Validar nombre si no está vacío
    if (editarDto.getNombre() != null && !editarDto.getNombre().trim().isEmpty()) {
      if (editarDto.getNombre().trim().length() < 2 || editarDto.getNombre().trim().length() > 50) {
        result.rejectValue(
            "nombre", "error.nombre", "El nombre debe tener entre 2 y 50 caracteres");
        hasValidationErrors = true;
      }
    }

    // Validar apellidos si no están vacíos
    if (editarDto.getApellidos() != null && !editarDto.getApellidos().trim().isEmpty()) {
      if (editarDto.getApellidos().trim().length() < 2
          || editarDto.getApellidos().trim().length() > 100) {
        result.rejectValue(
            "apellidos", "error.apellidos", "Los apellidos deben tener entre 2 y 100 caracteres");
        hasValidationErrors = true;
      }
    }

    // Validar email si no está vacío
    if (editarDto.getEmail() != null && !editarDto.getEmail().trim().isEmpty()) {
      String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
      if (!editarDto.getEmail().matches(emailPattern)) {
        result.rejectValue("email", "error.email", "El email debe tener un formato válido");
        hasValidationErrors = true;
      }
    }

    if (hasValidationErrors) {
      model.addAttribute("especialidades", Especialidad.values());
      model.addAttribute("esAdmin", false);
      return "editar-perfil";
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    try {
      Arbitro arbitro = arbitroRepository.findByUsername(username);
      if (arbitro == null) {
        throw new BusinessException("Árbitro no encontrado");
      }

      // Verificar y actualizar email si cambió
      if (!validarYActualizarEmailArbitro(editarDto, arbitro, model)) {
        model.addAttribute("especialidades", Especialidad.values());
        model.addAttribute("esAdmin", false);
        return "editar-perfil";
      }

      // Actualizar campos del árbitro
      actualizarCamposArbitro(editarDto, arbitro);

      // Procesar foto de perfil si se envió
      procesarFotoPerfil(
          fotoPerfil, arbitro.getUrlFotoPerfil(), url -> arbitro.setUrlFotoPerfil(url));

      arbitroRepository.save(arbitro);

      flash.addFlashAttribute("success", "Perfil actualizado exitosamente");
      return "redirect:/arbitro/perfil";

    } catch (Exception e) {
      logger.error("Error al actualizar perfil de árbitro: {}", e.getMessage());
      flash.addFlashAttribute("error", "Error al actualizar el perfil");
      return "redirect:/arbitro/perfil";
    }
  }

  @GetMapping("/admin/perfil/cambiar-password")
  public String mostrarFormularioPasswordAdmin(Model model) {
    model.addAttribute("cambiarPassword", new CambiarPasswordDto());
    model.addAttribute("esAdmin", true);
    return "cambiar-password";
  }

  @GetMapping("/arbitro/perfil/cambiar-password")
  public String mostrarFormularioPasswordArbitro(Model model) {
    model.addAttribute("cambiarPassword", new CambiarPasswordDto());
    model.addAttribute("esAdmin", false);
    return "cambiar-password";
  }

  @PostMapping("/admin/perfil/cambiar-password")
  public String cambiarPasswordAdmin(
      @Valid @ModelAttribute("cambiarPassword") CambiarPasswordDto passwordDto,
      BindingResult result,
      RedirectAttributes flash,
      Model model) {

    if (result.hasErrors()) {
      model.addAttribute("esAdmin", true);
      return "cambiar-password";
    }

    if (!passwordDto.getPasswordNueva().equals(passwordDto.getConfirmarPassword())) {
      model.addAttribute("error", "Las contraseñas no coinciden");
      model.addAttribute("esAdmin", true);
      return "cambiar-password";
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    try {
      Administrador administrador = administradorRepository.findByUsername(username);
      if (administrador == null) {
        throw new BusinessException("Administrador no encontrado");
      }

      // Verificar contraseña actual
      if (!passwordEncoder.matches(passwordDto.getPasswordActual(), administrador.getPassword())) {
        model.addAttribute("error", "La contraseña actual es incorrecta");
        model.addAttribute("esAdmin", true);
        return "cambiar-password";
      }

      // Actualizar contraseña
      administrador.setPassword(passwordEncoder.encode(passwordDto.getPasswordNueva()));
      administradorRepository.save(administrador);

      flash.addFlashAttribute("success", "Contraseña actualizada exitosamente");
      return "redirect:/admin/perfil";

    } catch (Exception e) {
      logger.error("Error al cambiar contraseña de administrador: {}", e.getMessage());
      model.addAttribute("error", "Error al cambiar la contraseña");
      model.addAttribute("esAdmin", true);
      return "cambiar-password";
    }
  }

  @PostMapping("/arbitro/perfil/cambiar-password")
  public String cambiarPasswordArbitro(
      @Valid @ModelAttribute("cambiarPassword") CambiarPasswordDto passwordDto,
      BindingResult result,
      RedirectAttributes flash,
      Model model) {

    if (result.hasErrors()) {
      model.addAttribute("esAdmin", false);
      return "cambiar-password";
    }

    if (!passwordDto.getPasswordNueva().equals(passwordDto.getConfirmarPassword())) {
      model.addAttribute("error", "Las contraseñas no coinciden");
      model.addAttribute("esAdmin", false);
      return "cambiar-password";
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    try {
      Arbitro arbitro = arbitroRepository.findByUsername(username);
      if (arbitro == null) {
        throw new BusinessException("Árbitro no encontrado");
      }

      // Verificar contraseña actual
      if (!passwordEncoder.matches(passwordDto.getPasswordActual(), arbitro.getPassword())) {
        model.addAttribute("error", "La contraseña actual es incorrecta");
        model.addAttribute("esAdmin", false);
        return "cambiar-password";
      }

      // Actualizar contraseña
      arbitro.setPassword(passwordEncoder.encode(passwordDto.getPasswordNueva()));
      arbitroRepository.save(arbitro);

      flash.addFlashAttribute("success", "Contraseña actualizada exitosamente");
      return "redirect:/arbitro/perfil";

    } catch (Exception e) {
      logger.error("Error al cambiar contraseña de árbitro: {}", e.getMessage());
      model.addAttribute("error", "Error al cambiar la contraseña");
      model.addAttribute("esAdmin", false);
      return "cambiar-password";
    }
  }

  // 5. Métodos de mapeo privados
  private PerfilDto mapearAdministradorADto(Administrador administrador) {
    PerfilDto dto = new PerfilDto();
    dto.setId(administrador.getId());
    dto.setUsername(administrador.getUsername());
    dto.setRole(administrador.getRole());
    dto.setFechaCreacion(administrador.getFechaCreacion());
    dto.setActivo(administrador.getActivo());
    dto.setUrlFotoPerfil(administrador.getUrlFotoPerfil());
    return dto;
  }

  private PerfilDto mapearArbitroADto(Arbitro arbitro) {
    PerfilDto dto = new PerfilDto();
    dto.setId(arbitro.getId());
    dto.setUsername(arbitro.getUsername()); // Usar el campo username del árbitro
    dto.setNombre(arbitro.getNombre());
    dto.setApellidos(arbitro.getApellidos());
    dto.setNombreCompleto(arbitro.getNombreCompleto());
    dto.setNumeroIdentificacion(arbitro.getNumeroIdentificacion());
    dto.setEmail(arbitro.getEmail());
    dto.setTelefono(arbitro.getTelefono());
    dto.setEspecialidad(arbitro.getEspecialidad());
    dto.setEscalafon(arbitro.getEscalafon());
    dto.setFechaNacimiento(arbitro.getFechaNacimiento());
    dto.setFotoPerfil(arbitro.getFotoPerfil());
    dto.setUrlFotoPerfil(arbitro.getUrlFotoPerfil());
    dto.setRole(arbitro.getRole());
    dto.setFechaCreacion(arbitro.getFechaCreacion());
    dto.setActivo(arbitro.getActivo());
    return dto;
  }

  // Métodos de mapeo para edición
  private EditarPerfilDto mapearAdministradorAEditarDto(Administrador administrador) {
    EditarPerfilDto dto = new EditarPerfilDto();
    dto.setUsername(administrador.getUsername());
    dto.setActivo(administrador.getActivo());
    return dto;
  }

  private EditarPerfilDto mapearArbitroAEditarDto(Arbitro arbitro) {
    EditarPerfilDto dto = new EditarPerfilDto();
    dto.setNombre(arbitro.getNombre());
    dto.setApellidos(arbitro.getApellidos());
    dto.setNumeroIdentificacion(arbitro.getNumeroIdentificacion());
    dto.setEmail(arbitro.getEmail());
    dto.setTelefono(arbitro.getTelefono());
    dto.setEspecialidad(arbitro.getEspecialidad());
    dto.setEscalafon(arbitro.getEscalafon());
    dto.setFechaNacimiento(arbitro.getFechaNacimiento());
    dto.setActivo(arbitro.getActivo());
    return dto;
  }

  // 6. Métodos de utilidad privados

  /**
   * Valida y actualiza el username de un administrador
   *
   * @param editarDto DTO con los datos de edición
   * @param administrador Entidad del administrador
   * @param flash Atributos de redirección
   * @return true si el username cambió, false en caso contrario
   */
  private boolean validarYActualizarUsername(
      EditarPerfilDto editarDto, Administrador administrador, RedirectAttributes flash) {
    String nuevoUsername = editarDto.getUsername();

    if (nuevoUsername != null
        && !nuevoUsername.trim().isEmpty()
        && !nuevoUsername.equals(administrador.getUsername())) {

      // Verificar que el nuevo username no exista
      Administrador existente = administradorRepository.findByUsername(nuevoUsername);
      if (existente != null && !existente.getId().equals(administrador.getId())) {
        flash.addFlashAttribute("error", "El nombre de usuario ya está en uso");
        return false;
      }

      administrador.setUsername(nuevoUsername);
      return true;
    }

    return false;
  }

  /**
   * Procesa la foto de perfil: elimina la anterior y guarda la nueva
   *
   * @param fotoPerfil Archivo de la nueva foto
   * @param urlFotoActual URL de la foto actual
   * @param setUrlFoto Función para establecer la nueva URL
   */
  private void procesarFotoPerfil(
      MultipartFile fotoPerfil,
      String urlFotoActual,
      java.util.function.Consumer<String> setUrlFoto) {
    if (fotoPerfil == null || fotoPerfil.isEmpty()) {
      return;
    }

    try {
      // Eliminar foto anterior si existe
      eliminarFotoAnterior(urlFotoActual);

      // Guardar nueva foto
      String nuevaUrl = guardarNuevaFoto(fotoPerfil);
      setUrlFoto.accept(nuevaUrl);

      logger.info("Nueva foto de perfil guardada: {}", nuevaUrl);

    } catch (IOException e) {
      logger.error("Error al guardar la foto de perfil: {}", e.getMessage(), e);
      throw new BusinessException("No se pudo guardar la foto de perfil");
    }
  }

  /**
   * Elimina una foto anterior del sistema de archivos
   *
   * @param urlFotoActual URL de la foto a eliminar
   */
  private void eliminarFotoAnterior(String urlFotoActual) {
    if (urlFotoActual != null && !urlFotoActual.isEmpty()) {
      String nombreArchivoAnterior = urlFotoActual.replace("/uploads/perfiles/", "");
      Path rutaArchivoAnterior =
          Paths.get(System.getProperty("user.dir"), "uploads", "perfiles", nombreArchivoAnterior);

      File archivoAnterior = rutaArchivoAnterior.toFile();
      if (archivoAnterior.exists() && archivoAnterior.delete()) {
        logger.info("Foto anterior eliminada: {}", nombreArchivoAnterior);
      }
    }
  }

  /**
   * Guarda una nueva foto en el sistema de archivos
   *
   * @param fotoPerfil Archivo de la foto
   * @return URL relativa de la foto guardada
   * @throws IOException Si ocurre un error al guardar
   */
  private String guardarNuevaFoto(MultipartFile fotoPerfil) throws IOException {
    String nombreArchivo = System.currentTimeMillis() + "_" + fotoPerfil.getOriginalFilename();
    Path rutaDirectorio = Paths.get(System.getProperty("user.dir"), "uploads", "perfiles");

    if (!Files.exists(rutaDirectorio)) {
      Files.createDirectories(rutaDirectorio);
    }

    Path rutaArchivo = rutaDirectorio.resolve(nombreArchivo);
    fotoPerfil.transferTo(rutaArchivo.toFile());

    return "/uploads/perfiles/" + nombreArchivo;
  }

  /**
   * Invalida la sesión actual y redirecciona al login
   *
   * @param request Petición HTTP
   * @param flash Atributos de redirección
   * @return Ruta de redirección
   */
  private String invalidarSesionYRedireccionar(
      HttpServletRequest request, RedirectAttributes flash) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    SecurityContextHolder.clearContext();
    flash.addFlashAttribute(
        "success",
        "Perfil actualizado. Por favor, inicie sesión nuevamente con su nuevo nombre de usuario.");
    return "redirect:/login";
  }

  /**
   * Valida y actualiza el email de un árbitro
   *
   * @param editarDto DTO con los datos de edición
   * @param arbitro Entidad del árbitro
   * @param model Modelo para agregar atributos de error
   * @return true si la validación es exitosa, false si hay errores
   */
  private boolean validarYActualizarEmailArbitro(
      EditarPerfilDto editarDto, Arbitro arbitro, Model model) {
    String nuevoEmail = editarDto.getEmail();

    if (nuevoEmail != null
        && !nuevoEmail.trim().isEmpty()
        && !nuevoEmail.trim().equals(arbitro.getEmail())) {

      // Verificar que el nuevo email no exista
      Arbitro existente = arbitroRepository.findByEmail(nuevoEmail.trim());
      if (existente != null && !existente.getId().equals(arbitro.getId())) {
        model.addAttribute("error", "El email ya está en uso");
        return false;
      }

      arbitro.setEmail(nuevoEmail.trim());
    }

    return true;
  }

  /**
   * Actualiza todos los campos editables de un árbitro
   *
   * @param editarDto DTO con los nuevos valores
   * @param arbitro Entidad del árbitro a actualizar
   */
  private void actualizarCamposArbitro(EditarPerfilDto editarDto, Arbitro arbitro) {
    if (editarDto.getNombre() != null && !editarDto.getNombre().trim().isEmpty()) {
      arbitro.setNombre(editarDto.getNombre().trim());
    }
    if (editarDto.getApellidos() != null && !editarDto.getApellidos().trim().isEmpty()) {
      arbitro.setApellidos(editarDto.getApellidos().trim());
    }
    if (editarDto.getNumeroIdentificacion() != null
        && !editarDto.getNumeroIdentificacion().trim().isEmpty()) {
      arbitro.setNumeroIdentificacion(editarDto.getNumeroIdentificacion().trim());
    }
    if (editarDto.getTelefono() != null && !editarDto.getTelefono().trim().isEmpty()) {
      arbitro.setTelefono(editarDto.getTelefono().trim());
    }
    if (editarDto.getEspecialidad() != null) {
      arbitro.setEspecialidad(editarDto.getEspecialidad());
    }
    if (editarDto.getEscalafon() != null && !editarDto.getEscalafon().trim().isEmpty()) {
      arbitro.setEscalafon(editarDto.getEscalafon().trim());
    }
    if (editarDto.getFechaNacimiento() != null) {
      arbitro.setFechaNacimiento(editarDto.getFechaNacimiento());
    }
    if (editarDto.getActivo() != null) {
      arbitro.setActivo(editarDto.getActivo());
    }
  }
}
