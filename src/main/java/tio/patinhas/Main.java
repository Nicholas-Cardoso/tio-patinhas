package tio.patinhas;

import tio.patinhas.models.FinancialAsset;
import tio.patinhas.models.Cryptocurrency;
import tio.patinhas.models.InvestmentSimulatorService;
import tio.patinhas.models.Portfolio;
import tio.patinhas.models.PortfolioPosition;
import tio.patinhas.models.User;
import tio.patinhas.models.MarketDashboard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== Projeto Tio Patinhas - Teste das classes ===\n");

            // 1. Instanciando objetos base
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

            ArrayList<Cryptocurrency> listaCriptomoedas = new ArrayList<>();
            listaCriptomoedas.add(bitcoin);
            listaCriptomoedas.add(ethereum);

            ArrayList<User> listaUsuarios = new ArrayList<>();
            listaUsuarios.add(usuario);
            listaUsuarios.add(new User(2L, "Scrooge McDuck", "patinhas@fiap.com.br", "hash_rico_777"));

            System.out.println("ArrayList de Criptomoedas criado com " + listaCriptomoedas.size() + " itens.");
            System.out.println("ArrayList de Usuários criado com " + listaUsuarios.size() + " itens.");

            HashMap<String, Cryptocurrency> mapaCriptomoedas = new HashMap<>();
            mapaCriptomoedas.put(bitcoin.getSimbolo(), bitcoin);
            mapaCriptomoedas.put(ethereum.getSimbolo(), ethereum);

            HashMap<Long, User> mapaUsuarios = new HashMap<>();
            for (User u : listaUsuarios) {
                mapaUsuarios.put(u.getId(), u);
            }

            System.out.println("HashMap de Criptomoedas criado. Chaves contidas: " + mapaCriptomoedas.keySet());
            System.out.println("HashMap de Usuários criado. Chaves contidas: " + mapaUsuarios.keySet());

            String arquivoCriptoPath = "criptomoedas_report.txt";
            String arquivoUsuariosPath = "usuarios_report.txt";

            salvarCriptomoedasNoArquivo(arquivoCriptoPath, listaCriptomoedas);
            salvarUsuariosNoArquivo(arquivoUsuariosPath, mapaUsuarios);

            System.out.println("Atualizando dados de mercado do Bitcoin no ArrayList...");
            bitcoin.setPreco(265_000.0);
            bitcoin.setVolumeNegociacao(1_850_000.0);

            System.out.println("Sobreescrevendo arquivo com os dados atualizados...");
            salvarCriptomoedasNoArquivo(arquivoCriptoPath, listaCriptomoedas);

            lerArquivoTexto(arquivoCriptoPath);
            lerArquivoTexto(arquivoUsuariosPath);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao popular objetos: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Erro na manipulação de arquivos: " + e.getMessage());
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

    private static void salvarCriptomoedasNoArquivo(String caminho, ArrayList<Cryptocurrency> lista) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminho))) {
            writer.write("--- RELATÓRIO DE CRIPTOMOEDAS ATIVAS ---\n");
            for (Cryptocurrency crypto : lista) {
                writer.write(String.format("ID: %d | Nome: %s | Ticker: %s | Preço: R$ %.2f | Volume: R$ %.2f\n",
                        crypto.getId(), crypto.getNome(), crypto.getSimbolo(), crypto.getPrecoAtual(), crypto.getVolumeNegociacao()));
            }
            System.out.println("Arquivo '" + caminho + "' gravado com sucesso.");
        }
    }

    private static void salvarUsuariosNoArquivo(String caminho, HashMap<Long, User> mapa) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminho))) {
            writer.write("--- CADASTRO INTERNO DE USUÁRIOS ---\n");
            for (Map.Entry<Long, User> entry : mapa.entrySet()) {
                User u = entry.getValue();
                writer.write(String.format("Chave(ID): %d -> Nome: %s | Email: %s | HashSenha: %s\n",
                        entry.getKey(), u.getNome(), u.getEmail(), u.getSenhaHash()));
            }
            System.out.println("Arquivo '" + caminho + "' gravado com sucesso.");
        }
    }

    private static void lerArquivoTexto(String caminho) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(caminho))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                System.out.println("  " + linha);
            }
        }
    }
}