## Backend API (Java Servlet + JDBC)

Esta seção descreve como executar a API backend feita em Java puro com Servlets e JDBC.

### Pré-requisitos

*   **JDK:** Java Development Kit (versão 11 ou superior recomendada) instalado e configurado no seu PATH.
*   **Git:** Necessário para clonar o repositório (se ainda não o fez).
*   **Apache Tomcat:** Versão 10.1.x recomendada (outras versões 10.x ou 9.x podem funcionar com ajustes nas dependências do `pom.xml` e nos namespaces `jakarta.*` vs `javax.*`). Não é necessário instalar, apenas baixar e extrair. Baixe em: [https://tomcat.apache.org/download-10.cgi](https://tomcat.apache.org/download-10.cgi) (escolha a versão "zip" para Windows 64-bit, ou "tar.gz" para Linux/macOS).
*   **Maven Wrapper:** Os arquivos do wrapper (`mvnw`, `mvnw.cmd`, `.mvn/`) **devem estar incluídos no repositório**. Nenhuma instalação global do Maven é necessária.
*   **Banco de Dados:** Um servidor de banco de dados (como MySQL, PostgreSQL) configurado e acessível. Você precisará criar a tabela `contatos` (veja script SQL no código ou em documentação separada) e configurar as credenciais de acesso no arquivo `src/main/java/com/exemplo/util/DatabaseUtil.java`. **Lembre-se:** *Não coloque senhas reais em código versionado em produção!*

### 1. Construindo a Aplicação (Gerando o .war)

1.  Clone o repositório (se ainda não o fez):
    ```bash
    git clone <url-do-seu-repositorio-aqui>
    cd <nome-da-pasta-do-projeto>
    ```
2.  Execute o Maven Wrapper para limpar o projeto anterior e empacotar a aplicação:
    *   **Windows (CMD/PowerShell):**
        ```bash
        mvnw.cmd clean package
        ```
    *   **Linux / macOS:**
        ```bash
        ./mvnw clean package
        ```
        *(Nota: Pode ser necessário dar permissão de execução: `chmod +x mvnw`)*

3.  Após o build bem-sucedido (`BUILD SUCCESS`), um arquivo chamado `api.war` será gerado dentro da pasta `target/`.

### 2. Configurando o Tomcat

1.  Baixe o Apache Tomcat do link fornecido nos pré-requisitos.
2.  **Extraia** o conteúdo do arquivo baixado (`.zip` ou `.tar.gz`) para um local de sua preferência no seu computador. Ex: `C:\Ferramentas\apache-tomcat-10.1.x` ou `/opt/tomcat`.

### 3. Deploy da Aplicação

1.  Localize o arquivo `api.war` gerado na pasta `target/` do seu projeto.
2.  Localize a pasta `webapps/` dentro do diretório onde você extraiu o Tomcat.
3.  **Copie** o arquivo `api.war` da sua pasta `target/` e **cole-o** dentro da pasta `webapps/` do Tomcat.

### 4. Executando o Servidor Tomcat

1.  Abra um Terminal ou Prompt de Comando.
2.  Navegue até a pasta `bin/` dentro do diretório onde você extraiu o Tomcat:
    ```bash
    # Exemplo Windows
    cd C:\Caminho\Para\apache-tomcat-10.1.x\bin

    # Exemplo Linux/macOS
    cd /caminho/para/apache-tomcat-10.1.x/bin
    ```
3.  Inicie o Tomcat (recomendado rodar no terminal para ver os logs):
    *   **Windows:**
        ```bash
        catalina.bat run
        ```
    *   **Linux / macOS:**
        ```bash
        ./catalina.sh run
        ```
        *(Nota: Pode ser necessário dar permissão de execução: `chmod +x *.sh`)*

4.  Aguarde o Tomcat iniciar. Ele fará o deploy automático do `api.war`. Procure por mensagens de sucesso e pela linha indicando que o servidor iniciou (ex: `Server startup in [xxxx] milliseconds`).
    *   **IMPORTANTE:** Se você encontrar um erro `java.net.BindException` (Address already in use) para as portas `8080` ou `8005` (ou a porta de shutdown que você configurou), significa que outra aplicação está usando essas portas. Você precisará parar a outra aplicação ou mudar as portas no arquivo `conf/server.xml` do Tomcat.
    *   **No Windows:** Pode ser necessário executar o `catalina.bat run` a partir de um terminal aberto **como Administrador**, especialmente se houver conflitos com portas abaixo de 1024 ou problemas de permissão.

5.  Para parar o Tomcat, pressione `Ctrl + C` no terminal onde ele está rodando.

### 5. Endpoints da API

Após o deploy e inicialização bem-sucedidos, a API estará disponível nos seguintes endpoints (assumindo que o Tomcat está rodando em `localhost:8080`):

**URL Base:** `http://localhost:8080/api/api/contatos`

*   `(GET) /` : Lista todos os contatos.
    *   Ex: `http://localhost:8080/api/api/contatos/`
*   `(GET) /{id}` : Busca um contato específico pelo seu ID.
    *   Ex: `http://localhost:8080/api/api/contatos/1`
*   `(POST) /` : Adiciona um novo contato. Requer um corpo (body) na requisição contendo o JSON do contato (sem o `id`).
    *   Ex: `http://localhost:8080/api/api/contatos/`
*   `(PUT) /{id}` : Atualiza um contato existente pelo seu ID. Requer um corpo (body) na requisição contendo o JSON com os dados atualizados do contato.
    *   Ex: `http://localhost:8080/api/api/contatos/1`
*   `(DELETE) /{id}` : Exclui um contato pelo seu ID.
    *   Ex: `http://localhost:8080/api/api/contatos/1`

**Observação sobre a URL:** A URL base contém `/api/api/` porque:
1. O primeiro `/api` é o *context path* da sua aplicação web, derivado do nome do arquivo `api.war`.
2. O segundo `/api/contatos` é o padrão de URL (`url-pattern`) definido na anotação `@WebServlet` do seu `ContatoServlet.java`.
