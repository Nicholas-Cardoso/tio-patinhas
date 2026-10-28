package tio.patinhas;

import tio.patinhas.dao.CryptocurrencyDAO;
import tio.patinhas.dao.PortfolioDAO;
import tio.patinhas.dao.PriceHistoryDAO;
import tio.patinhas.dao.TransactionDAO;
import tio.patinhas.dao.UserDAO;
import tio.patinhas.db.ConnectionFactory;
import tio.patinhas.models.Cryptocurrency;
import tio.patinhas.models.Portfolio;
import tio.patinhas.models.PriceHistory;
import tio.patinhas.models.Transaction;
import tio.patinhas.models.User;
import tio.patinhas.models.enums.TransactionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Programa principal do Projeto Tio Patinhas.
 * Menu principal -> submenu de cada módulo (Inclusão, Alteração, Exclusão,
 * Exibir todos, Exibir um). Os dados são lidos/gravados no banco Oracle via DAOs;
 * as listagens são carregadas em ArrayList antes de serem exibidas.
 */
public class Main {

    private static final Scanner SC = new Scanner(System.in);
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private static final CryptocurrencyDAO cryptoDao = new CryptocurrencyDAO();
    private static final UserDAO userDao = new UserDAO();
    private static final PortfolioDAO portfolioDao = new PortfolioDAO();
    private static final PriceHistoryDAO historyDao = new PriceHistoryDAO();
    private static final TransactionDAO transactionDao = new TransactionDAO();

    public static void main(String[] args) {
        System.out.println("=== Projeto Tio Patinhas ===");

        if (!ConnectionFactory.testarConexao()) {
            System.out.println("Não foi possível conectar ao banco. " +
                    "Ajuste usuário/senha/URL em ConnectionFactory e tente novamente.");
            return;
        }
        System.out.println("Conexão com o banco estabelecida.");

        int op;
        do {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1 - Criptomoedas");
            System.out.println("2 - Usuários");
            System.out.println("3 - Carteiras");
            System.out.println("4 - Histórico de Preços");
            System.out.println("5 - Transações");
            System.out.println("0 - Sair");
            op = lerOpcao(0, 5);

            switch (op) {
                case 1 -> menuCripto();
                case 2 -> menuUsuario();
                case 3 -> menuCarteira();
                case 4 -> menuHistorico();
                case 5 -> menuTransacao();
                default -> { }
            }
        } while (op != 0);

        System.out.println("Encerrando o programa. Até logo!");
    }

    // ------------------------------------------------------------------
    // Submenu genérico
    // ------------------------------------------------------------------
    private static int submenu(String titulo) {
        System.out.println("\n--- " + titulo + " ---");
        System.out.println("1 - Incluir");
        System.out.println("2 - Alterar");
        System.out.println("3 - Excluir");
        System.out.println("4 - Exibir todos");
        System.out.println("5 - Exibir um registro (por ID)");
        System.out.println("0 - Voltar");
        return lerOpcao(0, 5);
    }

