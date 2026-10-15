package tio.patinhas.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionFactory {
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@//oracle.fiap.com.br:1521/ORCL";

    // RM do aluno como USUARIO & SENHA a data de nascimento do mesmo.
    private static final String USUARIO = "";
    private static final String SENHA = "";

    private ConnectionFactory() {
    }

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER);

            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "Driver JDBC da Oracle não encontrado no classpath. " +
                            "Verifique se o ojdbc.jar foi adicionado ao projeto.", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Não foi possível conectar ao banco Oracle da FIAP. " +
                            "Verifique host, porta, service name, usuário e senha.", e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }

    public static void closeConnection(Connection connection, Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar o statement: " + e.getMessage());
            }
        }

        closeConnection(connection);
    }

    public static boolean testarConexao() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (Exception e) {
            System.err.println("Falha no teste de conexão: " + e.getMessage());
            return false;
        }
    }
}