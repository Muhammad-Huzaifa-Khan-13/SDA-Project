package backend.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
    		"jdbc:mysql://trolley.proxy.rlwy.net:54135/quizdb?useSSL=false&autoReconnect=true";

    private static final String USER = "root";
    private static final String PASSWORD = "gwIdHgdnnmBsrmLxLisJXmqyeFbqZsFn"; 

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found: " + e.getMessage());
            return null;

        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return null;
        }
    }
}
