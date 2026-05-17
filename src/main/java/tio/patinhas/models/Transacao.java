package tio.patinhas.models;

import tio.patinhas.models.enums.TransactionType;

import java.time.LocalDateTime;

public class Transacao extends Entity {
    private Long portfolioId;
    private Long cryptoId;

    private LocalDateTime dataHora;
    private TransactionType tipo;
    private double quantidade;
    private double precoUnitario;

    private Cryptocurrency cryptocurrency;

    public Transacao(Long id, Long portfolioId, Long cryptoId, Cryptocurrency cryptocurrency,
                     TransactionType tipo, double quantidade, double precoUnitario) {
        super(id);
        setPortfolioId(portfolioId);
        setCryptoId(cryptoId);

        if (tipo == null) {
            throw new IllegalArgumentException("Tipo inválido");
        }

        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade inválida");
        }

        if (precoUnitario <= 0) {
            throw new IllegalArgumentException("Preço inválido");
        }

        this.cryptocurrency = cryptocurrency;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.dataHora = LocalDateTime.now();
    }

    public double getValorTotal() {
        return quantidade * precoUnitario;
    }

    public double getValorAtual() {
        return quantidade * cryptocurrency.getPrecoAtual();
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        if (portfolioId == null) {
            throw new IllegalArgumentException("PortfolioId inválido");
        }
        this.portfolioId = portfolioId;
    }

    public Long getCryptoId() {
        return cryptoId;
    }

    public void setCryptoId(Long cryptoId) {
        if (cryptoId == null) {
            throw new IllegalArgumentException("CryptoId inválido");
        }
        this.cryptoId = cryptoId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public TransactionType getTipo() {
        return tipo;
    }

    public void setTipo(TransactionType tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo inválido");
        }
        this.tipo = tipo;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade inválida");
        }
        this.quantidade = quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(double precoUnitario) {
        if (precoUnitario <= 0) {
            throw new IllegalArgumentException("Preço inválido");
        }
        this.precoUnitario = precoUnitario;
    }

    public Cryptocurrency getCryptocurrency() {
        return cryptocurrency;
    }

    public void setCryptocurrency(Cryptocurrency cryptocurrency) {
        this.cryptocurrency = cryptocurrency;
    }
}
