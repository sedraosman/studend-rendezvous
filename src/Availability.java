import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class Availability extends JFrame {
    private int instructorId;
    private JTable availabilityTable;

    public Availability(int instructorId) {
        this.instructorId = instructorId;

        setTitle("Randevularim");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        getContentPane().setBackground(new Color(245, 249, 255));
        setLayout(new BorderLayout(10, 10));

        
        JLabel titleLabel = new JLabel("randevular", JLabel.CENTER);
        titleLabel.setFont(new Font("Century", Font.BOLD, 22));
        titleLabel.setForeground(new Color(40, 40, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        
        availabilityTable = new JTable();
        availabilityTable.setFont(new Font("Century", Font.PLAIN, 14));
        availabilityTable.setRowHeight(28);
        availabilityTable.setGridColor(new Color(220, 220, 220));
        availabilityTable.setShowVerticalLines(false);
        availabilityTable.setShowHorizontalLines(true);

        JTableHeader tableHeader = availabilityTable.getTableHeader();
        tableHeader.setFont(new Font("Century", Font.BOLD, 15));
        tableHeader.setBackground(new Color(0, 120, 215));
        tableHeader.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(availabilityTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        loadAvailability();
    }

    private void loadAvailability() {
        String sql = "SELECT date, start_time, end_time FROM Availability WHERE instructor_id = ? ORDER BY date, start_time";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Tarih", "Başlangıç ", "Bitiş "}, 0
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("date"),
                    rs.getString("start_time"),
                    rs.getString("end_time")
                });
            }

            availabilityTable.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Müsaitlikler yüklenirken hata: " + e.getMessage(),
                "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Availability(1).setVisible(true));
    }
}
