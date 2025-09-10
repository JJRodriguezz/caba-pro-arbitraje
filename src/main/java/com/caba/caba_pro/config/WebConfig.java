/**
 * Archivo: WebConfig.java Autores: Diego.Gonzalez Fecha última modificación: [10.09.2025]
 * Descripción: Configura la exposición de la carpeta uploads/perfiles como recurso estático.
 * Proyecto: CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Autowired private UserInfoInterceptor userInfoInterceptor;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String rutaAbsoluta = System.getProperty("user.dir") + "/uploads/perfiles/";
    registry
        .addResourceHandler("/uploads/perfiles/**")
        .addResourceLocations("file:" + rutaAbsoluta);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(userInfoInterceptor);
  }
}
