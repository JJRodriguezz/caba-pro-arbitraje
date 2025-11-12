# CABA Pro - Sistema de Gestión Integral de Arbitraje

[![Docker CI/CD](https://github.com/JJRodriguezz/caba-pro-arbitraje/actions/workflows/docker-ci-cd.yml/badge.svg)](https://github.com/JJRodriguezz/caba-pro-arbitraje/actions/workflows/docker-ci-cd.yml)

Sistema profesional de gestión de arbitraje deportivo desarrollado con Spring Boot 3.5.5 y Java 17.

## 🚀 Inicio Rápido

### Con Docker (Recomendado) 🐳

```powershell
# Opción 1: Usando Docker Compose
docker-compose up -d

# Opción 2: Usando Docker directamente
docker run -d -p 8080:8080 --name caba-pro TU-USUARIO/caba-pro:latest

# Acceder a: http://localhost:8080
```

### Sin Docker

## Requisitos del Sistema

- **Java**: 17 o superior
- **Maven**: 3.6+
- **Docker**: (Opcional) Para containerización
- **Tomcat**: Embebido en Spring Boot (no requiere instalación separada)

## Cómo ejecutar el programa

### Paso 1: Verificar requisitos
```bash
# Verificar versión de Java (debe ser 17+)
java -version

# Verificar que Maven wrapper esté disponible
ls mvnw*
```

### Paso 2: Clonar y navegar al proyecto
```bash
# Navegar al directorio del proyecto
cd caba-pro-arbitraje
```

### Paso 3: Ejecutar la aplicación

#### Opción 1: Usando Maven Wrapper (Recomendado)
```bash
# Windows
./mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run
```

#### Opción 2: Compilar y ejecutar JAR
```bash
# Compilar el proyecto
./mvnw clean package

# Ejecutar el JAR generado
java -jar target/caba-pro-0.0.1-SNAPSHOT.jar
```

#### Opción 3: Desde el IDE
1. Importar el proyecto como proyecto Maven
2. Esperar a que se descarguen las dependencias
3. Ejecutar la clase principal `CabaProApplication.java`

## Ruta principal

Una vez iniciada la aplicación, acceder a:

**http://localhost:8080**

La aplicación redirigirá automáticamente al login.

## Configuración adicional

### Variables de entorno (Opcionales)

Puedes configurar estas variables antes de ejecutar la aplicación:

```bash

# Ruta para fotos de perfil (por defecto: uploads/perfiles/)
export CABA_PRO_FOTOS_PERFIL_PATH=uploads/perfiles/

# Configuración de base de datos H2 (opcional)
export SPRING_DATASOURCE_URL=jdbc:h2:file:./data/caba_pro
export SPRING_DATASOURCE_USERNAME=sa
export SPRING_DATASOURCE_PASSWORD=
```

### Configuración automática

La aplicación crea automáticamente:
- Base de datos H2 en `./data/caba_pro`
- Directorio `uploads/perfiles/` para fotos de perfil
- Servidor Tomcat embebido en puerto 8080

### Acceso a la consola H2 (Base de datos)

- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:file:./data/caba_pro`
- **Usuario**: `sa`
- **Contraseña**: (dejar vacío)

## Comandos útiles

### Limpiar y recompilar
```bash
./mvnw clean compile
```

### Verificar formato de código
```bash
./mvnw spotless:check
```

### Aplicar formato automáticamente
```bash
./mvnw spotless:apply
```

## Resolución de problemas

### Error: Puerto 8080 ya en uso
```bash
# Cambiar puerto y ejecutar
export SERVER_PORT=8081
./mvnw spring-boot:run
```

### Error: Java version
```bash
# Verificar versión de Java
java -version
# Debe mostrar version 17 o superior
```

### Error: Permission denied (Linux/macOS)
```bash
# Dar permisos de ejecución al wrapper
chmod +x mvnw
./mvnw spring-boot:run
```

## 🐳 Docker y Despliegue

Para información detallada sobre Docker, despliegue en AWS y CI/CD, consulta:

📖 **[Guía Completa de Docker y Despliegue](docs/DOCKER-DEPLOYMENT.md)**

### Comandos rápidos Docker

```powershell
# Construir imagen localmente
docker build -t caba-pro:latest .

# Ejecutar con docker-compose
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener
docker-compose down
```

## 🔄 CI/CD

Este proyecto usa GitHub Actions para:
- ✅ Build y test automático
- ✅ Build de imagen Docker
- ✅ Push automático a Docker Hub
- ✅ Quality checks con Spotless

El workflow se ejecuta automáticamente en cada push a `main`, `develop` o `diego`.

## 📦 Estructura del Proyecto

```
caba-pro-arbitraje/
├── src/                          # Código fuente
├── target/                       # Compilados
├── data/                         # Base de datos H2
├── uploads/                      # Archivos subidos
├── docs/                         # Documentación
│   └── DOCKER-DEPLOYMENT.md     # Guía Docker completa
├── .github/
│   └── workflows/
│       └── docker-ci-cd.yml     # Pipeline CI/CD
├── Dockerfile                    # Configuración Docker
├── docker-compose.yml           # Orquestación Docker
├── .dockerignore                # Archivos ignorados por Docker
└── pom.xml                      # Dependencias Maven
```

## 👥 Contribución

Para contribuir al proyecto:

1. Fork el repositorio
2. Crea una rama (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -m 'feat: agregar funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es parte de un trabajo académico.
