package tio.patinhas.models;

import tio.patinhas.models.enums.TransactionType;

public class InvestmentSimulatorService {
    public void comprar(Portfolio portfolio, Cryptocurrency crypto, double quantidade) {
        validarQuantidade(quantidade);

        double precoAtual = crypto.getPrecoAtual();
        double custoTotal = precoAtual * quantidade;

        portfolio.debitar(custoTotal);

        Transacao transacao = new Transacao(
                System.currentTimeMillis(),
                portfolio.getId(),
                crypto.getId(),
                crypto,
                TransactionType.COMPRA,
                quantidade,
                precoAtual
        );

        portfolio.setTransacao(transacao);
        atualizarPosicao(portfolio, crypto, quantidade, true);

        System.out.println("Compra realizada: " +
                quantidade + " " +
                crypto.getSimbolo()
        );
    }

    public void vender(Portfolio portfolio, Cryptocurrency crypto, double quantidade) {
        validarQuantidade(quantidade);

        double precoAtual = crypto.getPrecoAtual();
        double valorVenda = precoAtual * quantidade;

        portfolio.creditar(valorVenda);

        Transacao transacao = new Transacao(
                System.currentTimeMillis(),
                portfolio.getId(),
                crypto.getId(),
                crypto,
                TransactionType.VENDA,
                quantidade,
                precoAtual
        );

        portfolio.setTransacao(transacao);
        atualizarPosicao(portfolio, crypto, quantidade, false);

        System.out.println(
                "Venda realizada: "
                        + quantidade
                        + " "
                        + crypto.getSimbolo()
        );
    }

    private void atualizarPosicao(Portfolio portfolio, Cryptocurrency crypto, double quantidade, boolean compra) {
        PortfolioPosition posicao = portfolio.buscarPosicao(crypto.getId());

        if (compra) {
            if (posicao == null) {
                posicao = new PortfolioPosition(
                        System.nanoTime(),
                        portfolio.getId(),
                        crypto.getId(),
                        quantidade
                );

                portfolio.adicionarPosicao(posicao);
            } else {
                posicao.adicionarQuantidade(quantidade);
            }
        } else {
            if (posicao == null) {
                throw new IllegalStateException("Não há posição para vender");
            }

            posicao.removerQuantidade(quantidade);
        }
    }

    private void validarQuantidade(double quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade inválida");
        }
    }
}
