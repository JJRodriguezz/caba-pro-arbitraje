/**
 * Archivo: WebConfig.java Autores: Diego.Gonzalez Fecha última modificación: [11.11.2025]
 * Descripción: Configura la exposición de la carpeta uploads/perfiles como recurso estático e
 * internacionalización. Proyecto: CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.config;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

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
    registry.addInterceptor(localeChangeInterceptor());
  }

  @Bean
  public LocaleResolver localeResolver() {
    CookieLocaleResolver resolver = new CookieLocaleResolver("language");
    resolver.setDefaultLocale(new Locale("es"));
    return resolver;
  }

  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
    interceptor.setParamName("lang");
    return interceptor;
  }
}
