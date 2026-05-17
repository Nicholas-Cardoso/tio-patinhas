package tio.patinhas.models;

import java.util.ArrayList;
import java.util.List;

public class Cryptocurrency extends FinancialAsset {
    private String nome;
    private String simbolo;
    private double precoAtual;
    private double variacaoPercentual;
    private double volumeNegociacao;

    private List<PriceHistory> historico = new ArrayList<>();

    public Cryptocurrency(Long id, String nome, String simbolo) {
        super(id);
        setNome(nome);
        setSimbolo(simbolo);
    }

    @Override
    public double calcularValor(double quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade inválida");
        }
        return precoAtual * quantidade;
    }

    @Override
    public String descricaoResumida() {
        return simbolo + " - " + nome;
    }

    public List<PriceHistory> getHistorico() {
        return historico;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome inválido");
        }
        this.nome = nome;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        if (simbolo == null || simbolo.isBlank()) {
            throw new IllegalArgumentException("Símbolo inválido");
        }
        this.simbolo = simbolo;
    }

    public Double getPrecoAtual() {
        return precoAtual;
    }

    public void setPrecoAtual(double precoAtual) {
        setPreco(precoAtual);
    }

    public Double getVariacaoPercentual() {
        return variacaoPercentual;
    }

    public void setVariacaoPercentual(double variacaoPercentual) {
        this.variacaoPercentual = variacaoPercentual;
    }

    public Double getVolumeNegociacao() {
        return volumeNegociacao;
    }

    public void setVolumeNegociacao(double novoVolume) {
        if (novoVolume < 0) {
            throw new IllegalArgumentException("Volume não pode ser negativo");
        }

        this.volumeNegociacao = novoVolume;
    }

    public void setPreco(double novoPreco) {
        if (novoPreco <= 0) {
            throw new IllegalArgumentException("Preço inválido");
        }

        if (this.precoAtual > 0) {
            this.variacaoPercentual = ((novoPreco - this.precoAtual) / this.precoAtual) * 100;
        }

        this.precoAtual = novoPreco;

        PriceHistory hp = new PriceHistory(System.currentTimeMillis(), getId(), novoPreco);
        historico.add(hp);
    }
}
