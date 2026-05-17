package tio.patinhas.models;

public class PortfolioPosition extends Entity {
    private Long portfolioId;
    private Long cryptoId;
    private double quantidade;

    public PortfolioPosition(Long id, Long portfolioId, Long cryptoId, double quantidade) {
        super(id);
        setPortfolioId(portfolioId);
        setCryptoId(cryptoId);
        setQuantidade(quantidade);
    }

    // Getter
    public Long getPortfolioId() {
        return portfolioId;
    }

    // Setter
    public void setPortfolioId(Long portfolioId) {
        if (portfolioId == null) {
            throw new IllegalArgumentException("PortfolioId inválido");
        }
        this.portfolioId = portfolioId;
    }

    // Getter
    public Long getCryptoId() {
        return cryptoId;
    }

    // Setter
    public void setCryptoId(Long cryptoId) {
        if (cryptoId == null) {
            throw new IllegalArgumentException("CryptoId inválido");
        }
        this.cryptoId = cryptoId;
    }

    // Getter
    public double getQuantidade() {
        return quantidade;
    }

    // Setter
    public void setQuantidade(double quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade inválida");
        }
        this.quantidade = quantidade;
    }

    public void adicionarQuantidade(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor inválido");
        }
        this.quantidade += valor;
    }

    public void removerQuantidade(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor inválido");
        }
        if (valor > quantidade) {
            throw new IllegalStateException("Quantidade insuficiente na posição");
        }
        this.quantidade -= valor;
    }
}
