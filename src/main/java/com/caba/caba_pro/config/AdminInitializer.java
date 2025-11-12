/**
 * Archivo: AdminInitializer.java Autores: JJRodriguezz Fecha última modificación: 11.11.2025
 * Descripción: Inicializador para crear usuario administrador por defecto Proyecto: CABA Pro -
 * Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.config;

import com.caba.caba_pro.models.Administrador;
import com.caba.caba_pro.repositories.AdministradorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración para inicializar usuario administrador por defecto. Se ejecuta automáticamente al
 * iniciar la aplicación.
 */
@Configuration
public class AdminInitializer {

  private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

  /**
   * Crea un usuario administrador por defecto si no existe ninguno en la base de datos.
   *
   * @param administradorRepository Repositorio de administradores
   * @param passwordEncoder Encoder de contraseñas
   * @return CommandLineRunner que se ejecuta al inicio
   */
  @Bean
  public CommandLineRunner initAdmin(
      AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
    return args -> {
      // Verificar si ya existe algún administrador
      if (administradorRepository.count() == 0) {
        logger.info("No se encontraron administradores. Creando administrador por defecto...");

        Administrador admin = new Administrador();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ROLE_ADMIN");

        administradorRepository.save(admin);

        logger.info("✅ Administrador por defecto creado exitosamente");
        logger.info("   Username: admin");
        logger.info("   Password: admin123");
        logger.info("   ⚠️  IMPORTANTE: Cambia esta contraseña después del primer login");
      } else {
        logger.info("Ya existen administradores en el sistema. No se creó usuario por defecto.");
      }
    };
  }
}
