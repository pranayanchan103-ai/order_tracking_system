package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Single place that knows how to connect to MySQL.
 * CHANGE THE PASSWORD below to your own MySQL root password.
 */
public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/order_tracker"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata";
    private static final String USER = "root";
    private static final String PASSWORD = "PriyaPrakash1108!";

    // Utility class: nobody should create an object of it
    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
