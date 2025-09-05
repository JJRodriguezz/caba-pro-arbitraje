/**
 * Archivo: LoginController.java Autores: Isabella.Idarraga Fecha última modificación: [04.09.2025]
 * Descripción: Controlador para la gestión de inicio de sesión Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

  @GetMapping("/login")
  public String mostrarLogin() {
    return "login"; // Esto asume que tienes un archivo login.html en src/main/resources/templates
  }
}