    // ------------------------------------------------------------------
    // CRIPTOMOEDAS
    // ------------------------------------------------------------------
    private static void menuCripto() {
        int op;
        do {
            op = submenu("CRIPTOMOEDAS");
            try {
                switch (op) {
                    case 1 -> incluirCripto();
                    case 2 -> alterarCripto();
                    case 3 -> excluirCripto();
                    case 4 -> exibirTodasCripto();
                    case 5 -> exibirUmaCripto();
                    default -> { }
                }
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (op != 0);
    }

    private static void incluirCripto() {
        Long id = lerId("ID da criptomoeda");
        if (cryptoDao.buscarPorId(id) != null) {
            System.out.println("Já existe uma criptomoeda com o ID " + id + ".");
            return;
        }
        String idApi = lerTexto("ID na API de mercado (ex.: bitcoin)", null,
                s -> s.matches("[a-z0-9-]{2,50}"),
                "Use 2 a 50 caracteres: letras minúsculas, números ou hífen.");
        String nome = lerTexto("Nome", null, s -> s.length() >= 2 && s.length() <= 50,
                "O nome deve ter entre 2 e 50 caracteres.");
        String simbolo = lerTexto("Símbolo (ex.: BTC)", null, s -> s.matches("[A-Za-z0-9]{2,10}"),
                "O símbolo deve ter 2 a 10 letras/números.").toUpperCase();
        double preco = lerDouble("Preço atual (R$)", null, v -> v > 0, "O preço deve ser maior que zero.");
        double variacao = lerDouble("Variação percentual (%)", null, v -> v >= -100,
                "A variação não pode ser menor que -100%.");
        double volume = lerDouble("Volume de negociação (R$)", null, v -> v >= 0,
                "O volume não pode ser negativo.");

        Cryptocurrency c = new Cryptocurrency(id, nome, simbolo);
        c.setPreco(preco);
        c.setVariacaoPercentual(variacao);
        c.setVolumeNegociacao(volume);
        cryptoDao.inserir(c, idApi);
    }

    private static void alterarCripto() {
        Long id = lerId("ID da criptomoeda a alterar");
        Cryptocurrency atual = cryptoDao.buscarPorId(id);
        if (atual == null) {
            System.out.println("Criptomoeda não encontrada.");
            return;
        }
        System.out.println("(Pressione ENTER para manter o valor atual)");
        String nome = lerTexto("Nome", atual.getNome(), s -> s.length() >= 2 && s.length() <= 50,
                "O nome deve ter entre 2 e 50 caracteres.");
        String simbolo = lerTexto("Símbolo", atual.getSimbolo(), s -> s.matches("[A-Za-z0-9]{2,10}"),
                "O símbolo deve ter 2 a 10 letras/números.").toUpperCase();
        double preco = lerDouble("Preço atual (R$)", atual.getPrecoAtual(), v -> v > 0,
                "O preço deve ser maior que zero.");
        double variacao = lerDouble("Variação percentual (%)", atual.getVariacaoPercentual(), v -> v >= -100,
                "A variação não pode ser menor que -100%.");
        double volume = lerDouble("Volume de negociação (R$)", atual.getVolumeNegociacao(), v -> v >= 0,
                "O volume não pode ser negativo.");

        Cryptocurrency nova = new Cryptocurrency(id, nome, simbolo);
        nova.setPreco(preco);
        nova.setVariacaoPercentual(variacao);
        nova.setVolumeNegociacao(volume);
        cryptoDao.atualizar(id, nova);
    }

    private static void excluirCripto() {
        Long id = lerId("ID da criptomoeda a excluir");
        if (cryptoDao.buscarPorId(id) == null) {
            System.out.println("Criptomoeda não encontrada.");
            return;
        }
        if (confirmar("Confirma a exclusão?")) {
            cryptoDao.excluir(id);
        }
    }

    private static void exibirTodasCripto() {
        ArrayList<Cryptocurrency> lista = new ArrayList<>(cryptoDao.listarTodas());
        if (lista.isEmpty()) {
            System.out.println("Nenhuma criptomoeda cadastrada.");
            return;
        }
        System.out.println("--- Criptomoedas (" + lista.size() + ") ---");
        for (Cryptocurrency c : lista) {
            imprimirCripto(c);
        }
    }

    private static void exibirUmaCripto() {
        Cryptocurrency c = cryptoDao.buscarPorId(lerId("ID da criptomoeda"));
        if (c == null) {
            System.out.println("Criptomoeda não encontrada.");
        } else {
            imprimirCripto(c);
        }
    }

    private static void imprimirCripto(Cryptocurrency c) {
        System.out.printf("ID: %d | %s (%s) | Preço: R$ %.2f | Variação: %.2f%% | Volume: R$ %.2f%n",
                c.getId(), c.getNome(), c.getSimbolo(), c.getPrecoAtual(),
                c.getVariacaoPercentual(), c.getVolumeNegociacao());
    }

    // ------------------------------------------------------------------
    // USUÁRIOS
    // ------------------------------------------------------------------
    private static void menuUsuario() {
        int op;
        do {
            op = submenu("USUÁRIOS");
            try {
                switch (op) {
                    case 1 -> incluirUsuario();
                    case 2 -> alterarUsuario();
                    case 3 -> excluirUsuario();
                    case 4 -> exibirTodosUsuarios();
                    case 5 -> exibirUmUsuario();
                    default -> { }
                }
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (op != 0);
    }

    private static void incluirUsuario() {
        Long id = lerId("ID do usuário");
        if (userDao.buscarPorId(id) != null) {
            System.out.println("Já existe um usuário com o ID " + id + ".");
            return;
        }
        String nome = lerTexto("Nome", null, s -> s.length() >= 3 && s.length() <= 100,
                "O nome deve ter entre 3 e 100 caracteres.");
        String email = lerTexto("E-mail", null, s -> EMAIL.matcher(s).matches() && s.length() <= 100,
                "E-mail inválido.");
        if (emailEmUso(email, null)) {
            System.out.println("Este e-mail já está cadastrado para outro usuário.");
            return;
        }
        String senha = lerTexto("Senha (mín. 6 caracteres)", null, s -> s.length() >= 6,
                "A senha deve ter pelo menos 6 caracteres.");

        userDao.inserir(new User(id, nome, email, hashSenha(senha)));
    }

    private static void alterarUsuario() {
        Long id = lerId("ID do usuário a alterar");
        User atual = userDao.buscarPorId(id);
        if (atual == null) {
            System.out.println("Usuário não encontrado.");
            return;
        }
        System.out.println("(Pressione ENTER para manter o valor atual)");
        String nome = lerTexto("Nome", atual.getNome(), s -> s.length() >= 3 && s.length() <= 100,
                "O nome deve ter entre 3 e 100 caracteres.");
        String email = lerTexto("E-mail", atual.getEmail(), s -> EMAIL.matcher(s).matches() && s.length() <= 100,
                "E-mail inválido.");
        if (emailEmUso(email, id)) {
            System.out.println("Este e-mail já está cadastrado para outro usuário.");
            return;
        }
        String senha = lerTexto("Nova senha (ENTER para manter)", "", s -> s.length() >= 6,
                "A senha deve ter pelo menos 6 caracteres.");
        String hash = senha.isEmpty() ? atual.getSenhaHash() : hashSenha(senha);

        userDao.atualizar(id, new User(id, nome, email, hash));
    }

    private static void excluirUsuario() {
        Long id = lerId("ID do usuário a excluir");
        if (userDao.buscarPorId(id) == null) {
            System.out.println("Usuário não encontrado.");
            return;
        }
        if (confirmar("Confirma a exclusão?")) {
            userDao.excluir(id);
        }
    }

    private static void exibirTodosUsuarios() {
        ArrayList<User> lista = new ArrayList<>(userDao.listarTodos());
        if (lista.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }
        System.out.println("--- Usuários (" + lista.size() + ") ---");
        for (User u : lista) {
            System.out.printf("ID: %d | %s | %s%n", u.getId(), u.getNome(), u.getEmail());
        }
    }

    private static void exibirUmUsuario() {
        User u = userDao.buscarPorId(lerId("ID do usuário"));
        if (u == null) {
            System.out.println("Usuário não encontrado.");
        } else {
            System.out.printf("ID: %d | %s | %s%n", u.getId(), u.getNome(), u.getEmail());
        }
    }

    private static boolean emailEmUso(String email, Long idIgnorar) {
        for (User u : userDao.listarTodos()) {
            if (u.getEmail().equalsIgnoreCase(email) && !u.getId().equals(idIgnorar)) {
                return true;
            }
        }
        return false;
    }

    /** Gera o hash SHA-256 da senha (a senha em texto puro nunca vai para o banco). */
    private static String hashSenha(String senha) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(senha.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo de hash indisponível.", e);
        }
    }

    // ------------------------------------------------------------------
    // CARTEIRAS
    // ------------------------------------------------------------------
    private static void menuCarteira() {
        int op;
        do {
            op = submenu("CARTEIRAS");
            try {
                switch (op) {
                    case 1 -> incluirCarteira();
                    case 2 -> alterarCarteira();
                    case 3 -> excluirCarteira();
                    case 4 -> exibirTodasCarteiras();
                    case 5 -> exibirUmaCarteira();
                    default -> { }
                }
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (op != 0);
    }

    private static void incluirCarteira() {
        Long id = lerId("ID da carteira");
        if (portfolioDao.buscarPorId(id) != null) {
            System.out.println("Já existe uma carteira com o ID " + id + ".");
            return;
        }
        Long userId = lerId("ID do usuário dono da carteira");
        if (userDao.buscarPorId(userId) == null) {
            System.out.println("Usuário não encontrado. Cadastre o usuário primeiro.");
            return;
        }
        for (Portfolio p : portfolioDao.listarTodas()) {
            if (p.getUserId().equals(userId)) {
                System.out.println("Este usuário já possui a carteira de ID " + p.getId() + ".");
                return;
            }
        }
        double saldo = lerDouble("Saldo virtual inicial (R$)", null, v -> v >= 0,
                "O saldo não pode ser negativo.");

        Portfolio p = new Portfolio(id, userId);
        p.setSaldoVirtual(saldo);
        portfolioDao.inserir(p);
    }

    private static void alterarCarteira() {
        Long id = lerId("ID da carteira a alterar");
        Portfolio p = portfolioDao.buscarPorId(id);
        if (p == null) {
            System.out.println("Carteira não encontrada.");
            return;
        }
        System.out.println("(Pressione ENTER para manter o valor atual)");
        double saldo = lerDouble("Saldo virtual (R$)", p.getSaldoVirtual(), v -> v >= 0,
                "O saldo não pode ser negativo.");
        p.setSaldoVirtual(saldo);
        portfolioDao.atualizar(id, p);
    }

    private static void excluirCarteira() {
        Long id = lerId("ID da carteira a excluir");
        if (portfolioDao.buscarPorId(id) == null) {
            System.out.println("Carteira não encontrada.");
            return;
        }
        if (confirmar("Confirma a exclusão?")) {
            portfolioDao.excluir(id);
        }
    }

    private static void exibirTodasCarteiras() {
        ArrayList<Portfolio> lista = new ArrayList<>(portfolioDao.listarTodas());
        if (lista.isEmpty()) {
            System.out.println("Nenhuma carteira cadastrada.");
            return;
        }
        System.out.println("--- Carteiras (" + lista.size() + ") ---");
        for (Portfolio p : lista) {
            imprimirCarteira(p);
        }
    }

    private static void exibirUmaCarteira() {
        Portfolio p = portfolioDao.buscarPorId(lerId("ID da carteira"));
        if (p == null) {
            System.out.println("Carteira não encontrada.");
        } else {
            imprimirCarteira(p);
        }
    }

    private static void imprimirCarteira(Portfolio p) {
        System.out.printf("ID: %d | userId: %d | Saldo virtual: R$ %.2f%n",
                p.getId(), p.getUserId(), p.getSaldoVirtual());
    }

    // ------------------------------------------------------------------
    // HISTÓRICO DE PREÇOS
    // ------------------------------------------------------------------
    private static void menuHistorico() {
        int op;
        do {
            op = submenu("HISTÓRICO DE PREÇOS");
            try {
                switch (op) {
                    case 1 -> incluirHistorico();
                    case 2 -> alterarHistorico();
                    case 3 -> excluirHistorico();
                    case 4 -> exibirTodosHistoricos();
                    case 5 -> exibirUmHistorico();
                    default -> { }
                }
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (op != 0);
    }

    private static void incluirHistorico() {
        Long id = lerId("ID do registro de histórico");
        if (historyDao.buscarPorId(id) != null) {
            System.out.println("Já existe um histórico com o ID " + id + ".");
            return;
        }
        Long cryptoId = lerId("ID da criptomoeda");
        if (cryptoDao.buscarPorId(cryptoId) == null) {
            System.out.println("Criptomoeda não encontrada.");
            return;
        }
        double preco = lerDouble("Preço registrado (R$)", null, v -> v > 0,
                "O preço deve ser maior que zero.");
        historyDao.inserir(new PriceHistory(id, cryptoId, preco));
    }

    private static void alterarHistorico() {
        Long id = lerId("ID do histórico a alterar");
        PriceHistory atual = historyDao.buscarPorId(id);
        if (atual == null) {
            System.out.println("Histórico não encontrado.");
            return;
        }
        System.out.println("(Pressione ENTER para manter o valor atual)");
        double preco = lerDouble("Preço registrado (R$)", atual.getPreco(), v -> v > 0,
                "O preço deve ser maior que zero.");

        PriceHistory novo = new PriceHistory(id, atual.getCryptoId(), preco);
        novo.setDataHora(atual.getDataHora());
        historyDao.atualizar(id, novo);
    }

    private static void excluirHistorico() {
        Long id = lerId("ID do histórico a excluir");
        if (historyDao.buscarPorId(id) == null) {
            System.out.println("Histórico não encontrado.");
            return;
        }
        if (confirmar("Confirma a exclusão?")) {
            historyDao.excluir(id);
        }
    }

    private static void exibirTodosHistoricos() {
        ArrayList<PriceHistory> lista = new ArrayList<>(historyDao.listarTodos());
        if (lista.isEmpty()) {
            System.out.println("Nenhum histórico cadastrado.");
            return;
        }
        System.out.println("--- Histórico de preços (" + lista.size() + ") ---");
        for (PriceHistory h : lista) {
            imprimirHistorico(h);
        }
    }

    private static void exibirUmHistorico() {
        PriceHistory h = historyDao.buscarPorId(lerId("ID do histórico"));
        if (h == null) {
            System.out.println("Histórico não encontrado.");
        } else {
            imprimirHistorico(h);
        }
    }

    private static void imprimirHistorico(PriceHistory h) {
        System.out.printf("ID: %d | cryptoId: %d | Data: %s | Preço: R$ %.2f%n",
                h.getId(), h.getCryptoId(), h.getDataHora(), h.getPreco());
    }

    // ------------------------------------------------------------------
    // TRANSAÇÕES
    // ------------------------------------------------------------------
    private static void menuTransacao() {
        int op;
        do {
            op = submenu("TRANSAÇÕES");
            try {
                switch (op) {
                    case 1 -> incluirTransacao();
                    case 2 -> alterarTransacao();
                    case 3 -> excluirTransacao();
                    case 4 -> exibirTodasTransacoes();
                    case 5 -> exibirUmaTransacao();
                    default -> { }
                }
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (op != 0);
    }

    private static void incluirTransacao() {
        Long id = lerId("ID da transação");
        if (transactionDao.buscarPorId(id) != null) {
            System.out.println("Já existe uma transação com o ID " + id + ".");
            return;
        }
        Long carteiraId = lerId("ID da carteira");
        if (portfolioDao.buscarPorId(carteiraId) == null) {
            System.out.println("Carteira não encontrada.");
            return;
        }
        Long cryptoId = lerId("ID da criptomoeda");
        Cryptocurrency crypto = cryptoDao.buscarPorId(cryptoId);
        if (crypto == null) {
            System.out.println("Criptomoeda não encontrada.");
            return;
        }
        TransactionType tipo = lerTipo(null);
        double qtd = lerDouble("Quantidade", null, v -> v > 0, "A quantidade deve ser maior que zero.");
        double precoUnit = lerDouble("Preço unitário (R$) [ENTER = preço atual]", crypto.getPrecoAtual(),
                v -> v > 0, "O preço deve ser maior que zero.");

        Transaction t = new Transaction(id, carteiraId, cryptoId, crypto, tipo, qtd, precoUnit);
        transactionDao.inserir(t);
    }

    private static void alterarTransacao() {
        Long id = lerId("ID da transação a alterar");
        Transaction atual = transactionDao.buscarPorId(id);
        if (atual == null) {
            System.out.println("Transação não encontrada.");
            return;
        }
        System.out.println("(Pressione ENTER para manter o valor atual)");
        TransactionType tipo = lerTipo(atual.getTipo());
        double qtd = lerDouble("Quantidade", atual.getQuantidade(), v -> v > 0,
                "A quantidade deve ser maior que zero.");
        double precoUnit = lerDouble("Preço unitário (R$)", atual.getPrecoUnitario(), v -> v > 0,
                "O preço deve ser maior que zero.");

        Cryptocurrency ref = new Cryptocurrency(atual.getCryptoId(), "N/D", "N/D");
        Transaction nova = new Transaction(id, atual.getPortfolioId(), atual.getCryptoId(),
                ref, tipo, qtd, precoUnit);
        transactionDao.atualizar(id, nova);
    }

    private static void excluirTransacao() {
        Long id = lerId("ID da transação a excluir");
        if (transactionDao.buscarPorId(id) == null) {
            System.out.println("Transação não encontrada.");
            return;
        }
        if (confirmar("Confirma a exclusão?")) {
            transactionDao.excluir(id);
        }
    }

    private static void exibirTodasTransacoes() {
        ArrayList<Transaction> lista = new ArrayList<>(transactionDao.listarTodas());
        if (lista.isEmpty()) {
            System.out.println("Nenhuma transação cadastrada.");
            return;
        }
        System.out.println("--- Transações (" + lista.size() + ") ---");
        for (Transaction t : lista) {
            imprimirTransacao(t);
        }
    }

    private static void exibirUmaTransacao() {
        Transaction t = transactionDao.buscarPorId(lerId("ID da transação"));
        if (t == null) {
            System.out.println("Transação não encontrada.");
        } else {
            imprimirTransacao(t);
        }
    }

    private static void imprimirTransacao(Transaction t) {
        System.out.printf("ID: %d | carteiraId: %d | cryptoId: %d | %s | qtd: %.8f | preço unit.: R$ %.2f | total: R$ %.2f%n",
                t.getId(), t.getPortfolioId(), t.getCryptoId(), t.getTipo(),
                t.getQuantidade(), t.getPrecoUnitario(), t.getValorTotal());
    }

    private static TransactionType lerTipo(TransactionType atual) {
        TransactionType[] valores = TransactionType.values();
        while (true) {
            System.out.print("Tipo " + Arrays.toString(valores)
                    + (atual != null ? " [" + atual + "]" : "") + ": ");
            String s = SC.nextLine().trim();
            if (s.isEmpty() && atual != null) {
                return atual;
            }
            try {
                return TransactionType.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Tipo inválido. Opções: " + Arrays.toString(valores));
            }
        }
    }

    // ------------------------------------------------------------------
    // Utilitários de leitura com validação
    // ------------------------------------------------------------------
    private static int lerOpcao(int min, int max) {
        while (true) {
            System.out.print("Escolha uma opção: ");
            String s = SC.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) {
                    return v;
                }
            } catch (NumberFormatException ignored) {
                // cai na mensagem abaixo
            }
            System.out.println("Opção inválida. Digite um número de " + min + " a " + max + ".");
        }
    }

    private static Long lerId(String msg) {
        while (true) {
            System.out.print(msg + ": ");
            String s = SC.nextLine().trim();
            try {
                long v = Long.parseLong(s);
                if (v > 0) {
                    return v;
                }
            } catch (NumberFormatException ignored) {
                // cai na mensagem abaixo
            }
            System.out.println("ID inválido. Informe um número inteiro maior que zero.");
        }
    }

    /**
     * Lê um texto validado. Se {@code atual} não for nulo, ENTER mantém o valor atual.
     */
    private static String lerTexto(String msg, String atual, Predicate<String> valido, String erro) {
        while (true) {
            System.out.print(msg + (atual != null && !atual.isEmpty() ? " [" + atual + "]" : "") + ": ");
            String s = SC.nextLine().trim();
            if (s.isEmpty() && atual != null) {
                return atual;
            }
            if (!s.isEmpty() && valido.test(s)) {
                return s;
            }
            System.out.println(s.isEmpty() ? "Campo obrigatório." : erro);
        }
    }

    /**
     * Lê um número decimal validado (aceita vírgula ou ponto).
     * Se {@code atual} não for nulo, ENTER mantém o valor atual.
     */
    private static double lerDouble(String msg, Double atual, Predicate<Double> valido, String erro) {
        while (true) {
            System.out.print(msg + (atual != null ? " [" + atual + "]" : "") + ": ");
            String s = SC.nextLine().trim().replace(',', '.');
            if (s.isEmpty() && atual != null) {
                return atual;
            }
            try {
                double v = Double.parseDouble(s);
                if (!Double.isNaN(v) && !Double.isInfinite(v) && valido.test(v)) {
                    return v;
                }
                System.out.println(erro);
            } catch (NumberFormatException e) {
                System.out.println(s.isEmpty() ? "Campo obrigatório." : "Valor numérico inválido.");
            }
        }
    }

    private static boolean confirmar(String msg) {
        while (true) {
            System.out.print(msg + " (S/N): ");
            String s = SC.nextLine().trim().toUpperCase();
            if (s.equals("S")) {
                return true;
            }
            if (s.equals("N")) {
                System.out.println("Operação cancelada.");
                return false;
            }
            System.out.println("Responda com S ou N.");
        }
    }
}