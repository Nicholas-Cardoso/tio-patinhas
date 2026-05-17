package tio.patinhas.models;

public class User extends Entity {
    private String nome;
    private String email;
    private String senhaHash;

    private Portfolio portfolio;

    public User(Long id, String nome, String email, String senhaHash) {
        super(id);
        validar(nome, email, senhaHash);

        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;

        this.portfolio = new Portfolio(id, id);
    }

    public void setNome(String novoNome) {
        if (novoNome == null || novoNome.isBlank()) {
            throw new IllegalArgumentException("Nome inválido");
        }
        this.nome = novoNome;
    }

    public void alterarNome(String novoNome) {
        setNome(novoNome);
    }

    public void setEmail(String novoEmail) {
        if (novoEmail == null || novoEmail.isBlank()) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.email = novoEmail;
    }

    public void alterarEmail(String novoEmail) {
        setEmail(novoEmail);
    }

    public void setSenhaHash(String novoHash) {
        if (novoHash == null || novoHash.isBlank()) {
            throw new IllegalArgumentException("Senha inválida");
        }
        this.senhaHash = novoHash;
    }

    public void alterarSenha(String novoHash) {
        setSenhaHash(novoHash);
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    private void validar(String nome, String email, String senhaHash) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome inválido");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email inválido");
        }

        if (senhaHash == null || senhaHash.isBlank()) {
            throw new IllegalArgumentException("Senha inválida");
        }
    }
}
