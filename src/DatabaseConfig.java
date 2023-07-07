package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static final String DB_URL = "jdbc:oracle:thin:@127.0.0.1:1521:XE";
    private static final String USERNAME = "hotel";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() {

        try {
            System.out.println("connection stable");
            return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Failed to connect to the Oracle database.");
            e.printStackTrace();
        }
        return null;
    }
}
