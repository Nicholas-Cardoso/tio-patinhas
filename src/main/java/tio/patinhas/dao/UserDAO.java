package tio.patinhas.dao;

import tio.patinhas.db.ConnectionFactory;
import tio.patinhas.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável pela integração da entidade {@link User}
 * com a tabela T_USUARIO do banco Oracle da FIAP.
 *
 * Implementa as quatro operações pedidas: inserir, atualizar (alterar),
 * excluir e exibir (buscar/listar) dados.
 */
public class UserDAO {

    public void inserir(User usuario) {
        String sql = "INSERT INTO T_USUARIO (ID, NOME, EMAIL, SENHA_HASH) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, usuario.getId());
            stmt.setString(2, usuario.getNome());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getSenhaHash());

            stmt.executeUpdate();

            System.out.println("Usuário inserido com sucesso. ID: " + usuario.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir usuário: " + e.getMessage(), e);
        }
    }

    public void atualizar(Long id, User usuario) {
        String sql = "UPDATE T_USUARIO SET NOME = ?, EMAIL = ?, SENHA_HASH = ? WHERE ID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenhaHash());
            stmt.setLong(4, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                System.out.println("Nenhum usuário encontrado com ID " + id);
            } else {
                System.out.println("Usuário (ID " + id + ") atualizado com sucesso.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM T_USUARIO WHERE ID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                System.out.println("Nenhum usuário encontrado com ID " + id);
            } else {
                System.out.println("Usuário (ID " + id + ") excluído com sucesso.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao excluir usuário. Verifique se não há carteira associada a ele: "
                            + e.getMessage(), e);
        }
    }

    public User buscarPorId(Long id) {
        String sql = "SELECT ID, NOME, EMAIL, SENHA_HASH FROM T_USUARIO WHERE ID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário: " + e.getMessage(), e);
        }
        return null;
    }

    public List<User> listarTodos() {
        List<User> lista = new ArrayList<>();

        String sql = "SELECT ID, NOME, EMAIL, SENHA_HASH FROM T_USUARIO ORDER BY ID";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage(), e);
        }
        return lista;
    }

    public void exibirTodos() {
        List<User> lista = listarTodos();

        System.out.println("--- Usuários cadastrados no banco (" + lista.size() + ") ---");

        for (User u : lista) {
            System.out.printf("ID: %d | %s | %s%n", u.getId(), u.getNome(), u.getEmail());
        }
    }

    private User mapearUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("ID"), rs.getString("NOME"), rs.getString("EMAIL"), rs.getString("SENHA_HASH"));
    }
}
