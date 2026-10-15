package tio.patinhas.dao;

import tio.patinhas.db.ConnectionFactory;
import tio.patinhas.models.Cryptocurrency;
import tio.patinhas.models.Transaction;
import tio.patinhas.models.enums.TransactionType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável pela integração da entidade {@link Transaction}
 * com a tabela T_TRANSACAO do banco Oracle da FIAP.
 *
 * Implementa as quatro operações pedidas: inserir, atualizar (alterar),
 * excluir e exibir (buscar/listar) dados.
 */
public class TransactionDAO {

    public void inserir(Transaction transacao) {
        String sql = "INSERT INTO T_TRANSACAO " +
                "(ID, ID_CARTEIRA, ID_CRIPTOMOEDA, DATA_HORA, TIPO, QUANTIDADE, PRECO_UNITARIO, VALOR_TOTAL) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, transacao.getId());
            stmt.setLong(2, transacao.getPortfolioId());
            stmt.setLong(3, transacao.getCryptoId());
            stmt.setTimestamp(4, Timestamp.valueOf(transacao.getDataHora()));
            stmt.setString(5, transacao.getTipo().name());
            stmt.setDouble(6, transacao.getQuantidade());
            stmt.setDouble(7, transacao.getPrecoUnitario());
            stmt.setDouble(8, transacao.getValorTotal());

            stmt.executeUpdate();

            System.out.println("Transação inserida com sucesso. ID: " + transacao.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir transação: " + e.getMessage(), e);
        }
    }

    public void atualizar(Long id, Transaction transacao) {
        String sql = "UPDATE T_TRANSACAO " +
                "SET TIPO = ?, QUANTIDADE = ?, PRECO_UNITARIO = ?, VALOR_TOTAL = ? " +
                "WHERE ID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transacao.getTipo().name());
            stmt.setDouble(2, transacao.getQuantidade());
            stmt.setDouble(3, transacao.getPrecoUnitario());
            stmt.setDouble(4, transacao.getValorTotal());
            stmt.setLong(5, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                System.out.println("Nenhuma transação encontrada com ID " + id);
            } else {
                System.out.println("Transação (ID " + id + ") atualizada com sucesso.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar transação: " + e.getMessage(), e);
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM T_TRANSACAO WHERE ID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                System.out.println("Nenhuma transação encontrada com ID " + id);
            } else {
                System.out.println("Transação (ID " + id + ") excluída com sucesso.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir transação: " + e.getMessage(), e);
        }
    }

    /**
     * Busca uma transação pelo ID. Como a tabela não guarda o nome/símbolo
     * da moeda, a Cryptocurrency retornada dentro do objeto fica com apenas o ID
     * preenchido (útil para quem quiser completar os dados com o CryptocurrencyDAO).
     */
    public Transaction buscarPorId(Long id) {
        String sql = "SELECT ID, ID_CARTEIRA, ID_CRIPTOMOEDA, DATA_HORA, TIPO, QUANTIDADE, PRECO_UNITARIO " +
                "FROM T_TRANSACAO WHERE ID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearTransaction(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar transação: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Transaction> listarTodas() {
        List<Transaction> lista = new ArrayList<>();

        String sql = "SELECT ID, ID_CARTEIRA, ID_CRIPTOMOEDA, DATA_HORA, TIPO, QUANTIDADE, PRECO_UNITARIO " +
                "FROM T_TRANSACAO ORDER BY ID";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar transações: " + e.getMessage(), e);
        }
        return lista;
    }

    public void exibirTodas() {
        List<Transaction> lista = listarTodas();

        System.out.println("--- Transações cadastradas no banco (" + lista.size() + ") ---");

        for (Transaction t : lista) {
            System.out.printf("ID: %d | carteiraId: %d | cryptoId: %d | %s | qtd: %.8f | preço unit.: %.8f | total: %.2f%n",
                    t.getId(), t.getPortfolioId(), t.getCryptoId(), t.getTipo(),
                    t.getQuantidade(), t.getPrecoUnitario(), t.getValorTotal());
        }
    }

    private Transaction mapearTransaction(ResultSet rs) throws SQLException {
        Cryptocurrency cryptoReferencia = new Cryptocurrency(
                rs.getLong("ID_CRIPTOMOEDA"), "N/D", "N/D");

        Transaction transacao = new Transaction(
                rs.getLong("ID"),
                rs.getLong("ID_CARTEIRA"),
                rs.getLong("ID_CRIPTOMOEDA"),
                cryptoReferencia,
                TransactionType.valueOf(rs.getString("TIPO")),
                rs.getDouble("QUANTIDADE"),
                rs.getDouble("PRECO_UNITARIO"));

        Timestamp ts = rs.getTimestamp("DATA_HORA");
        if (ts != null) {
            transacao.setDataHora(ts.toLocalDateTime());
        }

        return transacao;
    }
}
