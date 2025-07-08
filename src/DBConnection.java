
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_URL = "jdbc:sqlite:database.db"; 

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
          // System.out.println("Veritabanına başarıyla bağlanıldı.");
        } catch (SQLException e) {
            //System.out.println("Bağlantı hatası: " + e.getMessage());
        }
        return conn;
    }

    public static void main(String[] args) {
        connect();  
    }
}
