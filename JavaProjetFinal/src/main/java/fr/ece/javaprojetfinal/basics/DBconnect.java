package fr.ece.javaprojetfinal.basics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnect {
    private static final String url = "jdbc:mysql://localhost:3306/gestionnaire_jdbc";
    private static final String username = "root";
    private static final String password = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
