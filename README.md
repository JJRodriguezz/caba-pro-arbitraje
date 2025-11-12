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

## � Documentación de la API

La aplicación expone una API REST protegida por autenticación basada en formulario (Spring Security). A continuación un resumen de los endpoints más importantes (ver OpenAPI/Swagger para detalles y ejemplos de respuesta):

- Swagger UI: http://localhost:8080/swagger-ui.html  (o http://localhost:8080/swagger-ui/index.html)
- H2 Console: http://localhost:8080/h2-console  (JDBC URL: jdbc:h2:file:./data/caba_pro, usuario: sa, sin contraseña)

Endpoints REST principales (requieren autenticación):

- GET /api/arbitros
	- Descripción: Lista árbitros activos.
	- Rol: ROLE_ADMIN

- GET /api/arbitros/{id}
	- Descripción: Obtiene detalles de un árbitro.
	- Rol: ROLE_ADMIN o ROLE_ARBITRO (para su propio recurso)

- GET /api/arbitros/username/{username}
	- Descripción: Obtiene árbitro por username.

- GET /api/partidos
	- Descripción: Lista partidos activos.
	- Rol: ROLE_ADMIN

- GET /api/partidos/{id}
	- Descripción: Detalle de un partido (incluye asignaciones activas).
	- Rol: ROLE_ADMIN o ROLE_ARBITRO (si corresponde)

- GET /api/asignaciones
	- Descripción: Endpoints para gestión de asignaciones (ver controller para rutas y autorización exacta).

Nota: Las rutas del panel administrativo (Thymeleaf) están bajo /admin/** y requieren ROLE_ADMIN. Las rutas de árbitro están bajo /arbitro/** y requieren ROLE_ARBITRO.

## 🔐 Autenticación / Usuarios

La aplicación usa login con formulario web. Página de login: http://localhost:8080/login

Credenciales por defecto (si la aplicación crea el admin en la primera ejecución):

- Username: admin
- Password: admin123

⚠️ Cambia esta contraseña después del primer login. El usuario administrador por defecto se crea solo si no existen administradores en la BD.

## Seeder (datos de prueba)

Se incluye un seeder (CommandLineRunner) que genera tarifas, árbitros, torneos, partidos y asignaciones para facilitar pruebas y demo.

- Clase: `src/main/java/.../config/DataSeeder.java`
- Perfil: `dev` (también se ejecuta en `default` si no se configura otro perfil)

Cómo usarlo:

1. Por seguridad por defecto el seeder omite la generación si ya hay datos (comprueba `arbitroRepository.count()`). Si quieres forzar la generación temporalmente para demo, tienes dos opciones:
	 - Ejecutar la aplicación con una base de datos vacía (borrar `./data/caba_pro` antes de arrancar).
	 - Editar la guardia en `DataSeeder.java` (solo para desarrollo) para permitir ejecución forzada.

2. Después de ejecutar el seeder verás en logs un resumen con cuántos registros se crearon (árbitros, partidos, asignaciones, tarifas, torneos).

Recomendación: Una vez que terminaste la demostración, restaura la condición del seeder (vuelve a activar la comprobación para evitar duplicados en ejecuciones posteriores).

## Subida de fotos de perfil

- Ruta de almacenamiento en disco (por defecto): `uploads/perfiles/` en la raíz del proyecto.
- El servicio de subida valida tipos MIME de imagen y tamaño máximo 5 MB.
- La implementación usa escritura segura con `Files.copy(InputStream, Path)` y crea el directorio si no existe. Esto evita problemas con `MultipartFile.transferTo(...)` que en algunos contenedores puede resolver rutas relativas al directorio temporal del servlet.

Ver/editar foto desde UI (admin):

- Edita un árbitro desde el panel de administrador (`/admin/arbitros`) y usa el campo de carga de imagen (preview en cliente antes de subir).

## Tests

Ejecutar pruebas unitarias:

```bash
./mvnw.cmd test
```

## Limpieza / Reset de datos

Si quieres resetear la base de datos y volver al estado inicial (por ejemplo para re-ejecutar el seeder):

1. Detén la aplicación.
2. Borra el archivo de la base de datos: `./data/caba_pro.mv.db` y su archivo de trace si existe.
3. Borra el directorio de uploads si deseas eliminar fotos: `./uploads/perfiles/`
4. Vuelve a arrancar la aplicación.

## Notas y recomendaciones

- DevTools está habilitado en desarrollo; durante ediciones de clases puede provocar recargas automáticas y errores transitorios de classloader. Si detectas NoClassDefFoundError tras cambios rápidos, realiza un `clean compile` y reinicia la aplicación.
- No se realizaron cambios en los modelos de base de datos para permitir la vista/edición de fotos — la funcionalidad de foto se implementó sin modificar entidades persistentes (se usó la propiedad existente `urlFotoPerfil`).

---

Si quieres, ahora puedo:

1. Generar un archivo Postman/Insomnia con colecciones y ejemplos para los endpoints principales.
2. Completar la documentación OpenAPI (añadir ejemplos de request/response en los controllers si falta).
3. Revertir la modificación temporal del seeder (re-habilitar la guardia) y añadir una propiedad en application.yaml para forzar seeding por configuración.

Dime cuál prefieres y lo hago a continuación.
>>>>>>> main
