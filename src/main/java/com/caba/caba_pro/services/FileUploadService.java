/**
 * Archivo: FileUploadService.java Autores: JJRodriguezz Fecha última modificación: 11.11.2025
 * Descripción: Servicio para manejo seguro de carga de archivos (fotos de perfil) Proyecto: CABA
 * Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.services;

import com.caba.caba_pro.config.FotoPerfilProperties;
import com.caba.caba_pro.exceptions.BusinessException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Servicio para manejo seguro de carga y gestión de archivos. */
@Service
public class FileUploadService {

  private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

  // Tipos MIME permitidos para fotos de perfil
  private static final List<String> TIPOS_PERMITIDOS =
      Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");

  // Extensiones permitidas
  private static final List<String> EXTENSIONES_PERMITIDAS =
      Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp");

  // Tamaño máximo: 5MB
  private static final long TAMANO_MAXIMO_BYTES = 5 * 1024 * 1024;

  private final FotoPerfilProperties fotoPerfilProperties;

  public FileUploadService(FotoPerfilProperties fotoPerfilProperties) {
    this.fotoPerfilProperties = fotoPerfilProperties;
  }

  /**
   * Guarda una foto de perfil con validaciones de seguridad.
   *
   * @param archivo Archivo a guardar
   * @return URL relativa del archivo guardado
   * @throws BusinessException Si hay problemas de validación o al guardar
   */
  public String guardarFotoPerfil(MultipartFile archivo) {
    // Validar que el archivo no esté vacío
    if (archivo == null || archivo.isEmpty()) {
      throw new BusinessException("El archivo está vacío");
    }

    // Validar tamaño
    validarTamanoArchivo(archivo);

    // Validar tipo de archivo
    validarTipoArchivo(archivo);

    // Sanitizar y generar nombre único
    String nombreArchivo = generarNombreArchivoSeguro(archivo);

    try {
      // Obtener directorio de trabajo actual (raíz del proyecto)
      String directorioActual = System.getProperty("user.dir");
      String rutaBase = fotoPerfilProperties.getFotosPerfilPath();

      // Si la ruta es relativa, hacerla absoluta desde el directorio del proyecto
      Path rutaDirectorio;
      if (Paths.get(rutaBase).isAbsolute()) {
        rutaDirectorio = Paths.get(rutaBase);
      } else {
        rutaDirectorio = Paths.get(directorioActual, rutaBase);
      }

      // Crear directorio si no existe
      if (!Files.exists(rutaDirectorio)) {
        Files.createDirectories(rutaDirectorio);
        logger.info("Directorio creado: {}", rutaDirectorio.toAbsolutePath());
      }

      // Guardar archivo usando Files.copy para evitar problemas con rutas temporales de Tomcat
      Path rutaArchivo = rutaDirectorio.resolve(nombreArchivo);
      Files.copy(archivo.getInputStream(), rutaArchivo);

      String urlRelativa = "/uploads/perfiles/" + nombreArchivo;
      logger.info(
          "Foto de perfil guardada exitosamente: {} en {}",
          urlRelativa,
          rutaArchivo.toAbsolutePath());

      return urlRelativa;

    } catch (IOException e) {
      logger.error("Error al guardar la foto de perfil: {}", e.getMessage(), e);
      throw new BusinessException("No se pudo guardar la foto de perfil: " + e.getMessage());
    }
  }

  /**
   * Elimina una foto de perfil del sistema de archivos.
   *
   * @param urlFotoPerfil URL relativa de la foto a eliminar (ej: /uploads/perfiles/foto.jpg)
   */
  public void eliminarFotoPerfil(String urlFotoPerfil) {
    if (urlFotoPerfil == null || urlFotoPerfil.isEmpty()) {
      return; // No hay foto que eliminar
    }

    try {
      // Extraer nombre de archivo de la URL
      String nombreArchivo = urlFotoPerfil.substring(urlFotoPerfil.lastIndexOf("/") + 1);

      // Obtener directorio de trabajo actual (raíz del proyecto)
      String directorioActual = System.getProperty("user.dir");
      String rutaBase = fotoPerfilProperties.getFotosPerfilPath();

      // Si la ruta es relativa, hacerla absoluta desde el directorio del proyecto
      Path rutaDirectorio;
      if (Paths.get(rutaBase).isAbsolute()) {
        rutaDirectorio = Paths.get(rutaBase);
      } else {
        rutaDirectorio = Paths.get(directorioActual, rutaBase);
      }

      Path rutaArchivo = rutaDirectorio.resolve(nombreArchivo);

      if (Files.exists(rutaArchivo)) {
        Files.delete(rutaArchivo);
        logger.info("Foto de perfil eliminada: {}", rutaArchivo.toAbsolutePath());
      } else {
        logger.warn("No se encontró el archivo a eliminar: {}", rutaArchivo.toAbsolutePath());
      }

    } catch (IOException e) {
      logger.error("Error al eliminar foto de perfil {}: {}", urlFotoPerfil, e.getMessage(), e);
      // No lanzamos excepción para no bloquear otras operaciones
    }
  }

  /**
   * Valida el tamaño del archivo.
   *
   * @param archivo Archivo a validar
   * @throws BusinessException Si excede el tamaño máximo
   */
  private void validarTamanoArchivo(MultipartFile archivo) {
    if (archivo.getSize() > TAMANO_MAXIMO_BYTES) {
      long tamanoMB = archivo.getSize() / (1024 * 1024);
      throw new BusinessException(
          String.format(
              "El archivo es demasiado grande (%d MB). Tamaño máximo permitido: 5 MB", tamanoMB));
    }
  }

  /**
   * Valida el tipo MIME y extensión del archivo.
   *
   * @param archivo Archivo a validar
   * @throws BusinessException Si el tipo no es permitido
   */
  private void validarTipoArchivo(MultipartFile archivo) {
    String contentType = archivo.getContentType();
    String nombreOriginal = archivo.getOriginalFilename();

    // Validar MIME type
    if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType.toLowerCase())) {
      throw new BusinessException(
          "Tipo de archivo no permitido. Solo se permiten imágenes (JPEG, PNG, GIF, WebP)");
    }

    // Validar extensión
    if (nombreOriginal != null) {
      String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".")).toLowerCase();
      if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
        throw new BusinessException(
            "Extensión de archivo no permitida. Solo se permiten: .jpg, .jpeg, .png, .gif, .webp");
      }
    }
  }

  /**
   * Genera un nombre de archivo único y seguro.
   *
   * @param archivo Archivo original
   * @return Nombre sanitizado y único
   */
  private String generarNombreArchivoSeguro(MultipartFile archivo) {
    String nombreOriginal = archivo.getOriginalFilename();
    if (nombreOriginal == null || nombreOriginal.isEmpty()) {
      throw new BusinessException("El nombre del archivo es inválido");
    }

    // Obtener extensión
    String extension = "";
    int indicePunto = nombreOriginal.lastIndexOf(".");
    if (indicePunto > 0) {
      extension = nombreOriginal.substring(indicePunto).toLowerCase();
    }

    // Generar nombre único con UUID + timestamp
    String nombreUnico = UUID.randomUUID().toString() + "_" + System.currentTimeMillis();

    return nombreUnico + extension;
  }
}
