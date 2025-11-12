/**
 * Archivo: DataSeeder.java Autores: JJRodriguezz Fecha última modificación: 11.11.2025 Descripción:
 * Generador de datos de prueba para desarrollo y testing Proyecto: CABA Pro - Sistema de Gestión
 * Integral de Arbitraje
 */
package com.caba.caba_pro.config;

import com.caba.caba_pro.enums.AsignacionEstado;
import com.caba.caba_pro.enums.Especialidad;
import com.caba.caba_pro.enums.PartidoEstado;
import com.caba.caba_pro.enums.TorneoEstado;
import com.caba.caba_pro.models.*;
import com.caba.caba_pro.repositories.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración para generar datos de prueba automáticamente. Solo se ejecuta en perfiles de
 * desarrollo.
 */
@Configuration
@Profile({"dev", "default"}) // Se ejecuta solo en desarrollo
public class DataSeeder {

  private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
  private final Random random = new Random();

  @Value("${caba-pro.seed.force-enabled:false}")
  private boolean forceSeederEnabled;

  // Datos de ejemplo para nombres y apellidos
  private static final String[] NOMBRES = {
    "Carlos",
    "María",
    "Juan",
    "Ana",
    "Pedro",
    "Laura",
    "Diego",
    "Sofia",
    "Miguel",
    "Valentina",
    "Andrés",
    "Camila",
    "Santiago",
    "Isabella",
    "Sebastián"
  };

  private static final String[] APELLIDOS = {
    "García",
    "Rodríguez",
    "Martínez",
    "López",
    "González",
    "Hernández",
    "Pérez",
    "Sánchez",
    "Ramírez",
    "Torres",
    "Flores",
    "Rivera",
    "Gómez",
    "Díaz",
    "Cruz"
  };

  private static final String[] EQUIPOS = {
    "Águilas Doradas",
    "Tigres del Norte",
    "Leones de Oro",
    "Halcones Azules",
    "Dragones Rojos",
    "Panteras Negras",
    "Lobos Grises",
    "Búfalos Blancos",
    "Cóndores Andinos",
    "Jaguares Salvajes",
    "Pumas Veloces",
    "Osos Pardos",
    "Zorros Astutos",
    "Toros Bravos",
    "Cobras Venenosas"
  };

  private static final String[] ESTADIOS = {
    "Estadio Metropolitano",
    "Arena Central",
    "Coliseo Municipal",
    "Polideportivo Norte",
    "Estadio del Sur",
    "Arena Deportiva",
    "Complejo Olímpico",
    "Estadio Regional"
  };

  private static final String[] CIUDADES = {
    "Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena", "Bucaramanga", "Pereira", "Manizales"
  };

  private static final String[] ESCALAFONES = {"FIBA", "PRIMERA", "SEGUNDA", "TERCERA"};

  /**
   * Bean que genera datos de prueba al iniciar la aplicación.
   *
   * @param arbitroRepository Repositorio de árbitros
   * @param administradorRepository Repositorio de administradores
   * @param torneoRepository Repositorio de torneos
   * @param partidoRepository Repositorio de partidos
   * @param asignacionRepository Repositorio de asignaciones
   * @param tarifaRepository Repositorio de tarifas
   * @param passwordEncoder Encoder de contraseñas
   * @return CommandLineRunner
   */
  @Bean
  public CommandLineRunner loadData(
      ArbitroRepository arbitroRepository,
      AdministradorRepository administradorRepository,
      TorneoRepository torneoRepository,
      PartidoRepository partidoRepository,
      AsignacionRepository asignacionRepository,
      TarifaRepository tarifaRepository,
      PasswordEncoder passwordEncoder) {

    return args -> {
      // Solo ejecutar si forceSeederEnabled=true o si no hay suficientes datos
      if (!forceSeederEnabled && arbitroRepository.count() > 5) {
        logger.info("⏭  Datos de prueba ya existen. Omitiendo seeder.");
        logger.info(
            "   💡 Para forzar la ejecución, configura 'caba-pro.seed.force-enabled: true' en"
                + " application.yaml");
        return;
      }

      if (forceSeederEnabled) {
        logger.info(" Seeder forzado (force-enabled=true). Generando datos...");
      } else {
        logger.info(" Iniciando generación de datos de prueba...");
      }

      // 1. Crear tarifas por escalafón
      List<Tarifa> tarifas = crearTarifas(tarifaRepository);
      logger.info(" {} tarifas creadas", tarifas.size());

      // 2. Crear árbitros
      List<Arbitro> arbitros = crearArbitros(arbitroRepository, passwordEncoder, 20);
      logger.info(" {} árbitros creados", arbitros.size());

      // 3. Crear torneos
      List<Torneo> torneos = crearTorneos(torneoRepository);
      logger.info(" {} torneos creados", torneos.size());

      // 4. Crear partidos
      List<Partido> partidos = crearPartidos(partidoRepository, torneos, 30);
      logger.info(" {} partidos creados", partidos.size());

      // 5. Crear asignaciones
      List<Asignacion> asignaciones =
          crearAsignaciones(
              asignacionRepository, administradorRepository, partidos, arbitros, tarifas);
      logger.info("{} asignaciones creadas", asignaciones.size());

      logger.info(" Generación de datos completada exitosamente!");
      logger.info(" Resumen:");
      logger.info("   - Árbitros: {}", arbitros.size());
      logger.info("   - Torneos: {}", torneos.size());
      logger.info("   - Partidos: {}", partidos.size());
      logger.info("   - Asignaciones: {}", asignaciones.size());
      logger.info("   - Tarifas: {}", tarifas.size());
    };
  }

