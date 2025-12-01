# ========================================
# COMO CONFIGURAR VARIÁVEIS DE AMBIENTE NO INTELLIJ
# ========================================

## Opção 1: Configurar na Run Configuration (Recomendado para desenvolvimento)

1. **Abra o IntelliJ IDEA**

2. **Acesse Run > Edit Configurations...**
   - Ou clique no dropdown ao lado do botão Run (▶️) no canto superior direito
   - Selecione "Edit Configurations..."

3. **Selecione a configuração da aplicação**
   - Normalmente será "BluevelvetApplication" ou "Application"

4. **Configure as variáveis de ambiente:**
   - Procure o campo **"Environment variables"**
   - Clique no ícone de pasta 📁 ou no botão "..." ao lado
   - Clique no botão "+" para adicionar cada variável

5. **Adicione as seguintes variáveis:**
   ```
   DB_USERNAME=root
   DB_PASSWORD=root
   DB_URL=jdbc:mysql://localhost:3306/bluevelvet
   SERVER_PORT=8082
   UPLOAD_DIR=src/main/resources/static/uploads
   ADMIN_EMAIL=admin@bluevelvet.com
   ADMIN_PASSWORD=admin123
   ```

6. **OU cole todas de uma vez no formato:**
   ```
   DB_USERNAME=root;DB_PASSWORD=root;DB_URL=jdbc:mysql://localhost:3306/bluevelvet;SERVER_PORT=8082;UPLOAD_DIR=src/main/resources/static/uploads;ADMIN_EMAIL=admin@bluevelvet.com;ADMIN_PASSWORD=admin123
   ```

7. **Clique em OK e Apply**

8. **Execute a aplicação** ▶️

---

## Opção 2: Usar Plugin EnvFile (Recomendado para equipes)

1. **Instale o plugin EnvFile:**
   - File > Settings (Ctrl+Alt+S)
   - Plugins > Marketplace
   - Pesquise "EnvFile"
   - Instale e reinicie o IntelliJ

2. **Crie um arquivo `.env` na raiz do projeto:**
   ```bash
   cp .env.example .env
   ```
   - Edite o arquivo `.env` com seus valores locais

3. **Configure a Run Configuration:**
   - Run > Edit Configurations...
   - Selecione sua aplicação
   - Na aba "EnvFile"
   - Clique em "+" e selecione o arquivo `.env`
   - Marque a opção "Enable EnvFile"

4. **Execute a aplicação** ▶️

---

## Opção 3: Variáveis do Sistema (Global - não recomendado)

### Windows:
1. Pesquise "Editar variáveis de ambiente do sistema"
2. Clique em "Variáveis de Ambiente..."
3. Em "Variáveis do usuário", clique em "Novo"
4. Adicione cada variável
5. **Reinicie o IntelliJ**

### Verificação:
```powershell
# No PowerShell, verifique se as variáveis estão definidas:
$env:DB_USERNAME
$env:DB_PASSWORD
```

---

## Verificando se as variáveis foram carregadas

Quando a aplicação iniciar, você verá no console:
```
Started BluevelvetApplication in X seconds
```

Se houver erro como:
```
Could not resolve placeholder 'DB_USERNAME'
```

Significa que as variáveis não foram carregadas corretamente.

---

## Arquivo .gitignore

Certifique-se de que o arquivo `.env` está no `.gitignore`:
```
.env
```

O arquivo `.env.example` deve ser commitado, mas o `.env` (com valores reais) **nunca** deve ser commitado!

---

## Diferença entre as opções:

| Opção | Vantagem | Desvantagem |
|-------|----------|-------------|
| **Run Configuration** | Simples, específico do projeto | Precisa configurar manualmente |
| **Plugin EnvFile** | Fácil de usar arquivo .env | Requer plugin adicional |
| **Sistema** | Disponível para todos os apps | Afeta todo o sistema, pode conflitar |

**Recomendação:** Use a **Opção 1** (Run Configuration) para começar rápido, ou a **Opção 2** (EnvFile) se trabalha em equipe.

