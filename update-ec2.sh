#!/bin/bash
# ============================================
# Script de Actualización - CABA Pro en EC2
# ============================================

echo "🔄 Iniciando actualización de CABA Pro..."

# Variables
CONTAINER_NAME="caba-pro-app"
IMAGE_NAME="diegoagg/caba-pro:latest"
GOOGLE_MAPS_KEY="AIzaSyDL5e_rt6YzzFAHxyzpqvdqTfmp038Y_0s"

# 1. Detener contenedor actual
echo "⏹️  Deteniendo contenedor actual..."
sudo docker stop $CONTAINER_NAME 2>/dev/null || echo "   Contenedor no estaba corriendo"

# 2. Eliminar contenedor
echo "🗑️  Eliminando contenedor viejo..."
sudo docker rm $CONTAINER_NAME 2>/dev/null || echo "   No hay contenedor para eliminar"

# 3. Descargar nueva imagen
echo "📥 Descargando nueva versión desde Docker Hub..."
sudo docker pull $IMAGE_NAME

# 4. Limpiar imágenes viejas
echo "🧹 Limpiando imágenes antiguas..."
sudo docker image prune -f

# 5. Ejecutar nuevo contenedor
echo "🚀 Iniciando nuevo contenedor..."
sudo docker run -d \
  -p 80:8080 \
  --name $CONTAINER_NAME \
  --restart unless-stopped \
  -e GOOGLE_MAPS_API_KEY=$GOOGLE_MAPS_KEY \
  -v /home/ec2-user/caba-data:/app/data \
  $IMAGE_NAME

# 6. Esperar 5 segundos
echo "⏳ Esperando 5 segundos..."
sleep 5

# 7. Verificar estado
echo ""
echo "✅ Estado del contenedor:"
sudo docker ps | grep $CONTAINER_NAME

echo ""
echo "📊 Últimas líneas del log:"
sudo docker logs --tail 20 $CONTAINER_NAME

echo ""
echo "🎉 Actualización completada!"
echo "🌐 Accede a tu aplicación en: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)"
