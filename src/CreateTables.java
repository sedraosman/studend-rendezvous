import java.sql.Connection;
import java.sql.Statement;

public class CreateTables {
    public static void main(String[] args) {
        String sqlUsers = "CREATE TABLE IF NOT EXISTS Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "fullname TEXT NOT NULL," +
                "role TEXT CHECK(role IN ('student', 'instructor')) NOT NULL);";

        String sqlAvailability = "CREATE TABLE IF NOT EXISTS Availability (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "instructor_id INTEGER NOT NULL," +
                "date TEXT NOT NULL," +
                "start_time TEXT NOT NULL," +
                "end_time TEXT NOT NULL," +
                "FOREIGN KEY (instructor_id) REFERENCES Users(id));";

        String sqlAppointments = "CREATE TABLE IF NOT EXISTS Appointments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_id INTEGER NOT NULL," +
                "instructor_id INTEGER NOT NULL," +
                "date TEXT NOT NULL," +
                "start_time TEXT NOT NULL," +
                "status TEXT CHECK(status IN ('pending','approved','rejected')) DEFAULT 'pending'," +
                "FOREIGN KEY (student_id) REFERENCES Users(id)," +
                "FOREIGN KEY (instructor_id) REFERENCES Users(id));";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlUsers);
            stmt.execute(sqlAvailability);
            stmt.execute(sqlAppointments);
            System.out.println("Tablolar başarıyla oluşturuldu.");

        } catch (Exception e) {
            System.out.println("Tablo oluşturma hatası: " + e.getMessage());
        }
        
        
    }
}
