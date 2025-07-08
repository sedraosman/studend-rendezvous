import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class StudentDashboard extends JFrame {
    private int studentId;
    private String fullname;
    private JTable appointmentsTable;
    private JButton requestAppointmentButton, deleteButton, cancelAppointmentButton;

    public StudentDashboard(int studentId, String fullname) {
        this.studentId = studentId;
        this.fullname = fullname;

        setTitle("Öğrenci Paneli – " + fullname);
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 249, 255));
        setLayout(new BorderLayout(15, 15));

        
        JLabel titleLabel = new JLabel("Merhaba, " + fullname + "  Randevuların:", JLabel.CENTER);
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

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(new Color(245, 249, 255));

      
        cancelAppointmentButton = createButton("  İptal Et", new Color(255, 165, 0), Color.BLACK);
        cancelAppointmentButton.addActionListener(this::cancelAppointment);
        buttonPanel.add(cancelAppointmentButton);

        deleteButton = createButton("  Sil", new Color(220, 20, 60), Color.WHITE);
        deleteButton.addActionListener(this::deleteAppointment);
        buttonPanel.add(deleteButton);

        requestAppointmentButton = createButton(" Randevu Talep Et", new Color(70, 130, 180), Color.WHITE);
        requestAppointmentButton.addActionListener(this::requestNewAppointment);
        buttonPanel.add(requestAppointmentButton);
        

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
        String sql = "SELECT A.id, I.fullname AS instructor, A.date, A.start_time, A.status " +
                "FROM Appointments A " +
                "JOIN Users I ON A.instructor_id = I.id " +
                "WHERE A.student_id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Öğretmen","Saat", "Tarih",  "Durum"}, 0
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("instructor"),
                        rs.getString("start_time"),
                        rs.getString("date"),
                      
                        rs.getString("status")
                });
            }
            appointmentsTable.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Randevu yüklenirken hata: " + e.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void requestNewAppointment(ActionEvent e) {
        new Appointment(studentId, this::loadAppointments).setVisible(true);
    }

    private void deleteAppointment(ActionEvent e) {
        int selectedRow = appointmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "bir randevu seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int appointmentId = (int) appointmentsTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bu randevuyu silmek istediğinizden emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM Appointments WHERE id = ?";
            try (Connection conn = DBConnection.connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, appointmentId);
                int result = stmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, " silindi.");
                    loadAppointments();
                } else {
                    JOptionPane.showMessageDialog(this, " silinemedi.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelAppointment(ActionEvent e) {
        int selectedRow = appointmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, " randevu seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int appointmentId = (int) appointmentsTable.getValueAt(selectedRow, 0);
        String sql = "UPDATE Appointments SET status = 'rejected' WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            int result = stmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, " iptal edildi.");
                loadAppointments();
            } else {
                JOptionPane.showMessageDialog(this, " iptal edilemedi.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDashboard(1, ""));
    }
}
