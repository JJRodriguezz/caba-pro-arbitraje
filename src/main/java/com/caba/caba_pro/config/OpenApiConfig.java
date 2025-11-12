/**
 * Archivo: OpenApiConfig.java Autores: JJRodriguezz Fecha última modificación: 11.11.2025
 * Descripción: Configuración de OpenAPI/Swagger para documentación de la API REST Proyecto: CABA
 * Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI cabaProOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("CABA Pro - API REST")
                .description(
                    "Sistema de Gestión Integral de Arbitraje - API REST para gestión de árbitros,"
                        + " partidos, asignaciones y torneos")
                .version("1.0.0")
                .contact(
                    new Contact()
                        .name("Equipo CABA Pro")
                        .email("contacto@cabapro.com")
                        .url("https://github.com/JJRodriguezz/caba-pro-arbitraje"))
                .license(
                    new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
        .addServersItem(new Server().url("http://localhost:8080").description("Servidor Local"));
  }
}
