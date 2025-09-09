# DEBUG: Problema con editar árbitro

## Problema
- El formulario de editar árbitro no guarda los cambios
- No actualiza datos ni foto

## Archivo que necesita cambio
ArbitroService.java - método actualizarArbitro()

## Cambio necesario - reemplazar método completo:

```java
public Arbitro actualizarArbitro(Long id, ArbitroDto arbitroDto, MultipartFile fotoPerfil) {
    logger.info("=== INICIO ACTUALIZACIÓN ÁRBITRO ===");
    logger.info("ID: {}", id);
    logger.info("Nuevo nombre: {}", arbitroDto.getNombre());

    try {
        // 1. Buscar árbitro existente
        Arbitro arbitroExistente = buscarPorId(id);
        logger.info("✅ Árbitro encontrado: {}", arbitroExistente.getNombreCompleto());

        // 2. Actualizar datos básicos
        arbitroExistente.setNombre(arbitroDto.getNombre());
        arbitroExistente.setApellidos(arbitroDto.getApellidos());
        arbitroExistente.setEmail(arbitroDto.getEmail());
        arbitroExistente.setTelefono(arbitroDto.getTelefono());
        arbitroExistente.setEspecialidad(arbitroDto.getEspecialidad());
        arbitroExistente.setEscalafon(arbitroDto.getEscalafon());
        arbitroExistente.setFechaNacimiento(arbitroDto.getFechaNacimiento());
        logger.info("✅ Datos básicos actualizados");

        // 3. Procesar foto si se envió
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            try {
                String nuevaUrlFoto = guardarNuevaFoto(fotoPerfil);
                
                // Eliminar foto anterior si existe
                if (arbitroExistente.getUrlFotoPerfil() != null) {
                    eliminarFotoAnterior(arbitroExistente.getUrlFotoPerfil());
                }
                
                arbitroExistente.setUrlFotoPerfil(nuevaUrlFoto);
                logger.info("✅ Foto actualizada: {}", nuevaUrlFoto);
            } catch (Exception e) {
                logger.error("❌ Error con foto: {}", e.getMessage());
            }
        }

        // 4. Guardar en base de datos
        Arbitro resultado = arbitroRepository.save(arbitroExistente);
        logger.info("✅ ÁRBITRO GUARDADO EXITOSAMENTE");
        
        return resultado;

    } catch (Exception e) {
        logger.error("❌ ERROR GENERAL: {}", e.getMessage(), e);
        throw new BusinessException("Error al actualizar árbitro: " + e.getMessage());
    }
}
```

SOLUCIÓN RÁPIDA: Comentar las validaciones complejas y enfocase solo en actualizar.
