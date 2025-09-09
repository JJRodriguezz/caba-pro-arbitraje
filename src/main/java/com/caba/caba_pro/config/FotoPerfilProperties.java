package com.caba.caba_pro.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Archivo: FotoPerfilProperties.java Autores: Autores.Archivo Fecha última modificación: 08.09.2025
 * Descripción: Configuración para la ruta de fotos de perfil de árbitros Proyecto: CABA Pro -
 * Sistema de Gestión Integral de Arbitraje
 */
@Configuration
@ConfigurationProperties(prefix = "caba-pro")
public class FotoPerfilProperties {

  /** Ruta donde se almacenan las fotos de perfil de árbitros. */
  private String fotosPerfilPath;

  public String getFotosPerfilPath() {
    return fotosPerfilPath;
  }

  public void setFotosPerfilPath(String fotosPerfilPath) {
    this.fotosPerfilPath = fotosPerfilPath;
  }
}
