# 🔒 Guia de Variáveis de Ambiente - BlueVelvet Music Store

## 📋 Índice

1. [Por que usar variáveis de ambiente?](#por-que-usar)
2. [Configuração no Windows](#windows)
3. [Configuração no Linux/Mac](#linux-mac)
4. [Configuração no IntelliJ IDEA](#intellij)
5. [Configuração no VS Code](#vscode)
6. [Lista de Variáveis](#variaveis)
7. [Boas Práticas](#boas-praticas)

---

## 🎯 Por que usar variáveis de ambiente? {#por-que-usar}

### ❌ Problemas com valores hardcoded:

- Senhas expostas no Git
- Configurações diferentes para cada ambiente
- Risco de segurança ao compartilhar código
- Dificuldade em mudar configurações

### ✅ Vantagens das variáveis de ambiente:

- **Segurança:** Senhas não vão para o Git
- **Flexibilidade:** Configurações diferentes por ambiente
- **Praticidade:** Fácil de mudar sem alterar código
- **Boas práticas:** Padrão da indústria

---

## 🪟 Configuração no Windows {#windows}

### Opção 1: Variáveis de Sistema (Permanente)

1. **Abrir Variáveis de Ambiente:**
    - Pressione `Win + Pause` ou
    - Configurações → Sistema → Sobre → Configurações avançadas do sistema
    - Clique em "Variáveis de Ambiente"

2. **Adicionar Novas Variáveis:**
    - Clique em "Novo" em "Variáveis do usuário"
    - Nome: `DB_USERNAME`
    - Valor: `root`
    - Repita para todas as variáveis

3. **Reiniciar o Terminal/IDE**

### Opção 2: PowerShell (Temporário - sessão atual)

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "root"
$env:DB_URL = "jdbc:mysql://localhost:3306/bluevelvet"
$env:SERVER_PORT = "8082"
$env:ADMIN_EMAIL = "admin@bluevelvet.com"
$env:ADMIN_PASSWORD = "admin123"

# Executar a aplicação
mvn spring-boot:run
```

### Opção 3: Script PowerShell (Recomendado)

Crie um arquivo `run-local.ps1`:

```powershell
# Configurar variáveis de ambiente
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "root"
$env:DB_URL = "jdbc:mysql://localhost:3306/bluevelvet"
$env:SERVER_PORT = "8082"
$env:UPLOAD_DIR = "src/main/resources/static/uploads"
$env:ADMIN_EMAIL = "admin@bluevelvet.com"
$env:ADMIN_PASSWORD = "admin123"

Write-Host "✓ Variáveis de ambiente configuradas" -ForegroundColor Green
Write-Host "✓ Iniciando aplicação..." -ForegroundColor Green

# Executar aplicação
mvn spring-boot:run -Dmaven.test.skip=true
```

Execute: `.\run-local.ps1`

---

## 🐧 Configuração no Linux/Mac {#linux-mac}

### Opção 1: Arquivo .env + export

```bash
# Carregar variáveis do arquivo .env
export $(cat .env | xargs)

# Ou criar um script run.sh
#!/bin/bash
export DB_USERNAME=root
export DB_PASSWORD=root
export DB_URL=jdbc:mysql://localhost:3306/bluevelvet
export SERVER_PORT=8082
export ADMIN_EMAIL=admin@bluevelvet.com
export ADMIN_PASSWORD=admin123

mvn spring-boot:run -Dmaven.test.skip=true
```

### Opção 2: .bashrc ou .zshrc (Permanente)

Adicione ao final do arquivo `~/.bashrc` ou `~/.zshrc`:

```bash
# BlueVelvet Music Store - Environment Variables
export DB_USERNAME=root
export DB_PASSWORD=root
export DB_URL=jdbc:mysql://localhost:3306/bluevelvet
export SERVER_PORT=8082
export ADMIN_EMAIL=admin@bluevelvet.com
export ADMIN_PASSWORD=admin123
```

Recarregue: `source ~/.bashrc`

---

## 💡 Configuração no IntelliJ IDEA {#intellij}

### Método 1: Run Configuration

1. **Abrir Run/Debug Configurations**
    - Menu: Run → Edit Configurations
    - Ou clique no dropdown ao lado do botão Run

2. **Adicionar Environment Variables**
    - Na seção "Environment variables"
    - Clique no ícone de pasta/editar
    - Adicione cada variável:
      ```
      DB_USERNAME=root
      DB_PASSWORD=root
      DB_URL=jdbc:mysql://localhost:3306/bluevelvet
      SERVER_PORT=8082
      ADMIN_EMAIL=admin@bluevelvet.com
      ADMIN_PASSWORD=admin123
      ```

3. **Salvar e Executar**

### Método 2: Plugin EnvFile

1. **Instalar Plugin:**
    - File → Settings → Plugins
    - Procurar: "EnvFile"
    - Instalar e reiniciar

2. **Configurar:**
    - Run → Edit Configurations
    - Aba "EnvFile"
    - Adicionar arquivo `.env`
    - Marcar "Enable EnvFile"

---

## 📝 Configuração no VS Code {#vscode}

### Arquivo launch.json

Crie/edite `.vscode/launch.json`:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot - BluevelvetApplication",
      "request": "launch",
      "mainClass": "com.musicstore.bluevelvet.BluevelvetApplication",
      "projectName": "bluevelvet",
      "env": {
        "DB_USERNAME": "root",
        "DB_PASSWORD": "root",
        "DB_URL": "jdbc:mysql://localhost:3306/bluevelvet",
        "SERVER_PORT": "8082",
        "UPLOAD_DIR": "src/main/resources/static/uploads",
        "ADMIN_EMAIL": "admin@bluevelvet.com",
        "ADMIN_PASSWORD": "admin123"
      }
    }
  ]
}
```

---

## 📊 Lista de Variáveis {#variaveis}

| Variável         | Descrição               | Valor Padrão                             | Obrigatória |
|------------------|-------------------------|------------------------------------------|-------------|
| `DB_USERNAME`    | Usuário do MySQL        | `root`                                   | ✅ Sim       |
| `DB_PASSWORD`    | Senha do MySQL          | `root`                                   | ✅ Sim       |
| `DB_URL`         | URL de conexão do banco | `jdbc:mysql://localhost:3306/bluevelvet` | ✅ Sim       |
| `SERVER_PORT`    | Porta do servidor       | `8082`                                   | ❌ Não       |
| `UPLOAD_DIR`     | Diretório de uploads    | `src/main/resources/static/uploads`      | ❌ Não       |
| `ADMIN_EMAIL`    | Email do admin inicial  | `admin@bluevelvet.com`                   | ❌ Não       |
| `ADMIN_PASSWORD` | Senha do admin inicial  | `admin123`                               | ❌ Não       |

### Sintaxe no application.yaml:

```yaml
${VARIAVEL:valor_padrao}
```

- `VARIAVEL`: Nome da variável de ambiente
- `valor_padrao`: Valor usado se a variável não existir

**Exemplo:**

```yaml
username: ${DB_USERNAME:root}
```

- Se `DB_USERNAME` existir, usa seu valor
- Se não existir, usa `root`

---

## 🛡️ Boas Práticas {#boas-praticas}

### ✅ FAÇA:

1. **Sempre use `.env.example`**
    - Template com valores de exemplo
    - Pode ser commitado no Git
    - Documentação para outros desenvolvedores

2. **Adicione `.env` ao .gitignore**
   ```gitignore
   .env
   .env.local
   *.env
   !.env.example
   ```

3. **Use senhas fortes em produção**
    - Mínimo 16 caracteres
    - Letras, números e símbolos
    - Nunca use senhas padrão

4. **Documentação**
    - Sempre documente cada variável
    - Explique o propósito e valores aceitos

5. **Validação**
    - Valide variáveis obrigatórias na inicialização
    - Falhe rápido se algo estiver errado

### ❌ NÃO FAÇA:

1. **NUNCA commite arquivos .env**
    - Contém credenciais reais
    - Risco crítico de segurança

2. **NUNCA use senhas em logs**
    - Não logue variáveis sensíveis
    - Use máscaras: `password=***`

3. **NUNCA hardcode em produção**
    - Sempre use variáveis de ambiente
    - Ou serviços de secrets (AWS Secrets Manager, etc.)

4. **NUNCA compartilhe .env por email/chat**
    - Use gerenciadores de senhas
    - Ou compartilhamento seguro (1Password, LastPass, etc.)

---

## 🚀 Diferentes Ambientes

### Desenvolvimento (Local)

```properties
DB_USERNAME=root
DB_PASSWORD=root
DB_URL=jdbc:mysql://localhost:3306/bluevelvet_dev
SERVER_PORT=8082
```

### Homologação (Staging)

```properties
DB_USERNAME=bluevelvet_stage
DB_PASSWORD=<senha_forte_complexa>
DB_URL=jdbc:mysql://staging-db.example.com:3306/bluevelvet_stage
SERVER_PORT=8080
```

### Produção

```properties
DB_USERNAME=bluevelvet_prod
DB_PASSWORD=<senha_muito_forte_e_complexa>
DB_URL=jdbc:mysql://prod-db.example.com:3306/bluevelvet_prod
SERVER_PORT=8080
```

---

## 🔐 Serviços de Secrets (Produção)

Para produção, considere usar:

### AWS Secrets Manager

```yaml
spring:
  datasource:
    username: ${aws.secretsmanager.get('bluevelvet/db/username')}
    password: ${aws.secretsmanager.get('bluevelvet/db/password')}
```

### Azure Key Vault

```yaml
spring:
  cloud:
    azure:
      keyvault:
        secret:
          enabled: true
```

### HashiCorp Vault

```yaml
spring:
  cloud:
    vault:
      token: ${VAULT_TOKEN}
      scheme: https
```

---

## 📞 Verificação

### Como verificar se as variáveis estão sendo usadas:

**PowerShell:**

```powershell
echo $env:DB_USERNAME
echo $env:SERVER_PORT
```

**Linux/Mac:**

```bash
echo $DB_USERNAME
echo $SERVER_PORT
```

**Na aplicação (logs):**
Procure por logs que mostrem valores padrão sendo usados.

---

## 📝 Checklist de Segurança

- [ ] `.env` está no `.gitignore`
- [ ] `.env.example` está commitado (sem valores reais)
- [ ] Senhas fortes em produção
- [ ] Variáveis documentadas no README
- [ ] Team sabe como configurar localmente
- [ ] Produção usa serviço de secrets
- [ ] Logs não expõem credenciais
- [ ] Backups de configuração seguros

---

**Última atualização:** 01/12/2025  
**Versão:** 1.0

