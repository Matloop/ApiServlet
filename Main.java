package com.matloop;

import com.matloop.dao.ContatoDAO;
import com.matloop.model.Contato;
import com.matloop.util.DatabaseUtil;

import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // 1. Tenta inicializar o banco de dados
        try {
            System.out.println("Inicializando banco...");
            DatabaseUtil.initializeDatabase();
        } catch (RuntimeException e) {
            System.err.println("ERRO CRÍTICO na inicialização: " + e.getMessage());
            return; // Não continua se a inicialização falhar
        }

        ContatoDAO contatoDAO = new ContatoDAO();

        // 2. Tenta adicionar contatos
        System.out.println("Adicionando contatos...");
        try {
            // Adiciona direto, sem ID (será gerado)
            contatoDAO.adicionar(new Contato(0, "Maria Souza", "48911112222", "maria.s@provider.com"));
            contatoDAO.adicionar(new Contato(0, "João Pereira", "51933334444", "joao.p@provider.com"));
            // Tente adicionar um com email repetido para ver o erro (se a constraint UNIQUE existir)
            contatoDAO.adicionar(new Contato(0, "Maria Repetida", "48955556666", "maria.s@provider.com"));

            System.out.println("Contatos (ou tentativas) adicionados.");

        } catch (SQLException e) {
            // Captura qualquer erro durante as inserções
            System.err.println("ERRO ao adicionar contato: " + e.getMessage());
            // Nota: Se um erro ocorrer aqui, os contatos subsequentes não serão adicionados.
        }

        // 3. Lista os contatos existentes
        System.out.println("\nListando contatos no banco:");
        try {
            List<Contato> contatos = contatoDAO.listarTodos();
            if (contatos.isEmpty()) {
                System.out.println("Nenhum contato encontrado.");
            } else {
                for (Contato c : contatos) {
                    // Formato de saída simplificado
                    System.out.printf("  ID: %d, Nome: %s, Email: %s\n", c.getId(), c.getNome(), c.getEmail());
                }
            }
        } catch (SQLException e) {
            System.err.println("ERRO ao listar contatos: " + e.getMessage());
        }
    }
}