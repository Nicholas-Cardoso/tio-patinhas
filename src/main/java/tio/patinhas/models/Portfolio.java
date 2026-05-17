package tio.patinhas.models;

import java.util.ArrayList;
import java.util.List;

public class Portfolio extends Entity {
    private Long userId;

    private Double saldoVirtual = 0.0;
    private List<Transacao> transacoes = new ArrayList<>();
    private List<PortfolioPosition> posicoes = new ArrayList<>();

    public Portfolio(Long id, Long userId) {
        super(id);
        setUserId(userId);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId inválido");
        }
        this.userId = userId;
    }

    public void creditar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor inválido");
        }

        saldoVirtual += valor;
    }

    public void debitar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor inválido");
        }

        if (valor > saldoVirtual) {
            throw new IllegalStateException("Saldo insuficiente");
        }

        saldoVirtual -= valor;
    }

    public List<Transacao> listarTransacoes() {
        return List.copyOf(transacoes);
    }

    public List<PortfolioPosition> getPosicoes() {
        return List.copyOf(posicoes);
    }

    public PortfolioPosition buscarPosicao(Long cryptoId) {
        return posicoes.stream()
                .filter(p -> p.getCryptoId().equals(cryptoId))
                .findFirst()
                .orElse(null);
    }

    public void adicionarPosicao(PortfolioPosition posicao) {
        if (posicao == null) {
            throw new IllegalArgumentException("Posição inválida");
        }
        if (!posicao.getPortfolioId().equals(getId())) {
            throw new IllegalArgumentException("Posição não pertence a esta carteira");
        }
        posicoes.add(posicao);
    }

    public Double getValorTotalInvestido() {
        return transacoes.stream().mapToDouble(Transacao::getValorTotal).sum();
    }

    public Double getValorAtual() {
        return transacoes.stream().mapToDouble(Transacao::getValorAtual).sum();
    }

    public Double getLucroPrejuizo() {
        return getValorAtual() - getValorTotalInvestido();
    }

    public Double getSaldoVirtual() {
        return saldoVirtual;
    }

    public void setSaldoVirtual(Double saldoVirtual) {
        this.saldoVirtual = saldoVirtual;
    }

    public List<Transacao> getTransacoes() {
        return List.copyOf(transacoes);
    }

    public void setTransacao(Transacao t) {
        if (t == null) {
            throw new IllegalArgumentException("Transação inválida");
        }

        transacoes.add(t);
    }
}
