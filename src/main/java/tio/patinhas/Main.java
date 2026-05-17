package tio.patinhas;

import tio.patinhas.models.FinancialAsset;
import tio.patinhas.models.Cryptocurrency;
import tio.patinhas.models.InvestmentSimulatorService;
import tio.patinhas.models.MarketDashboard;
import tio.patinhas.models.Portfolio;
import tio.patinhas.models.PortfolioPosition;
import tio.patinhas.models.User;

import java.util.List;

public class Main {
    static void main(String[] args) {
        try {
            System.out.println("=== Projeto Tio Patinhas - Teste das classes ===\n");

            User usuario = new User(1L, "Donald Duck", "donald@fiap.com.br", "hash_senha_123");
            System.out.println("Usuário: " + usuario.getNome() + " (id=" + usuario.getId() + ")");

            Portfolio carteira = usuario.getPortfolio();
            System.out.println("Carteira id=" + carteira.getId() + " | userId(FK)=" + carteira.getUserId());

            Cryptocurrency bitcoin = new Cryptocurrency(10L, "Bitcoin", "BTC");
            bitcoin.setPreco(250_000.0);
            bitcoin.setVolumeNegociacao(1_500_000.0);

            Cryptocurrency ethereum = new Cryptocurrency(20L, "Ethereum", "ETH");
            ethereum.setPreco(15_000.0);
            ethereum.setVolumeNegociacao(800_000.0);

            FinancialAsset ativo = bitcoin;
            System.out.println("\nPolimorfismo dinâmico (override):");
            System.out.println(ativo.descricaoResumida());
            System.out.println("Valor de 2.5 unidades: R$ " + ativo.calcularValor(2.5));

            carteira.creditar(1_000_000.0);

            InvestmentSimulatorService simulador = new InvestmentSimulatorService();
            simulador.comprar(carteira, bitcoin, 1.0);
            simulador.comprar(carteira, ethereum, 5.0);

            PortfolioPosition posicaoBtc = carteira.buscarPosicao(bitcoin.getId());
            System.out.println("\nEntidade associativa PosicaoCarteira:");
            System.out.println("  PK id=" + posicaoBtc.getId());
            System.out.println("  FK portfolioId=" + posicaoBtc.getPortfolioId());
            System.out.println("  FK cryptoId=" + posicaoBtc.getCryptoId());
            System.out.println("  quantidade=" + posicaoBtc.getQuantidade());

            System.out.println("\nSaldo virtual: R$ " + carteira.getSaldoVirtual());
            System.out.println("Transações: " + carteira.getTransacoes().size());

            MarketDashboard dashboard = new MarketDashboard();
            System.out.println("\nPolimorfismo estático (overload):");
            dashboard.exibirResumo(bitcoin);
            dashboard.exibirResumo(List.of(bitcoin, ethereum));

            System.out.println("\n=== Teste concluído com sucesso ===");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao popular objetos: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            Portfolio carteiraInvalida = new Portfolio(99L, 99L);
            carteiraInvalida.debitar(100.0);
        } catch (IllegalStateException e) {
            System.out.println("\nTry-catch (cenário de erro esperado): " + e.getMessage());
        }
    }
}
