package tio.patinhas.models;

public abstract class FinancialAsset extends Entity {

    public FinancialAsset(Long id) {
        super(id);
    }

    public abstract double calcularValor(double quantidade);

    public abstract String descricaoResumida();
}
