package com.caba.caba_pro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Archivo: WebConfig.java Autores: Copilot Fecha última modificación: 08.09.2025 Descripción:
 * Configura la exposición de la carpeta uploads/perfiles como recurso estático. Proyecto: CABA Pro
 * - Sistema de Gestión Integral de Arbitraje
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String rutaAbsoluta = System.getProperty("user.dir") + "/uploads/perfiles/";
    registry
        .addResourceHandler("/uploads/perfiles/**")
        .addResourceLocations("file:" + rutaAbsoluta);
  }
}
