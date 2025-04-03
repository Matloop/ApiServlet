package com.matloop.dao;

import com.matloop.model.Contato;
import com.matloop.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContatoDAO {

    public List<Contato> listarTodos() throws SQLException {
        List<Contato> contatos = new ArrayList<>();
        String sql = "SELECT id, nome, telefone, email FROM contatos ORDER BY nome";

        // Try-with-resources garante que Connection, PreparedStatement e ResultSet serão fechados
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Contato contato = new Contato(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("telefone"),
                        rs.getString("email")
                );
                contatos.add(contato);
            }
        }
        return contatos;
    }

    public Contato buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, telefone, email FROM contatos WHERE id = ?";
        Contato contato = null;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    contato = new Contato(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("telefone"),
                            rs.getString("email")
                    );
                }
            }
        }
        return contato; // Retorna null se não encontrado
    }

    public Contato adicionar(Contato contato) throws SQLException {
        String sql = "INSERT INTO contatos (nome, telefone, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, contato.getNome());
            stmt.setString(2, contato.getTelefone());
            stmt.setString(3, contato.getEmail());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar contato, nenhuma linha afetada.");
            }

            // Recupera o ID gerado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    contato.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao criar contato, nenhum ID obtido.");
                }
            }
        }
        return contato; // Retorna o contato com o ID preenchido
    }

    public boolean atualizar(Contato contato) throws SQLException {
        String sql = "UPDATE contatos SET nome = ?, telefone = ?, email = ? WHERE id = ?";
        boolean atualizado = false;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contato.getNome());
            stmt.setString(2, contato.getTelefone());
            stmt.setString(3, contato.getEmail());
            stmt.setInt(4, contato.getId());

            int affectedRows = stmt.executeUpdate();
            atualizado = affectedRows > 0;
        }
        return atualizado;
    }

    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM contatos WHERE id = ?";
        boolean excluido = false;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            excluido = affectedRows > 0;
        }
        return excluido;
    }
}