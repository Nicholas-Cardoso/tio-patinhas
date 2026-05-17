package tio.patinhas.service;

public class AuthenticateService {
    public boolean login(String email, String senha) {
        // lógica de autenticação
        return validarCredenciais(email, senha);
    }

    public void logout(String email) {
        // lógica de logout
        System.out.println("Logout realizado para: " + email);
    }

    public boolean validarCredenciais(String email, String senha) {
        // validação fictícia
        return email != null && senha != null && !email.isEmpty() && !senha.isEmpty();
    }
}
