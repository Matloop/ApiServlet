package com.matloop.util; // Ou outro pacote

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener // Registra este listener automaticamente (requer Servlet 3.0+)
public class DatabaseInitializationListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Este método é chamado quando a aplicação web inicia
        sce.getServletContext().log("Aplicação iniciada. Inicializando banco de dados...");
        try {
            DatabaseUtil.initializeDatabase();
            sce.getServletContext().log("Banco de dados inicializado com sucesso.");
        } catch (Exception e) {
            // Loga o erro de forma que seja visível nos logs do servidor
            sce.getServletContext().log("!!!!!!!! FALHA AO INICIALIZAR O BANCO DE DADOS !!!!!!!!");
            sce.getServletContext().log("Erro: ", e);
            // Você pode querer impedir que a aplicação continue aqui,
            // dependendo da criticidade do banco de dados.
            // Lançar uma RuntimeException aqui pode parar o deploy.
            // throw new RuntimeException("Falha ao inicializar o banco de dados.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Este método é chamado quando a aplicação web para
        sce.getServletContext().log("Aplicação finalizada.");
        // Aqui você pode adicionar código para limpar recursos, se necessário
        // (ex: fechar connection pools se estivesse usando um)
    }
}