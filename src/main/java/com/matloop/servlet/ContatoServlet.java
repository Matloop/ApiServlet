// Use o pacote correto para sua versão do Tomcat
// package com.exemplo.servlet.javax; // Para Tomcat 9-
// import javax.servlet.annotation.WebServlet;
// import javax.servlet.http.HttpServlet;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import javax.servlet.ServletException;

package com.matloop.servlet; // Para Tomcat 10+

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;


import com.matloop.dao.ContatoDAO;
import com.matloop.model.Contato;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

// Define o padrão de URL para este servlet
@WebServlet("/api/contatos/*")
public class ContatoServlet extends HttpServlet {

    private ContatoDAO contatoDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        contatoDAO = new ContatoDAO();
        gson = new Gson(); // Instancia o Gson
    }

    // GET /api/contatos -> Lista todos
    // GET /api/contatos/{id} -> Busca por ID
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // Ex: "/" ou "/123"

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Listar todos
                List<Contato> contatos = contatoDAO.listarTodos();
                sendJsonResponse(resp, contatos, HttpServletResponse.SC_OK);
            } else {
                // Buscar por ID
                try {
                    int id = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial "/"
                    Contato contato = contatoDAO.buscarPorId(id);
                    if (contato != null) {
                        sendJsonResponse(resp, contato, HttpServletResponse.SC_OK);
                    } else {
                        sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Contato não encontrado.");
                    }
                } catch (NumberFormatException e) {
                    sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "ID inválido.");
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Erro no banco de dados ao buscar contatos", e);
        }
    }

    // POST /api/contatos -> Adiciona novo contato
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Lê o corpo da requisição
            String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            // Converte o JSON do corpo para um objeto Contato
            Contato novoContato = gson.fromJson(body, Contato.class);

            // Validação simples (poderia ser mais robusta)
            if (novoContato == null || novoContato.getNome() == null || novoContato.getNome().trim().isEmpty()) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Dados inválidos para o contato.");
                return;
            }

            Contato contatoCriado = contatoDAO.adicionar(novoContato);
            sendJsonResponse(resp, contatoCriado, HttpServletResponse.SC_CREATED); // 201 Created

        } catch (JsonSyntaxException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "JSON inválido no corpo da requisição.");
        } catch (SQLException e) {
            throw new ServletException("Erro no banco de dados ao adicionar contato", e);
        } catch (Exception e) { // Captura outras exceções
            throw new ServletException("Erro processando requisição POST", e);
        }
    }

    // PUT /api/contatos/{id} -> Atualiza contato existente
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "ID do contato é necessário para atualização.");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Contato contatoAtualizar = gson.fromJson(body, Contato.class);

            if (contatoAtualizar == null || contatoAtualizar.getNome() == null || contatoAtualizar.getNome().trim().isEmpty()) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Dados inválidos para atualização.");
                return;
            }

            contatoAtualizar.setId(id); // Garante que o ID correto está sendo usado

            boolean atualizado = contatoDAO.atualizar(contatoAtualizar);

            if (atualizado) {
                // Opcional: buscar e retornar o objeto atualizado
                Contato contatoAtualizado = contatoDAO.buscarPorId(id);
                sendJsonResponse(resp, contatoAtualizado, HttpServletResponse.SC_OK);
                // Ou apenas retornar sucesso sem corpo: resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Contato não encontrado para atualização.");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "ID inválido.");
        } catch (JsonSyntaxException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "JSON inválido no corpo da requisição.");
        } catch (SQLException e) {
            throw new ServletException("Erro no banco de dados ao atualizar contato", e);
        } catch (Exception e) {
            throw new ServletException("Erro processando requisição PUT", e);
        }
    }

    // DELETE /api/contatos/{id} -> Exclui contato
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "ID do contato é necessário para exclusão.");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean excluido = contatoDAO.excluir(id);

            if (excluido) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Contato não encontrado para exclusão.");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "ID inválido.");
        } catch (SQLException e) {
            throw new ServletException("Erro no banco de dados ao excluir contato", e);
        } catch (Exception e) {
            throw new ServletException("Erro processando requisição DELETE", e);
        }
    }

    // Método auxiliar para enviar respostas JSON
    private void sendJsonResponse(HttpServletResponse resp, Object data, int statusCode) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(statusCode);
        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }

    // Método auxiliar para enviar respostas de erro
    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(statusCode);
        PrintWriter out = resp.getWriter();
        // Cria um objeto simples para a mensagem de erro
        out.print(gson.toJson(java.util.Collections.singletonMap("erro", message)));
        out.flush();
    }
}