  private List<Tarifa> crearTarifas(TarifaRepository tarifaRepository) {
    List<Tarifa> tarifas = new ArrayList<>();

    if (tarifaRepository.count() == 0) {
      tarifas.add(crearTarifa(tarifaRepository, "FIBA", new BigDecimal("500000")));
      tarifas.add(crearTarifa(tarifaRepository, "PRIMERA", new BigDecimal("350000")));
      tarifas.add(crearTarifa(tarifaRepository, "SEGUNDA", new BigDecimal("250000")));
      tarifas.add(crearTarifa(tarifaRepository, "TERCERA", new BigDecimal("150000")));
    } else {
      tarifas.addAll(tarifaRepository.findAll());
    }

    return tarifas;
  }

  private Tarifa crearTarifa(
      TarifaRepository tarifaRepository, String escalafon, BigDecimal monto) {
    Tarifa tarifa = new Tarifa();
    tarifa.setEscalafon(escalafon);
    tarifa.setMonto(monto);
    return tarifaRepository.save(tarifa);
  }

  private List<Arbitro> crearArbitros(
      ArbitroRepository arbitroRepository, PasswordEncoder passwordEncoder, int cantidad) {
    List<Arbitro> arbitros = new ArrayList<>();
    Especialidad[] especialidades = Especialidad.values();

    for (int i = 0; i < cantidad; i++) {
      String nombre = NOMBRES[random.nextInt(NOMBRES.length)];
      String apellido = APELLIDOS[random.nextInt(APELLIDOS.length)];
      String username = nombre.toLowerCase() + i;

      Arbitro arbitro = new Arbitro();
      arbitro.setNombre(nombre);
      arbitro.setApellidos(apellido);
      arbitro.setUsername(username);
      arbitro.setPassword(passwordEncoder.encode("arbitro123"));
      arbitro.setEmail(username + "@cabaproarbitraje.com");
      arbitro.setNumeroIdentificacion("100000" + String.format("%04d", i));
      arbitro.setTelefono("300" + String.format("%07d", random.nextInt(10000000)));
      arbitro.setFechaNacimiento(LocalDate.now().minusYears(25 + random.nextInt(20))); // 25-45 años
      arbitro.setEspecialidad(especialidades[random.nextInt(especialidades.length)]);
      arbitro.setEscalafon(ESCALAFONES[random.nextInt(ESCALAFONES.length)]);

      arbitros.add(arbitroRepository.save(arbitro));
    }

    return arbitros;
  }

  private List<Torneo> crearTorneos(TorneoRepository torneoRepository) {
    List<Torneo> torneos = new ArrayList<>();

    String[] nombresTorneos = {
      "Liga Nacional 2025",
      "Copa Profesional",
      "Torneo Apertura",
      "Campeonato Regional",
      "Liga de Campeones"
    };

    for (String nombreTorneo : nombresTorneos) {
      Torneo torneo = new Torneo();
      torneo.setNombre(nombreTorneo);
      torneo.setDescripcion("Descripción del " + nombreTorneo);
      torneo.setFechaInicio(LocalDate.now().minusMonths(2));
      torneo.setFechaFin(LocalDate.now().plusMonths(4));
      torneo.setEstado(TorneoEstado.ACTIVO);
      torneo.setUbicacion("Colombia");
      torneos.add(torneoRepository.save(torneo));
    }

    return torneos;
  }

