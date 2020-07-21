package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class UConexion {
    private static Connection connection;
    private static UConexion uConexion;

    private UConexion(String driver, String databaseUrl, String user, String password) {
        try {
            Class.forName(driver);

            connection = DriverManager.getConnection(databaseUrl, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection Initialize(String configFilePath) {
        if (uConexion != null) {
            System.err.println("Object already initialized");
            return connection;
        }

        try (InputStream input = new FileInputStream(configFilePath)) {
            Properties prop = new Properties();

            prop.load(input);

            UConexion uconexion = new UConexion(
                prop.getProperty("driver"),
                prop.getProperty("databaseUrl"),
                prop.getProperty("user"),
                prop.getProperty("password")
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

        return connection;
    }

    @Override
    public UConexion clone() {
        try {
            throw new CloneNotSupportedException();
        } catch (CloneNotSupportedException e) {
            System.err.println("This class cannot be cloned");
        }
        return null;
    }
}