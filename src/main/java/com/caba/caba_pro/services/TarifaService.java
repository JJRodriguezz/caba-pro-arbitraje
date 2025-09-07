/**
 * Archivo: TarifaService.java Autores: JJRodriguezz Fecha última modificación: 06.09.2025 Descripción:
 * Lógica de negocio para gestionar tarifas por escalafón. Proyecto: CABA Pro - Sistema de Gestión
 * Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.DTOs.TarifaFormDto;
import com.caba.caba_pro.DTOs.TarifaItemDto;
import com.caba.caba_pro.exceptions.BusinessException;
import com.caba.caba_pro.models.Tarifa;
import com.caba.caba_pro.repositories.TarifaRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TarifaService {

  // 1. Constantes estáticas
  private static final Logger logger = LoggerFactory.getLogger(TarifaService.class);
  private static final List<String> ESCALAFONES_SOPORTADOS =
      List.of("FIBA", "PRIMERA", "SEGUNDA", "TERCERA");

  // 2. Variables de instancia
  private final TarifaRepository tarifaRepository;

  // 3. Constructores
  public TarifaService(TarifaRepository tarifaRepository) {
    this.tarifaRepository = tarifaRepository;
  }

  // 4. Métodos públicos

  @Transactional(readOnly = true)
  public List<Tarifa> buscarActivas() {
    return tarifaRepository.findByActivoTrueOrderByEscalafonAsc();
  }

  @Transactional(readOnly = true)
  public BigDecimal obtenerMontoPorEscalafon(String escalafon) {
    return tarifaRepository
        .findByEscalafonIgnoreCaseAndActivoTrue(escalafon)
        .map(Tarifa::getMonto)
        .orElseThrow(
            () ->
                new BusinessException(
                    "No hay una tarifa configurada para el escalafón: " + escalafon));
  }

  public void guardarTarifas(TarifaFormDto form) {
    // Filtramos solo escalafones soportados para evitar ruido o errores
    Map<String, TarifaItemDto> porEscalafon =
        form.getTarifas().stream()
            .filter(t -> ESCALAFONES_SOPORTADOS.contains(t.getEscalafon()))
            .collect(Collectors.toMap(TarifaItemDto::getEscalafon, t -> t, (a, b) -> b));

    // Upsert por escalafón (crea o actualiza)
    List<Tarifa> aGuardar = new ArrayList<>();
    for (String esc : ESCALAFONES_SOPORTADOS) {
      TarifaItemDto item = porEscalafon.get(esc);
      if (item == null) {
        continue; // si no vino en el form, se ignora (no se borra)
      }
      Tarifa tarifa =
          tarifaRepository
              .findByEscalafonIgnoreCaseAndActivoTrue(esc)
              .orElseGet(
                  () -> {
                    Tarifa t = new Tarifa();
                    t.setEscalafon(esc);
                    t.setActivo(true);
                    return t;
                  });
      tarifa.setMonto(item.getMonto());
      aGuardar.add(tarifa);
    }

    tarifaRepository.saveAll(aGuardar);
    logger.info("Tarifas actualizadas: {}", aGuardar.size());
  }

  // 5. Métodos privados
}
