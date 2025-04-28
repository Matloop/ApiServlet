package com.matloop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {

    // --- Configurações (Melhor externalizar em produção!) ---
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "agenda_contatos"; // Escolha um nome para seu banco
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Sua senha do root (ou de um usuário dedicado)
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    // URL para conectar ao SERVIDOR MySQL (sem especificar o banco de dados)
    // useSSL=false é comum para dev local. serverTimezone=UTC é recomendado.
    // allowPublicKeyRetrieval=true pode ser necessário com MySQL 8+ e autenticação padrão
    private static final String SERVER_URL = String.format(
            "jdbc:mysql://%s:%s/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            DB_HOST, DB_PORT
    );

    // URL para conectar ao BANCO DE DADOS específico
    private static final String DB_URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            DB_HOST, DB_PORT, DB_NAME
    );

    // Carrega o driver JDBC uma vez quando a classe é carregada
    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("FATAL: Driver JDBC não encontrado: " + e.getMessage());
            throw new RuntimeException("Driver JDBC não encontrado.", e);
        }
    }

    /**
     * Obtém uma conexão com o BANCO DE DADOS configurado (DB_NAME).
     * Assume que initializeDatabase() já foi chamado ou que o banco já existe.
     * @return Uma conexão SQL.
     * @throws SQLException Se ocorrer um erro ao conectar.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Verifica e cria o banco de dados e a tabela 'contatos' se não existirem.
     * Deve ser chamado na inicialização da aplicação.
     * CUIDADO: O usuário DB_USER precisa ter permissões para CRIAR DATABASE e TABELAS.
     */
    public static void initializeDatabase() {
        System.out.println("Inicializando banco de dados...");

        // 1. Tentar criar o BANCO DE DADOS se não existir
        try (Connection conn = DriverManager.getConnection(SERVER_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            String createDbSql = "CREATE DATABASE IF NOT EXISTS `" + DB_NAME + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            System.out.println("Executando: " + createDbSql);
            stmt.executeUpdate(createDbSql);
            System.out.println("Banco de dados '" + DB_NAME + "' verificado/criado.");

        } catch (SQLException e) {
            System.err.println("Erro ao tentar criar/verificar o banco de dados: " + e.getMessage());
            // Decide se a aplicação pode continuar ou deve parar
            throw new RuntimeException("Falha crítica na inicialização do banco de dados.", e);
        }

        // 2. Tentar criar a TABELA se não existir (conectando ao banco específico agora)
        try (Connection conn = getConnection(); // Usa a conexão para o DB_NAME
             Statement stmt = conn.createStatement()) {

            String createTableSql = "CREATE TABLE IF NOT EXISTS contatos ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nome VARCHAR(100) NOT NULL,"
                    + "telefone VARCHAR(20),"
                    + "email VARCHAR(100) UNIQUE"
                    + ")";
            System.out.println("Executando SQL para criar tabela...");
            stmt.executeUpdate(createTableSql);
            System.out.println("Tabela 'contatos' verificada/criada com sucesso.");

        } catch (SQLException e) {
            System.err.println("Erro ao tentar criar/verificar a tabela 'contatos': " + e.getMessage());
            throw new RuntimeException("Falha crítica na inicialização da tabela.", e);
        }
        System.out.println("Inicialização do banco de dados concluída.");
    }
}