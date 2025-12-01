# 🎵 BlueVelvet Music Store - Configuração e Execução

## 🔐 Configuração de Variáveis de Ambiente

Este projeto usa **variáveis de ambiente** para proteger informações sensíveis como senhas e configurações.

### ⚡ Execução Rápida (Recomendado)

**Windows:**

```powershell
.\run-local.ps1
```

**Linux/Mac:**

```bash
chmod +x run-local.sh
./run-local.sh
```

Os scripts acima configuram automaticamente todas as variáveis necessárias.

---

## 📋 Configuração Manual

### 1. Copiar Arquivo de Exemplo

```bash
cp .env.example .env
```

### 2. Editar o arquivo `.env`

Abra o arquivo `.env` e ajuste os valores conforme necessário:

```properties
DB_USERNAME=root
DB_PASSWORD=sua_senha_aqui
DB_URL=jdbc:mysql://localhost:3306/bluevelvet
SERVER_PORT=8082
ADMIN_EMAIL=admin@bluevelvet.com
ADMIN_PASSWORD=admin123
```

### 3. Configurar Variáveis no Sistema

**Windows (PowerShell):**

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "root"
$env:DB_URL = "jdbc:mysql://localhost:3306/bluevelvet"
```

**Linux/Mac:**

```bash
export DB_USERNAME=root
export DB_PASSWORD=root
export DB_URL=jdbc:mysql://localhost:3306/bluevelvet
```

---

## 🚀 Executar Aplicação

### Com Maven:

```bash
mvn spring-boot:run -Dmaven.test.skip=true
```

### Com IDE (IntelliJ/Eclipse):

Configure as variáveis de ambiente nas configurações de execução.

Ver: [GUIA_VARIAVEIS_AMBIENTE.md](GUIA_VARIAVEIS_AMBIENTE.md)

---

## 🔑 Credenciais Padrão

Após a primeira execução, o sistema cria automaticamente um usuário admin:

- **Email:** admin@bluevelvet.com
- **Senha:** admin123

⚠️ **IMPORTANTE:** Altere estas credenciais em produção!

---

## 📚 Documentação Completa

- [GUIA_VARIAVEIS_AMBIENTE.md](GUIA_VARIAVEIS_AMBIENTE.md) - Guia completo de configuração
- [CREDENCIAIS_LOGIN.md](CREDENCIAIS_LOGIN.md) - Solução de problemas de login
- [CORRECOES_SEGURANCA_E_UI.md](CORRECOES_SEGURANCA_E_UI.md) - Histórico de correções

---

## ⚠️ Segurança

### ❌ NUNCA faça:

- Commitar arquivo `.env` no Git
- Compartilhar senhas por email/chat
- Usar senhas padrão em produção
- Expor credenciais em logs

### ✅ SEMPRE faça:

- Use senhas fortes em produção
- Mantenha `.env` no `.gitignore`
- Use gerenciador de senhas
- Revise o código antes de commitrar

---

## 🌐 URLs Principais

**Após iniciar a aplicação:**

- Login: http://localhost:8082/login
- Dashboard: http://localhost:8082/dashboard
- Shop (Público): http://localhost:8082/shop
- API Docs: http://localhost:8082/swagger-ui.html

---

## 🛠️ Variáveis Disponíveis

| Variável         | Descrição            | Padrão                                   |
|------------------|----------------------|------------------------------------------|
| `DB_USERNAME`    | Usuário MySQL        | `root`                                   |
| `DB_PASSWORD`    | Senha MySQL          | `root`                                   |
| `DB_URL`         | URL do banco         | `jdbc:mysql://localhost:3306/bluevelvet` |
| `SERVER_PORT`    | Porta do servidor    | `8082`                                   |
| `UPLOAD_DIR`     | Diretório de uploads | `src/main/resources/static/uploads`      |
| `ADMIN_EMAIL`    | Email do admin       | `admin@bluevelvet.com`                   |
| `ADMIN_PASSWORD` | Senha do admin       | `admin123`                               |

---

## 📞 Suporte

Problemas? Consulte:

1. [CREDENCIAIS_LOGIN.md](CREDENCIAIS_LOGIN.md) - Problemas de login
2. [GUIA_VARIAVEIS_AMBIENTE.md](GUIA_VARIAVEIS_AMBIENTE.md) - Configuração de variáveis
3. Logs da aplicação no console

---

**Última atualização:** 01/12/2025

