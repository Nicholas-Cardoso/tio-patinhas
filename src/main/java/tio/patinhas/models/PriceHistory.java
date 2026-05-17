package tio.patinhas.models;

import java.util.Date;

public class PriceHistory extends Entity {
    private Long cryptoId;

    private Date dataHora;
    private Double preco;

    public PriceHistory(Long id, Long cryptoId, Double preco) {
        super(id);
        setCryptoId(cryptoId);
        setPreco(preco);
        this.dataHora = new Date();
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

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        if (preco == null || preco <= 0) {
            throw new IllegalArgumentException("Preço inválido");
        }
        this.preco = preco;
        this.dataHora = new Date();
    }
}
