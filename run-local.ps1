# ========================================
# SCRIPT DE EXECUÇÃO LOCAL - BLUEVELVET
# ========================================
# Este script configura as variáveis de ambiente e inicia a aplicação
# ========================================

Write-Host "================================================" -ForegroundColor Cyan
Write-Host " BlueVelvet Music Store - Inicializacao" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Configurar variaveis de ambiente
Write-Host "Configurando variaveis de ambiente..." -ForegroundColor Yellow

$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "root"
$env:DB_URL = "jdbc:mysql://localhost:3306/bluevelvet"
$env:SERVER_PORT = "8082"
$env:UPLOAD_DIR = "src/main/resources/static/uploads"
$env:ADMIN_EMAIL = "admin@bluevelvet.com"
$env:ADMIN_PASSWORD = "admin123"

Write-Host "Variaveis configuradas:" -ForegroundColor Green
Write-Host "  - DB_USERNAME: $env:DB_USERNAME" -ForegroundColor Gray
Write-Host "  - DB_PASSWORD: ***" -ForegroundColor Gray
Write-Host "  - DB_URL: $env:DB_URL" -ForegroundColor Gray
Write-Host "  - SERVER_PORT: $env:SERVER_PORT" -ForegroundColor Gray
Write-Host "  - ADMIN_EMAIL: $env:ADMIN_EMAIL" -ForegroundColor Gray
Write-Host ""

# Verificar se o MySQL esta rodando
Write-Host "Verificando MySQL..." -ForegroundColor Yellow
$mysqlProcess = Get-Process mysqld -ErrorAction SilentlyContinue
if ($mysqlProcess) {
    Write-Host "MySQL esta rodando (PID: $($mysqlProcess.Id))" -ForegroundColor Green
} else {
    Write-Host "MySQL nao detectado. Certifique-se de que esta rodando!" -ForegroundColor Red
}
Write-Host ""

# Matar processos Java existentes
Write-Host "Limpando processos Java antigos..." -ForegroundColor Yellow
$javaProcesses = Get-Process java -ErrorAction SilentlyContinue
if ($javaProcesses) {
    $javaProcesses | Stop-Process -Force
    Write-Host "Processos Java anteriores finalizados" -ForegroundColor Green
} else {
    Write-Host "Nenhum processo Java anterior encontrado" -ForegroundColor Green
}
Write-Host ""

# Iniciar aplicacao
Write-Host "================================================" -ForegroundColor Cyan
Write-Host " Iniciando Aplicacao..." -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Logs da aplicacao:" -ForegroundColor Yellow
Write-Host "===============================================" -ForegroundColor Gray
Write-Host ""

# Executar Maven
mvn spring-boot:run '-Dmaven.test.skip=true'

# Ao finalizar
Write-Host ""
Write-Host "===============================================" -ForegroundColor Gray
Write-Host "Aplicacao finalizada" -ForegroundColor Green

