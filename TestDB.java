import backend.db.DatabaseConnection;
import java.sql.Connection;

public class TestDB {
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();

        if (conn != null) {
            System.out.println("DB Connected Successfully!");
        } else {
            System.out.println("DB Connection Failed!");
        }
    }
}
