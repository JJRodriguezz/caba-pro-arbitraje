FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copiamos los archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargamos las dependencias
RUN mvn dependency:go-offline -B

# Copiamos el código
COPY src ./src

# Compilamos la aplicación
RUN mvn clean package -DskipTests -Dspotless.check.skip=true

# Imagen ligera solo con JRE para ejecutar
FROM eclipse-temurin:17-jre-alpine

# Metadatos de la imagen
LABEL maintainer="CABA Pro Team"
LABEL description="Sistema de Gestión Integral de Arbitraje"
LABEL version="1.0"

# Creamos un usuario
RUN addgroup -S spring && adduser -S spring -G spring

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el JAR compilado desde la etapa de build
COPY --from=builder /app/target/*.jar app.jar

# Creamos directorios necesarios para la aplicación
RUN mkdir -p /app/data /app/uploads/perfiles && \
    chown -R spring:spring /app

# Cambiamos al usuario no-root
USER spring:spring

# Exponemos el puerto 8080
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando para ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
