#!/bin/bash
# ============================================
# Script de Administración - CABA Pro EC2
# ============================================

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

CONTAINER_NAME="caba-pro-app"

# Función para mostrar el menú
show_menu() {
    echo ""
    echo "================================"
    echo "   CABA Pro - Administración"
    echo "================================"
    echo "1. 📊 Ver estado"
    echo "2. ▶️  Iniciar aplicación"
    echo "3. ⏹️  Detener aplicación"
    echo "4. 🔄 Reiniciar aplicación"
    echo "5. 📋 Ver logs (últimas 50 líneas)"
    echo "6. 📡 Ver logs en tiempo real"
    echo "7. 💻 Ver uso de recursos"
    echo "8. 🔄 Actualizar a nueva versión"
    echo "9. 🗑️  Eliminar aplicación"
    echo "0. ❌ Salir"
    echo "================================"
}

# Función para ver estado
check_status() {
    echo -e "${YELLOW}📊 Estado del contenedor:${NC}"
    if sudo docker ps | grep -q $CONTAINER_NAME; then
        echo -e "${GREEN}✅ La aplicación está CORRIENDO${NC}"
        sudo docker ps | grep $CONTAINER_NAME
    else
        echo -e "${RED}❌ La aplicación está DETENIDA${NC}"
        sudo docker ps -a | grep $CONTAINER_NAME || echo "No se encontró el contenedor"
    fi
}

# Función para iniciar
start_app() {
    echo -e "${YELLOW}▶️  Iniciando aplicación...${NC}"
    sudo docker start $CONTAINER_NAME
    sleep 2
    check_status
}

# Función para detener
stop_app() {
    echo -e "${YELLOW}⏹️  Deteniendo aplicación...${NC}"
    sudo docker stop $CONTAINER_NAME
    sleep 2
    check_status
}

# Función para reiniciar
restart_app() {
    echo -e "${YELLOW}🔄 Reiniciando aplicación...${NC}"
    sudo docker restart $CONTAINER_NAME
    sleep 2
    check_status
}

# Función para ver logs
show_logs() {
    echo -e "${YELLOW}📋 Últimas 50 líneas del log:${NC}"
    sudo docker logs --tail 50 $CONTAINER_NAME
}

# Función para ver logs en vivo
live_logs() {
    echo -e "${YELLOW}📡 Logs en tiempo real (Ctrl+C para salir):${NC}"
    sudo docker logs -f $CONTAINER_NAME
}

# Función para ver recursos
show_stats() {
    echo -e "${YELLOW}💻 Uso de recursos (Ctrl+C para salir):${NC}"
    sudo docker stats $CONTAINER_NAME
}

# Función para actualizar
update_app() {
    echo -e "${YELLOW}🔄 Actualizando a nueva versión...${NC}"
    ./update-ec2.sh
}

# Función para eliminar
delete_app() {
    echo -e "${RED}⚠️  ¿Estás seguro de eliminar la aplicación? (s/n)${NC}"
    read -r response
    if [[ "$response" =~ ^([sS][iI]|[sS])$ ]]; then
        echo "🗑️  Eliminando..."
        sudo docker stop $CONTAINER_NAME
        sudo docker rm $CONTAINER_NAME
        echo -e "${GREEN}✅ Aplicación eliminada${NC}"
    else
        echo "❌ Cancelado"
    fi
}

# Loop principal
while true; do
    show_menu
    echo -n "Selecciona una opción: "
    read -r option
    
    case $option in
        1) check_status ;;
        2) start_app ;;
        3) stop_app ;;
        4) restart_app ;;
        5) show_logs ;;
        6) live_logs ;;
        7) show_stats ;;
        8) update_app ;;
        9) delete_app ;;
        0) echo "👋 ¡Hasta luego!"; exit 0 ;;
        *) echo -e "${RED}❌ Opción inválida${NC}" ;;
    esac
    
    echo ""
    echo "Presiona Enter para continuar..."
    read -r
done
