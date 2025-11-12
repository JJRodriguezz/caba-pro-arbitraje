# ============================================
# Script de Despliegue Docker - CABA Pro
# ============================================
# Uso: Copia y pega los comandos uno por uno

# ========================================
# PASO 1: VERIFICAR IMAGEN LOCAL
# ========================================

# Ver imágenes disponibles
docker images | Select-String "caba-pro"

# Probar la aplicación localmente
docker run -d `
  -p 8080:8080 `
  --name caba-pro-test `
  -v ${PWD}/data:/app/data `
  -v ${PWD}/uploads:/app/uploads `
  caba-pro:latest

# Ver logs (espera a ver "Started CabaProApplication")
docker logs -f caba-pro-test
# Presiona Ctrl+C para salir de los logs

# Probar en navegador: http://localhost:8080

# Detener y eliminar contenedor de prueba
docker stop caba-pro-test
docker rm caba-pro-test


# ========================================
# PASO 2: LOGIN A DOCKER HUB
# ========================================

# Antes de ejecutar esto, debes:
# 1. Crear cuenta en https://hub.docker.com/signup
# 2. Verificar tu email
# 3. Crear repositorio público llamado "caba-pro"

docker login
# Username: TU-USUARIO-DOCKERHUB
# Password: TU-CONTRASEÑA


# ========================================
# PASO 3: TAG Y PUSH A DOCKER HUB
# ========================================

# IMPORTANTE: Reemplaza "TU-USUARIO" con tu usuario real de Docker Hub

# Tag con 'latest'
docker tag caba-pro:latest TU-USUARIO/caba-pro:latest

# Tag con versión específica
docker tag caba-pro:latest TU-USUARIO/caba-pro:v1.0.0

# Push a Docker Hub (puede tardar varios minutos)
docker push TU-USUARIO/caba-pro:latest
docker push TU-USUARIO/caba-pro:v1.0.0

# Verificar en: https://hub.docker.com/r/TU-USUARIO/caba-pro


# ========================================
# PASO 4: PROBAR IMAGEN DESDE DOCKER HUB
# ========================================

# Eliminar imagen local (para probar que funciona desde Docker Hub)
docker rmi TU-USUARIO/caba-pro:latest

# Pull desde Docker Hub
docker pull TU-USUARIO/caba-pro:latest

# Ejecutar
docker run -d -p 8080:8080 TU-USUARIO/caba-pro:latest

# Probar en navegador: http://localhost:8080


# ========================================
# DOCKER COMPOSE - USO SIMPLIFICADO
# ========================================

# Levantar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Ver servicios corriendo
docker-compose ps

# Detener servicios
docker-compose down

# Reconstruir y levantar (después de cambios en código)
docker-compose up -d --build


# ========================================
# COMANDOS ÚTILES
# ========================================

# Ver todos los contenedores (incluidos detenidos)
docker ps -a

# Ver imágenes
docker images

# Eliminar contenedor
docker rm -f nombre-contenedor

# Eliminar imagen
docker rmi nombre-imagen

# Limpiar todo (cuidado: elimina TODO lo no usado)
docker system prune -a --volumes

# Ver uso de espacio
docker system df

# Ver logs de un contenedor
docker logs nombre-contenedor

# Ejecutar comando dentro del contenedor
docker exec -it nombre-contenedor sh

# Copiar archivo desde contenedor
docker cp nombre-contenedor:/ruta/archivo ./destino

# Copiar archivo al contenedor
docker cp ./archivo nombre-contenedor:/ruta/


# ========================================
# GITHUB ACTIONS - CONFIGURACIÓN
# ========================================

# 1. Ve a: https://github.com/JJRodriguezz/caba-pro-arbitraje/settings/secrets/actions

# 2. Crea estos secrets:
#    Name: DOCKER_USERNAME
#    Value: tu-usuario-dockerhub

#    Name: DOCKER_PASSWORD
#    Value: tu-contraseña-dockerhub

# 3. Haz commit y push
git add .
git commit -m "feat: add Docker configuration and CI/CD pipeline"
git push origin diego

# 4. Monitorea en: https://github.com/JJRodriguezz/caba-pro-arbitraje/actions


# ========================================
# TROUBLESHOOTING
# ========================================

# Si el puerto 8080 está en uso:
docker run -d -p 8090:8080 caba-pro:latest
# Acceder a: http://localhost:8090

# Si tienes problemas de permisos en Windows:
# Reinicia Docker Desktop

# Si la imagen es muy grande:
docker images
# Busca 'caba-pro' y verifica el tamaño

# Si no puedes conectarte a Docker Hub:
docker logout
docker login

# Ver información detallada de la imagen:
docker inspect caba-pro:latest

# Ver procesos dentro del contenedor:
docker top nombre-contenedor
