#!/bin/bash

# ========================================
# SCRIPT DE EXECUÇÃO LOCAL - BLUEVELVET
# ========================================
# Este script configura as variáveis de ambiente e inicia a aplicação
# ========================================

echo "╔════════════════════════════════════════════════╗"
echo "║  🎵 BlueVelvet Music Store - Inicialização   ║"
echo "╚════════════════════════════════════════════════╝"
echo ""

# Configurar variáveis de ambiente
echo "⚙️  Configurando variáveis de ambiente..."

export DB_USERNAME=root
export DB_PASSWORD=root
export DB_URL=jdbc:mysql://localhost:3306/bluevelvet
export SERVER_PORT=8082
export UPLOAD_DIR=src/main/resources/static/uploads
export ADMIN_EMAIL=admin@bluevelvet.com
export ADMIN_PASSWORD=admin123

echo "✓ Variáveis configuradas:"
echo "  • DB_USERNAME: $DB_USERNAME"
echo "  • DB_PASSWORD: ***"
echo "  • DB_URL: $DB_URL"
echo "  • SERVER_PORT: $SERVER_PORT"
echo "  • ADMIN_EMAIL: $ADMIN_EMAIL"
echo ""

# Verificar se o MySQL está rodando
echo "🔍 Verificando MySQL..."
if pgrep -x "mysqld" > /dev/null; then
    echo "✓ MySQL está rodando"
else
    echo "⚠️  MySQL não detectado. Certifique-se de que está rodando!"
fi
echo ""

# Matar processos Java existentes
echo "🧹 Limpando processos Java antigos..."
if pgrep -f "spring-boot:run" > /dev/null; then
    pkill -f "spring-boot:run"
    echo "✓ Processos Java anteriores finalizados"
else
    echo "✓ Nenhum processo Java anterior encontrado"
fi
echo ""

# Iniciar aplicação
echo "╔════════════════════════════════════════════════╗"
echo "║  🚀 Iniciando Aplicação...                    ║"
echo "╚════════════════════════════════════════════════╝"
echo ""
echo "📝 Logs da aplicação:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Executar Maven
mvn spring-boot:run -Dmaven.test.skip=true

# Ao finalizar
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✓ Aplicação finalizada"