  private List<Partido> crearPartidos(
      PartidoRepository partidoRepository, List<Torneo> torneos, int cantidad) {
    List<Partido> partidos = new ArrayList<>();

    for (int i = 0; i < cantidad; i++) {
      Partido partido = new Partido();

      // Seleccionar torneo aleatorio
      Torneo torneo = torneos.get(random.nextInt(torneos.size()));
      partido.setTorneo(torneo);

      // Seleccionar equipos diferentes
      String equipoLocal = EQUIPOS[random.nextInt(EQUIPOS.length)];
      String equipoVisitante;
      do {
        equipoVisitante = EQUIPOS[random.nextInt(EQUIPOS.length)];
      } while (equipoVisitante.equals(equipoLocal));

      partido.setEquipoLocal(equipoLocal);
      partido.setEquipoVisitante(equipoVisitante);

      // Información del partido
      partido.setNombre(equipoLocal + " vs " + equipoVisitante);
      partido.setDescripcion("Jornada " + (i + 1) + " - " + torneo.getNombre());

      // Fecha entre hace 1 mes y dentro de 2 meses
      int diasOffset = random.nextInt(90) - 30;
      partido.setFechaHora(LocalDateTime.now().plusDays(diasOffset).withHour(18).withMinute(0));

      // Estadio
      partido.setSede(ESTADIOS[random.nextInt(ESTADIOS.length)]);

      // Estado según la fecha
      if (diasOffset < -7) {
        partido.setEstado(PartidoEstado.FINALIZADO);
      } else if (diasOffset < 0) {
        partido.setEstado(PartidoEstado.EN_CURSO);
      } else {
        partido.setEstado(PartidoEstado.PROGRAMADO);
      }

      partidos.add(partidoRepository.save(partido));
    }

    return partidos;
  }

  private List<Asignacion> crearAsignaciones(
      AsignacionRepository asignacionRepository,
      AdministradorRepository administradorRepository,
      List<Partido> partidos,
      List<Arbitro> arbitros,
      List<Tarifa> tarifas) {

    List<Asignacion> asignaciones = new ArrayList<>();
    String[] posiciones = {"Principal", "Auxiliar 1", "Auxiliar 2", "Cuarto Árbitro"};

    // Obtener un administrador para las asignaciones
    Administrador admin = administradorRepository.findAll().get(0);

    for (Partido partido : partidos) {
      // Asignar 2-4 árbitros por partido
      int numArbitros = 2 + random.nextInt(3); // 2, 3 o 4 árbitros

      for (int i = 0; i < numArbitros && i < posiciones.length; i++) {
        Arbitro arbitro = arbitros.get(random.nextInt(arbitros.size()));

        Asignacion asignacion = new Asignacion();
        asignacion.setPartido(partido);
        asignacion.setArbitro(arbitro);
        asignacion.setPosicion(posiciones[i]);
        asignacion.setAdminUsername(admin.getUsername());

        // Buscar tarifa según escalafón del árbitro
        BigDecimal monto =
            tarifas.stream()
                .filter(t -> t.getEscalafon().equals(arbitro.getEscalafon()))
                .findFirst()
                .map(Tarifa::getMonto)
                .orElse(new BigDecimal("200000"));

        asignacion.setMontoPago(monto);

        // Estado según fecha del partido
        if (partido.getEstado() == PartidoEstado.PROGRAMADO) {
          asignacion.setEstado(
              random.nextBoolean() ? AsignacionEstado.ACEPTADA : AsignacionEstado.PENDIENTE);
          if (asignacion.getEstado() == AsignacionEstado.ACEPTADA) {
            asignacion.setRespondidoEn(LocalDateTime.now().minusDays(random.nextInt(15)));
          }
        } else if (partido.getEstado() == PartidoEstado.FINALIZADO) {
          asignacion.setEstado(AsignacionEstado.COMPLETADA);
          asignacion.setRespondidoEn(LocalDateTime.now().minusDays(random.nextInt(30)));
        } else {
          asignacion.setEstado(AsignacionEstado.ACEPTADA);
          asignacion.setRespondidoEn(LocalDateTime.now().minusDays(random.nextInt(20)));
        }

        asignaciones.add(asignacionRepository.save(asignacion));
      }
    }

    return asignaciones;
  }
}
