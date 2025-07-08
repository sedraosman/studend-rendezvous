import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class InstructorDashboard extends JFrame {
    private int instructorId;
    private String fullname;
    private JTable appointmentsTable;
    private JButton approveAllButton;
    private JButton defineAvailabilityButton;
    private JButton viewAvailabilityButton;
    private JButton approveSelectedButton;
    private JButton cancelSelectedButton;

    public InstructorDashboard(int instructorId, String fullname) {
        this.instructorId = instructorId;
        this.fullname = fullname;

        setTitle("Öğretim Görevlisi Paneli – " + fullname);
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 249, 255));
        setLayout(new BorderLayout(15, 15));

       
        JLabel titleLabel = new JLabel("Hoş Geldiniz, " + fullname, JLabel.CENTER);
        titleLabel.setFont(new Font("Century", Font.BOLD, 26));
        titleLabel.setForeground(new Color(33, 45, 63));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        
        appointmentsTable = new JTable();
        appointmentsTable.setFont(new Font("Century", Font.PLAIN, 15));
        appointmentsTable.setRowHeight(30);
        appointmentsTable.setGridColor(new Color(220, 220, 220));
        appointmentsTable.setShowVerticalLines(false);
        appointmentsTable.setShowHorizontalLines(true);

        JTableHeader tableHeader = appointmentsTable.getTableHeader();
        tableHeader.setFont(new Font("Century", Font.BOLD, 16));
        tableHeader.setBackground(new Color(0, 120, 215));
        tableHeader.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 20, 10));
        buttonPanel.setBackground(new Color(245, 249, 255));
        
        defineAvailabilityButton = createButton("Randevu Tanımla", new Color(70, 130, 180), Color.WHITE);
        defineAvailabilityButton.addActionListener(e -> new AvailabilityFrame(instructorId).setVisible(true));
        buttonPanel.add(defineAvailabilityButton);
     
        approveSelectedButton = createButton(" Randevuyu Onayla", new Color(255, 165, 0), Color.BLACK);
        approveSelectedButton.addActionListener(this::approveSelected);
        buttonPanel.add(approveSelectedButton);

        cancelSelectedButton = createButton(" Randevuyu Reddet", new Color(220, 20, 60), Color.WHITE);
        cancelSelectedButton.addActionListener(this::cancelSelected);
        buttonPanel.add(cancelSelectedButton);

       

        viewAvailabilityButton = createButton("Randevularım", new Color(105, 105, 105), Color.WHITE);
        viewAvailabilityButton.addActionListener(e -> new Availability(instructorId).setVisible(true));
        buttonPanel.add(viewAvailabilityButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadAppointments();
        setVisible(true);
    }

    private JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Century", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 1, true));
        return button;
    }

    private void loadAppointments() {
        String sql = "SELECT A.id, U.fullname AS student, A.date, A.start_time, A.status " +
                "FROM Appointments A " +
                "JOIN Users U ON A.student_id = U.id " +
                "WHERE A.instructor_id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Öğrenci","Saat", "Tarih",  "Durum"},0
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("student"),
                        rs.getString("start_time"),
                        rs.getString("date"),
                        rs.getString("status")
                });
            }
            appointmentsTable.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Randevular yüklenirken hata: " + e.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

   

    private void approveSelected(ActionEvent e) {
        int selectedRow = appointmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "bir randevu seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int appointmentId = (int) appointmentsTable.getValueAt(selectedRow, 0);
        String sql = "UPDATE Appointments SET status = 'approved' WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Randevu onaylandı.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            loadAppointments();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Onay işlemi başarısız: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelSelected(ActionEvent e) {
        int selectedRow = appointmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, " bir randevu seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int appointmentId = (int) appointmentsTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bu randevuyu reddetmek istediğinizden emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "UPDATE Appointments SET status = 'rejected' WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Randevu reddedildi.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, " zaten reddedilmiş.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            }
            loadAppointments();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, " işlem başarısız: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InstructorDashboard(1, ""));
    }
}
