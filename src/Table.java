import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Table extends JFrame {
    private JTabbedPane tabbedPane;

    public Table() {
        setTitle("Veritabanı Tablo Görüntüleyici");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

     
        addTableTab("Users");
        addTableTab("Availability");
        addTableTab("Appointments");

        setVisible(true);
    }

    private void addTableTab(String tableName) {
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

       
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(meta.getColumnName(i));
            }

            
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i-1] = rs.getObject(i);
                }
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Tablo yüklenirken hata: " + e.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }

        tabbedPane.addTab(tableName, scrollPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Table ::new);
    }
}