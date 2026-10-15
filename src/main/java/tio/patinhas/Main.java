package tio.patinhas;

import tio.patinhas.dao.CryptocurrencyDAO;
import tio.patinhas.dao.UserDAO;
import tio.patinhas.dao.PortfolioDAO;
import tio.patinhas.dao.PriceHistoryDAO;
import tio.patinhas.dao.TransactionDAO;
import tio.patinhas.db.ConnectionFactory;
import tio.patinhas.models.FinancialAsset;
import tio.patinhas.models.Cryptocurrency;
import tio.patinhas.models.InvestmentSimulatorService;
import tio.patinhas.models.Portfolio;
import tio.patinhas.models.PortfolioPosition;
import tio.patinhas.models.PriceHistory;
import tio.patinhas.models.Transaction;
import tio.patinhas.models.enums.TransactionType;
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

        // Testes de integração com o banco de dados Oracle (FIAP)
        // Uma rodada de teste (INSERT, SELECT, UPDATE, DELETE) para cada tabela
        testarIntegracaoBancoDeDados();
        testarIntegracaoUsuario();
        testarIntegracaoCarteira();
        testarIntegracaoHistoricoPreco();
        testarIntegracaoTransacao();
    }

    // Roteiro de testes da integração da classe Cryptocurrency com o banco Oracle da FIAP.
    // Cobrindo INSERT, SELECT, UPDATE e DELETE.
    private static void testarIntegracaoBancoDeDados() {
        System.out.println("\n=== Teste de integração com o Banco Oracle (T_CRIPTOMOEDA) ===");

        if (!ConnectionFactory.testarConexao()) {
            System.out.println("Não foi possível conectar ao banco. " +
                    "Ajuste usuário/senha/URL em ConnectionFactory antes de rodar os testes.");

            return;
        }

        CryptocurrencyDAO dao = new CryptocurrencyDAO();

        try {
            // 1. INSERT
            Cryptocurrency solana = new Cryptocurrency(4L, "Solana", "SOL");
            solana.setPreco(600.0);
            solana.setVariacaoPercentual(4.2);
            solana.setVolumeNegociacao(9_500_000.0);

            dao.inserir(solana, "solana");
            Long idGerado = solana.getId();

            // 2. SELECT (todas + por id)
            System.out.println("\n-- Listagem após inserção --");
            dao.exibirTodas();

            System.out.println("\n-- Busca pelo ID gerado (" + idGerado + ") --");
            Cryptocurrency encontrada = dao.buscarPorId(idGerado);

            if (encontrada != null) {
                System.out.println("Encontrada: " + encontrada.descricaoResumida()
                        + " | Preço: " + encontrada.getPrecoAtual());
            }

            // 3. UPDATE
            System.out.println("\n-- Atualizando preço e volume da Solana --");
            solana.setPreco(650.0);
            solana.setVolumeNegociacao(10_200_000.0);
            dao.atualizar(idGerado, solana);

            System.out.println("\n-- Listagem após atualização --");
            dao.exibirTodas();

            // 4. DELETE
            System.out.println("\n-- Excluindo a criptomoeda de teste --");
            dao.excluir(idGerado);

            System.out.println("\n-- Listagem após exclusão --");
            dao.exibirTodas();

        } catch (RuntimeException e) {
            System.err.println("Erro durante o teste de integração com o banco: " + e.getMessage());
        }
    }

    // Roteiro de testes da integração da classe User com o banco Oracle da FIAP.
    // Cobrindo INSERT, SELECT, UPDATE e DELETE.
    private static void testarIntegracaoUsuario() {
        System.out.println("\n=== Teste de integração com o Banco Oracle (T_USUARIO) ===");

        if (!ConnectionFactory.testarConexao()) {
            System.out.println("Não foi possível conectar ao banco.");
            return;
        }

        UserDAO dao = new UserDAO();

        try {
            User usuarioTeste = new User(100L, "Huguinho Teste", "huguinho.teste@fiap.com.br", "hash_teste_001");
            dao.inserir(usuarioTeste);

            System.out.println("\n-- Listagem após inserção --");
            dao.exibirTodos();

            System.out.println("\n-- Busca pelo ID 100 --");
            User encontrado = dao.buscarPorId(100L);
            if (encontrado != null) {
                System.out.println("Encontrado: " + encontrado.getNome() + " | " + encontrado.getEmail());
            }

            System.out.println("\n-- Atualizando nome e email --");
            usuarioTeste.setNome("Huguinho Atualizado");
            usuarioTeste.setEmail("huguinho.atualizado@fiap.com.br");
            dao.atualizar(100L, usuarioTeste);

            System.out.println("\n-- Listagem após atualização --");
            dao.exibirTodos();

            System.out.println("\n-- Excluindo o usuário de teste --");
            dao.excluir(100L);

            System.out.println("\n-- Listagem após exclusão --");
            dao.exibirTodos();

        } catch (RuntimeException e) {
            System.err.println("Erro durante o teste de integração com o banco: " + e.getMessage());
        }
    }

    // Roteiro de testes da integração da classe Portfolio com o banco Oracle da FIAP.
    // Cobrindo INSERT, SELECT, UPDATE e DELETE.
    private static void testarIntegracaoCarteira() {
        System.out.println("\n=== Teste de integração com o Banco Oracle (T_CARTEIRA) ===");

        if (!ConnectionFactory.testarConexao()) {
            System.out.println("Não foi possível conectar ao banco.");
            return;
        }

        UserDAO userDao = new UserDAO();
        PortfolioDAO dao = new PortfolioDAO();

        try {
            User donoTeste = new User(101L, "Luizinho Teste", "luizinho.teste@fiap.com.br", "hash_teste_002");
            userDao.inserir(donoTeste);

            Portfolio carteiraTeste = new Portfolio(101L, 101L);
            carteiraTeste.setSaldoVirtual(5000.0);
            dao.inserir(carteiraTeste);

            System.out.println("\n-- Listagem após inserção --");
            dao.exibirTodas();

            System.out.println("\n-- Busca pelo ID 101 --");
            Portfolio encontrada = dao.buscarPorId(101L);
            if (encontrada != null) {
                System.out.println("Encontrada: saldo virtual R$ " + encontrada.getSaldoVirtual());
            }

            System.out.println("\n-- Atualizando saldo virtual --");
            carteiraTeste.setSaldoVirtual(7500.0);
            dao.atualizar(101L, carteiraTeste);

            System.out.println("\n-- Listagem após atualização --");
            dao.exibirTodas();

            System.out.println("\n-- Excluindo a carteira de teste --");
            dao.excluir(101L);
            userDao.excluir(101L);

            System.out.println("\n-- Listagem após exclusão --");
            dao.exibirTodas();

        } catch (RuntimeException e) {
            System.err.println("Erro durante o teste de integração com o banco: " + e.getMessage());
        }
    }

    // Roteiro de testes da integração da classe PriceHistory com o banco Oracle da FIAP.
    // Cobrindo INSERT, SELECT, UPDATE e DELETE.
    private static void testarIntegracaoHistoricoPreco() {
        System.out.println("\n=== Teste de integração com o Banco Oracle (T_HISTORICO_PRECO) ===");

        if (!ConnectionFactory.testarConexao()) {
            System.out.println("Não foi possível conectar ao banco.");
            return;
        }

        PriceHistoryDAO dao = new PriceHistoryDAO();

        try {
            PriceHistory historicoTeste = new PriceHistory(200L, 1L, 350000.0);
            dao.inserir(historicoTeste);

            System.out.println("\n-- Listagem após inserção --");
            dao.exibirTodos();

            System.out.println("\n-- Busca pelo ID 200 --");
            PriceHistory encontrado = dao.buscarPorId(200L);
            if (encontrado != null) {
                System.out.println("Encontrado: preço " + encontrado.getPreco());
            }

            System.out.println("\n-- Atualizando preço registrado --");
            historicoTeste.setPreco(355000.0);
            dao.atualizar(200L, historicoTeste);

            System.out.println("\n-- Listagem após atualização --");
            dao.exibirTodos();

            System.out.println("\n-- Excluindo o histórico de teste --");
            dao.excluir(200L);

            System.out.println("\n-- Listagem após exclusão --");
            dao.exibirTodos();

        } catch (RuntimeException e) {
            System.err.println("Erro durante o teste de integração com o banco: " + e.getMessage());
        }
    }

    // Roteiro de testes da integração da classe Transaction com o banco Oracle da FIAP.
    // Cobrindo INSERT, SELECT, UPDATE e DELETE.
    private static void testarIntegracaoTransacao() {
        System.out.println("\n=== Teste de integração com o Banco Oracle (T_TRANSACAO) ===");

        if (!ConnectionFactory.testarConexao()) {
            System.out.println("Não foi possível conectar ao banco.");
            return;
        }

        TransactionDAO dao = new TransactionDAO();

        try {
            Cryptocurrency cryptoReferencia = new Cryptocurrency(1L, "Bitcoin", "BTC");
            Transaction transacaoTeste = new Transaction(
                    300L, 1L, 1L, cryptoReferencia, TransactionType.COMPRA, 0.02, 345000.50);
            dao.inserir(transacaoTeste);

            System.out.println("\n-- Listagem após inserção --");
            dao.exibirTodas();

            System.out.println("\n-- Busca pelo ID 300 --");
            Transaction encontrada = dao.buscarPorId(300L);
            if (encontrada != null) {
                System.out.println("Encontrada: tipo " + encontrada.getTipo() + " | valor total: " + encontrada.getValorTotal());
            }

            System.out.println("\n-- Atualizando quantidade da transação --");
            transacaoTeste.setQuantidade(0.03);
            dao.atualizar(300L, transacaoTeste);

            System.out.println("\n-- Listagem após atualização --");
            dao.exibirTodas();

            System.out.println("\n-- Excluindo a transação de teste --");
            dao.excluir(300L);

            System.out.println("\n-- Listagem após exclusão --");
            dao.exibirTodas();

        } catch (RuntimeException e) {
            System.err.println("Erro durante o teste de integração com o banco: " + e.getMessage());
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