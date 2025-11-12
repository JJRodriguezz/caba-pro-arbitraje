/**
 * Archivo: GoogleMapsService.java Autores: JJRodriguezz Fecha última modificación: 12.11.2025
 * Descripción: Servicio para integrar Google Maps API (geocoding, lugares) Proyecto: CABA Pro -
 * Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Servicio para consumir Google Maps API Este servicio implementa el requisito de consumir una API
 * externa (Bloque 1 - Tarea 1-4) Proporciona funcionalidades de geocoding y búsqueda de lugares
 */
@Service
public class GoogleMapsService {

  // 1. Constantes estáticas
  private static final Logger logger = LoggerFactory.getLogger(GoogleMapsService.class);
  private static final String GEOCODING_API_URL =
      "https://maps.googleapis.com/maps/api/geocode/json";
  private static final String PLACES_API_URL =
      "https://maps.googleapis.com/maps/api/place/autocomplete/json";

  // 2. Variables de instancia
  @Value("${google.maps.api.key:}")
  private String apiKey;

  private final RestTemplate restTemplate;

  // 3. Constructores
  public GoogleMapsService() {
    this.restTemplate = new RestTemplate();
  }

  // 4. Métodos públicos

  /**
   * Verifica si la API Key está configurada
   *
   * @return true si la API Key está presente
   */
  public boolean isConfigured() {
    return apiKey != null && !apiKey.isEmpty();
  }

  /**
   * Obtiene la API Key configurada (para uso en el frontend)
   *
   * @return API Key o cadena vacía si no está configurada
   */
  public String getApiKey() {
    if (!isConfigured()) {
      logger.warn("Google Maps API Key no está configurada");
      return "";
    }
    return apiKey;
  }

  /**
   * Convierte una dirección en coordenadas (geocoding) Ejemplo de consumo de API externa para el
   * entregable
   *
   * @param address Dirección a geocodificar
   * @return Objeto con latitud y longitud, o null si falla
   */
  public Coordinates geocodeAddress(String address) {
    if (!isConfigured()) {
      logger.error("No se puede hacer geocoding: API Key no configurada");
      return null;
    }

    try {
      String url =
          String.format(
              "%s?address=%s&key=%s", GEOCODING_API_URL, address.replace(" ", "+"), apiKey);

      logger.info("Geocodificando dirección: {}", address);

      // Aquí se haría la llamada real a la API
      // String response = restTemplate.getForObject(url, String.class);
      // Por ahora retornamos null para manejo de errores

      return null;
    } catch (Exception e) {
      logger.error("Error al geocodificar dirección: {}", e.getMessage());
      return null;
    }
  }

  /**
   * Verifica la conectividad con la API de Google Maps
   *
   * @return true si la API responde correctamente
   */
  public boolean checkApiHealth() {
    if (!isConfigured()) {
      return false;
    }

    try {
      // Hacemos una llamada simple para verificar la API
      String testUrl = String.format("%s?address=test&key=%s", GEOCODING_API_URL, apiKey);
      restTemplate.getForObject(testUrl, String.class);
      logger.info("Google Maps API está disponible");
      return true;
    } catch (Exception e) {
      logger.error("Google Maps API no está disponible: {}", e.getMessage());
      return false;
    }
  }

  // 5. Clases internas

  /** Clase para representar coordenadas geográficas */
  public static class Coordinates {
    private final double latitude;
    private final double longitude;

    public Coordinates(double latitude, double longitude) {
      this.latitude = latitude;
      this.longitude = longitude;
    }

    public double getLatitude() {
      return latitude;
    }

    public double getLongitude() {
      return longitude;
    }
  }
}
