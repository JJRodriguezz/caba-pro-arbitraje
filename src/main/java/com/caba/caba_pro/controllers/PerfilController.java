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
    String email = auth.getName();

    logger.info("Mostrando perfil de árbitro: {}", email);

    try {
      Arbitro arbitro = arbitroRepository.findByEmail(email);
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
    String email = auth.getName();

    try {
      Arbitro arbitro = arbitroRepository.findByEmail(email);
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
      HttpServletRequest request) {

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

      boolean usernameChanged = false;
      String nuevoUsername = editarDto.getUsername();

      // Verificar si el username cambió
      if (nuevoUsername != null
          && !nuevoUsername.trim().isEmpty()
          && !nuevoUsername.equals(administrador.getUsername())) {

        // Verificar que el nuevo username no exista
        Administrador existente = administradorRepository.findByUsername(nuevoUsername);
        if (existente != null && !existente.getId().equals(administrador.getId())) {
          flash.addFlashAttribute("error", "El nombre de usuario ya está en uso");
          return "redirect:/admin/perfil/editar";
        }

        administrador.setUsername(nuevoUsername);
        usernameChanged = true;
      }

      administrador.setActivo(editarDto.getActivo());

      administradorRepository.save(administrador);

      // Si cambió el username, invalidar sesión para forzar nuevo login
      if (usernameChanged) {
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
      @Valid @ModelAttribute("editarPerfil") EditarPerfilDto editarDto,
      BindingResult result,
      RedirectAttributes flash,
      Model model,
      HttpServletRequest request) {

    if (result.hasErrors()) {
      model.addAttribute("especialidades", Especialidad.values());
      model.addAttribute("esAdmin", false);
      return "editar-perfil";
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    try {
      Arbitro arbitro = arbitroRepository.findByEmail(email);
      if (arbitro == null) {
        throw new BusinessException("Árbitro no encontrado");
      }

      // Verificar y actualizar email si cambió
      String nuevoEmail = editarDto.getEmail();
      if (nuevoEmail != null
          && !nuevoEmail.trim().isEmpty()
          && !nuevoEmail.equals(arbitro.getEmail())) {

        // Verificar que el nuevo email no exista
        Arbitro existente = arbitroRepository.findByEmail(nuevoEmail);
        if (existente != null && !existente.getId().equals(arbitro.getId())) {
          model.addAttribute("error", "El email ya está en uso");
          model.addAttribute("especialidades", Especialidad.values());
          model.addAttribute("esAdmin", false);
          return "editar-perfil";
        }

        arbitro.setEmail(nuevoEmail);
      }

      // Actualizar otros campos del árbitro
      if (editarDto.getNombre() != null) arbitro.setNombre(editarDto.getNombre());
      if (editarDto.getApellidos() != null) arbitro.setApellidos(editarDto.getApellidos());
      if (editarDto.getNumeroIdentificacion() != null)
        arbitro.setNumeroIdentificacion(editarDto.getNumeroIdentificacion());
      if (editarDto.getTelefono() != null) arbitro.setTelefono(editarDto.getTelefono());
      if (editarDto.getEspecialidad() != null) arbitro.setEspecialidad(editarDto.getEspecialidad());
      if (editarDto.getEscalafon() != null) arbitro.setEscalafon(editarDto.getEscalafon());
      if (editarDto.getFechaNacimiento() != null)
        arbitro.setFechaNacimiento(editarDto.getFechaNacimiento());
      arbitro.setActivo(editarDto.getActivo());

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
    String email = auth.getName();

    try {
      Arbitro arbitro = arbitroRepository.findByEmail(email);
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

  // Métodos de mapeo privados
  private PerfilDto mapearAdministradorADto(Administrador administrador) {
    PerfilDto dto = new PerfilDto();
    dto.setId(administrador.getId());
    dto.setUsername(administrador.getUsername());
    dto.setRole(administrador.getRole());
    dto.setFechaCreacion(administrador.getFechaCreacion());
    dto.setActivo(administrador.getActivo());
    return dto;
  }

  private PerfilDto mapearArbitroADto(Arbitro arbitro) {
    PerfilDto dto = new PerfilDto();
    dto.setId(arbitro.getId());
    dto.setUsername(arbitro.getEmail()); // Para árbitros, el username es el email
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
}
