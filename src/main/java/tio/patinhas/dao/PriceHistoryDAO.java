package tio.patinhas.dao;

import tio.patinhas.db.ConnectionFactory;
import tio.patinhas.models.PriceHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe responsável pela integração da entidade {@link PriceHistory}
 * com a tabela T_HISTORICO_PRECO do banco Oracle da FIAP.
 *
 * Implementa as quatro operações pedidas: inserir, atualizar (alterar),
 * excluir e exibir (buscar/listar) dados.
 */
public class PriceHistoryDAO {

    public void inserir(PriceHistory historico) {
        String sql = "INSERT INTO T_HISTORICO_PRECO (ID, ID_CRIPTOMOEDA, DATA_HORA, PRECO) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, historico.getId());
            stmt.setLong(2, historico.getCryptoId());
            stmt.setTimestamp(3, new Timestamp(historico.getDataHora().getTime()));
            stmt.setDouble(4, historico.getPreco());

            stmt.executeUpdate();

            System.out.println("Histórico de preço inserido com sucesso. ID: " + historico.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir histórico de preço: " + e.getMessage(), e);
        }
    }

    public void atualizar(Long id, PriceHistory historico) {
        String sql = "UPDATE T_HISTORICO_PRECO SET ID_CRIPTOMOEDA = ?, DATA_HORA = ?, PRECO = ? WHERE ID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, historico.getCryptoId());
            stmt.setTimestamp(2, new Timestamp(historico.getDataHora().getTime()));
            stmt.setDouble(3, historico.getPreco());
            stmt.setLong(4, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                System.out.println("Nenhum histórico encontrado com ID " + id);
            } else {
                System.out.println("Histórico (ID " + id + ") atualizado com sucesso.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar histórico de preço: " + e.getMessage(), e);
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM T_HISTORICO_PRECO WHERE ID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                System.out.println("Nenhum histórico encontrado com ID " + id);
            } else {
                System.out.println("Histórico (ID " + id + ") excluído com sucesso.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir histórico de preço: " + e.getMessage(), e);
        }
    }

    public PriceHistory buscarPorId(Long id) {
        String sql = "SELECT ID, ID_CRIPTOMOEDA, DATA_HORA, PRECO FROM T_HISTORICO_PRECO WHERE ID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPriceHistory(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar histórico de preço: " + e.getMessage(), e);
        }
        return null;
    }

    public List<PriceHistory> listarTodos() {
        List<PriceHistory> lista = new ArrayList<>();

        String sql = "SELECT ID, ID_CRIPTOMOEDA, DATA_HORA, PRECO FROM T_HISTORICO_PRECO ORDER BY ID";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPriceHistory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar histórico de preços: " + e.getMessage(), e);
        }
        return lista;
    }

    public void exibirTodos() {
        List<PriceHistory> lista = listarTodos();

        System.out.println("--- Histórico de preços cadastrados no banco (" + lista.size() + ") ---");

        for (PriceHistory h : lista) {
            System.out.printf("ID: %d | cryptoId: %d | Data: %s | Preço: %.8f%n",
                    h.getId(), h.getCryptoId(), h.getDataHora(), h.getPreco());
        }
    }

    private PriceHistory mapearPriceHistory(ResultSet rs) throws SQLException {
        PriceHistory historico = new PriceHistory(
                rs.getLong("ID"), rs.getLong("ID_CRIPTOMOEDA"), rs.getDouble("PRECO"));
        Timestamp ts = rs.getTimestamp("DATA_HORA");
        if (ts != null) {
            historico.setDataHora(new Date(ts.getTime()));
        }
        return historico;
    }
}
