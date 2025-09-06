/**
 * Archivo: BusinessException.java Autores: Isabella.Idarraga Fecha última modificación:
 * [04.09.2025] Descripción: Clase de excepción personalizada para errores de negocio en la
 * aplicación Proyecto: CABA Pro - Sistema de Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.exceptions;

public class BusinessException extends RuntimeException {

  public BusinessException(String message) {
    super(message);
  }

  public BusinessException(String message, Throwable cause) {
    super(message, cause);
  }
}
