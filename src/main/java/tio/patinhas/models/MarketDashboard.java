package tio.patinhas.models;

import java.util.List;

public class MarketDashboard {
    public void exibirResumo(Cryptocurrency crypto) {
        System.out.println("--- Resumo ---");
        System.out.println(crypto.descricaoResumida());
        System.out.println("Preço: " + crypto.getPrecoAtual());
        System.out.println("Variação: " + crypto.getVariacaoPercentual() + "%");
        System.out.println("Volume: " + crypto.getVolumeNegociacao());
    }

    public void exibirResumo(List<Cryptocurrency> criptos) {
        System.out.println("--- Resumo do mercado ---");
        criptos.forEach(this::exibirResumo);
    }

    public void exibirPrecoAtual(List<Cryptocurrency> criptos) {
        criptos.forEach(c ->
                System.out.println(c.getPrecoAtual())
        );
    }

    public void exibirVariacao(List<Cryptocurrency> criptos) {
        criptos.forEach(c ->
                System.out.println(c.getVariacaoPercentual())
        );
    }

    public void exibirHistorico(List<Cryptocurrency> criptos) {
        criptos.forEach(c ->
                System.out.println(c.getHistorico())
        );
    }

    public void exibirVolume(List<Cryptocurrency> criptos) {
        criptos.forEach(c ->
                System.out.println(c.getVolumeNegociacao())
        );
    }
}
