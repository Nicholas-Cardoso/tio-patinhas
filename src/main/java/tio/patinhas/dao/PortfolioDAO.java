package tio.patinhas.dao;

import tio.patinhas.db.ConnectionFactory;
import tio.patinhas.models.Portfolio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável pela integração da entidade {@link Portfolio}
 * com a tabela T_CARTEIRA do banco Oracle da FIAP.
 *
 * Implementa as quatro operações pedidas: inserir, atualizar (alterar),
 * excluir e exibir (buscar/listar) dados.
 */
public class PortfolioDAO {

    public void inserir(Portfolio carteira) {
        String sql = "INSERT INTO T_CARTEIRA (ID, ID_USUARIO, SALDO_VIRTUAL) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, carteira.getId());
            stmt.setLong(2, carteira.getUserId());
            stmt.setDouble(3, carteira.getSaldoVirtual());

            stmt.executeUpdate();

            System.out.println("Carteira inserida com sucesso. ID: " + carteira.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir carteira: " + e.getMessage(), e);
        }
    }

    public void atualizar(Long id, Portfolio carteira) {
        String sql = "UPDATE T_CARTEIRA SET SALDO_VIRTUAL = ? WHERE ID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, carteira.getSaldoVirtual());
            stmt.setLong(2, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                System.out.println("Nenhuma carteira encontrada com ID " + id);
            } else {
                System.out.println("Carteira (ID " + id + ") atualizada com sucesso.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar carteira: " + e.getMessage(), e);
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM T_CARTEIRA WHERE ID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                System.out.println("Nenhuma carteira encontrada com ID " + id);
            } else {
                System.out.println("Carteira (ID " + id + ") excluída com sucesso.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao excluir carteira. Verifique se não há transações associadas a ela: "
                            + e.getMessage(), e);
        }
    }

    public Portfolio buscarPorId(Long id) {
        String sql = "SELECT ID, ID_USUARIO, SALDO_VIRTUAL FROM T_CARTEIRA WHERE ID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPortfolio(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar carteira: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Portfolio> listarTodas() {
        List<Portfolio> lista = new ArrayList<>();

        String sql = "SELECT ID, ID_USUARIO, SALDO_VIRTUAL FROM T_CARTEIRA ORDER BY ID";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPortfolio(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar carteiras: " + e.getMessage(), e);
        }
        return lista;
    }

    public void exibirTodas() {
        List<Portfolio> lista = listarTodas();

        System.out.println("--- Carteiras cadastradas no banco (" + lista.size() + ") ---");

        for (Portfolio p : lista) {
            System.out.printf("ID: %d | userId: %d | Saldo virtual: R$ %.2f%n",
                    p.getId(), p.getUserId(), p.getSaldoVirtual());
        }
    }

    private Portfolio mapearPortfolio(ResultSet rs) throws SQLException {
        Portfolio carteira = new Portfolio(rs.getLong("ID"), rs.getLong("ID_USUARIO"));
        carteira.setSaldoVirtual(rs.getDouble("SALDO_VIRTUAL"));
        return carteira;
    }
}
