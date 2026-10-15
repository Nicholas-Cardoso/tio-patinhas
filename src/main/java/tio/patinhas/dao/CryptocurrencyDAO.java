package tio.patinhas.dao;

import tio.patinhas.db.ConnectionFactory;
import tio.patinhas.models.Cryptocurrency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável pela integração da entidade {@link Cryptocurrency}
 * com a tabela T_CRIPTOMOEDA do banco Oracle da FIAP.
 *
 * Implementa as quatro operações pedidas: inserir, atualizar (alterar),
 * excluir e exibir (buscar/listar) dados.
 */
public class CryptocurrencyDAO {

    /**
     * Insere uma nova criptomoeda no banco e devolve o objeto já com o
     * ID gerado pela sequence/trigger do Oracle.
     *
     * @param crypto objeto com nome, símbolo, preço, variação e volume já preenchidos
     * @param idApi  identificador da moeda na API de mercado (ex.: "bitcoin")
     */
    public void inserir(Cryptocurrency crypto, String idApi) {
        String sql = "INSERT INTO T_CRIPTOMOEDA " +
                "(ID, ID_API, NOME, SIMBOLO, PRECO_ATUAL, VAR_PERCENTUAL, VOLUME_NEGOCIACAO) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, crypto.getId());
            stmt.setString(2, idApi);
            stmt.setString(3, crypto.getNome());
            stmt.setString(4, crypto.getSimbolo());
            stmt.setDouble(5, crypto.getPrecoAtual());
            stmt.setDouble(6, crypto.getVariacaoPercentual());
            stmt.setDouble(7, crypto.getVolumeNegociacao());

            stmt.executeUpdate();

            Long idGerado = buscarIdPorIdApi(conn, idApi);
            if (idGerado != null) {
                crypto.setId(idGerado);
            }

            System.out.println("Criptomoeda inserida com sucesso. ID gerado: " + crypto.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir criptomoeda: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza nome, símbolo, preço, variação e volume de uma
     * criptomoeda já existente, identificada pelo ID.
     */
    public void atualizar(Long id, Cryptocurrency crypto) {
        String sql = "UPDATE T_CRIPTOMOEDA " +
                "SET NOME = ?, SIMBOLO = ?, PRECO_ATUAL = ?, VAR_PERCENTUAL = ?, VOLUME_NEGOCIACAO = ? " +
                "WHERE ID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, crypto.getNome());
            stmt.setString(2, crypto.getSimbolo());
            stmt.setDouble(3, crypto.getPrecoAtual());
            stmt.setDouble(4, crypto.getVariacaoPercentual());
            stmt.setDouble(5, crypto.getVolumeNegociacao());
            stmt.setLong(6, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                System.out.println("Nenhuma criptomoeda encontrada com ID " + id);
            } else {
                System.out.println("Criptomoeda (ID " + id + ") atualizada com sucesso.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar criptomoeda: " + e.getMessage(), e);
        }
    }

    /**
     * Exclui uma criptomoeda pelo ID.
     * Observação: como a FK de T_TRANSACAO para T_CRIPTOMOEDA é RESTRICT,
     * o Oracle vai barrar a exclusão (ORA-02292) se existirem
     * transações associadas a essa moeda.
     */
    public void excluir(Long id) {
        String sql = "DELETE FROM T_CRIPTOMOEDA WHERE ID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                System.out.println("Nenhuma criptomoeda encontrada com ID " + id);
            } else {
                System.out.println("Criptomoeda (ID " + id + ") excluída com sucesso.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao excluir criptomoeda. Verifique se não há transações associadas a ela: "
                            + e.getMessage(), e);
        }
    }

    /**
     * Busca e retorna uma única criptomoeda pelo ID.
     */
    public Cryptocurrency buscarPorId(Long id) {
        String sql = "SELECT ID, NOME, SIMBOLO, PRECO_ATUAL, VAR_PERCENTUAL, VOLUME_NEGOCIACAO " +
                "FROM T_CRIPTOMOEDA WHERE ID = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCryptocurrency(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar criptomoeda: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Lista todas as criptomoedas cadastradas, ordenadas por ID.
     */
    public List<Cryptocurrency> listarTodas() {
        List<Cryptocurrency> lista = new ArrayList<>();

        String sql = "SELECT ID, NOME, SIMBOLO, PRECO_ATUAL, VAR_PERCENTUAL, VOLUME_NEGOCIACAO " +
                "FROM T_CRIPTOMOEDA ORDER BY ID";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCryptocurrency(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar criptomoedas: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Exibe no console todas as criptomoedas cadastradas no banco.
     */
    public void exibirTodas() {
        List<Cryptocurrency> lista = listarTodas();

        System.out.println("--- Criptomoedas cadastradas no banco (" + lista.size() + ") ---");

        for (Cryptocurrency c : lista) {
            System.out.printf("ID: %d | %s (%s) | Preço: %.8f | Variação: %.2f%% | Volume: %.2f%n",
                    c.getId(), c.getNome(), c.getSimbolo(),
                    c.getPrecoAtual(), c.getVariacaoPercentual(), c.getVolumeNegociacao());
        }
    }

    private Long buscarIdPorIdApi(Connection conn, String idApi) throws SQLException {
        String sql = "SELECT ID FROM T_CRIPTOMOEDA WHERE ID_API = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idApi);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("ID");
                }
            }
        }
        return null;
    }

    private Cryptocurrency mapearCryptocurrency(ResultSet rs) throws SQLException {
        Cryptocurrency crypto = new Cryptocurrency(
                rs.getLong("ID"), rs.getString("NOME"), rs.getString("SIMBOLO"));
        crypto.setPreco(rs.getDouble("PRECO_ATUAL"));
        crypto.setVariacaoPercentual(rs.getDouble("VAR_PERCENTUAL"));
        crypto.setVolumeNegociacao(rs.getDouble("VOLUME_NEGOCIACAO"));
        return crypto;
    }
}