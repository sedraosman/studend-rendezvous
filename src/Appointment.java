import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Appointment extends JFrame {
    private JComboBox<String> gunBox, ayBox, yilBox;
    private JComboBox<TeacherItem> instructorComboBox;
    private JComboBox<String> timeComboBox;
    private JButton submitButton, cancelButton;
    private int studentId;
    private Runnable onSuccess;

   
    private List<String> availableDates = new ArrayList<>();
    private List<String> availableTimes = new ArrayList<>();

    public Appointment(int studentId, Runnable onSuccess) {
        this.studentId = studentId;
        this.onSuccess = onSuccess;

        setTitle("Yeni Randevu Talebi");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);  
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel instructorLabel = new JLabel("Öğretim Görevlisi:");
        instructorLabel.setFont(new Font("Century", Font.PLAIN, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(instructorLabel, gbc);

        instructorComboBox = new JComboBox<>();
        loadInstructors();
        instructorComboBox.setFont(new Font("Century", Font.PLAIN, 18));
        instructorComboBox.addActionListener(e -> loadAvailableDatesAndTimes()); 
        gbc.gridx = 1;
        panel.add(instructorComboBox, gbc);

        JLabel gunLabel = new JLabel("Gün:");
        gunLabel.setFont(new Font("Century", Font.PLAIN, 20));
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(gunLabel, gbc);

        gunBox = new JComboBox<>();
        gunBox.setFont(new Font("Century", Font.PLAIN, 18));
        gbc.gridx = 1;
        panel.add(gunBox, gbc);

        

        String[] yillar = new String[100];
        for (int i = 0; i < 100; i++) yillar[i] = String.valueOf(2025 - i);
        yilBox = new JComboBox<>(yillar);
        yilBox.setFont(new Font("Century", Font.PLAIN, 18));
        gbc.gridx = 1;
        panel.add(yilBox, gbc);

        JLabel timeLabel = new JLabel("Saat (HH:MM):");
        timeLabel.setFont(new Font("Century", Font.PLAIN, 20));
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(timeLabel, gbc);

        timeComboBox = new JComboBox<>();
        timeComboBox.setFont(new Font("Century", Font.PLAIN, 18));
        gbc.gridx = 1;
        panel.add(timeComboBox, gbc);

        submitButton = new JButton("Gönder");
        submitButton.setFont(new Font("Century", Font.PLAIN, 20));
        submitButton.setBackground(Color.BLUE);   
        submitButton.setForeground(Color.WHITE);
        submitButton.setPreferredSize(new Dimension(160, 50));
        submitButton.addActionListener(this::submitAppointment);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panel.add(submitButton, gbc);

        cancelButton = new JButton("İptal");
        cancelButton.setFont(new Font("Century", Font.PLAIN, 20));
        cancelButton.setBackground(Color.BLUE);   // Changed button color to blue
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(160, 50));
        cancelButton.addActionListener(e -> dispose());
        gbc.gridy = 6;
        panel.add(cancelButton, gbc);

        add(panel);
    }

    private void loadInstructors() {
        String sql = "SELECT id, fullname FROM Users WHERE role = 'instructor'";
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            instructorComboBox.removeAllItems();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("fullname");
                instructorComboBox.addItem(new TeacherItem(id, name));
            }

            if (instructorComboBox.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "Hiç öğretim görevlisi bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Hocalar yüklenemedi: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAvailableDatesAndTimes() {
        TeacherItem selectedTeacher = (TeacherItem) instructorComboBox.getSelectedItem();
        if (selectedTeacher == null) {
            JOptionPane.showMessageDialog(this, "Lütfen bir öğretim görevlisi seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT date, start_time FROM Availability WHERE instructor_id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, selectedTeacher.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                availableDates.clear();
                availableTimes.clear();
                while (rs.next()) {
                    String date = rs.getString("date");
                    String time = rs.getString("start_time");

                    if (!availableDates.contains(date)) {
                        availableDates.add(date);
                    }

                    if (!availableTimes.contains(time)) {
                        availableTimes.add(time);
                    }
                }

                if (availableDates.isEmpty() || availableTimes.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Mevcut tarih ve saatler bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                }

                gunBox.setModel(new DefaultComboBoxModel<>(availableDates.toArray(new String[0])));
                timeComboBox.setModel(new DefaultComboBoxModel<>(availableTimes.toArray(new String[0])));

            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Mevcut tarih ve saatler yüklenemedi: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitAppointment(ActionEvent e) {
        TeacherItem selectedTeacher = (TeacherItem) instructorComboBox.getSelectedItem();
        String selectedDate = (String) gunBox.getSelectedItem();
        String selectedTime = (String) timeComboBox.getSelectedItem();

        String checkSql = "SELECT COUNT(*) FROM Appointments WHERE instructor_id = ? AND date = ? AND start_time = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, selectedTeacher.getId());
            checkStmt.setString(2, selectedDate);
            checkStmt.setString(3, selectedTime);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        JOptionPane.showMessageDialog(this, "Bu tarih ve saatte zaten bir randevu var. Lütfen başka bir zaman seçin.", "Randevu Hatası", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            String sql = "INSERT INTO Appointments (student_id, instructor_id, date, start_time) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, studentId);
                stmt.setInt(2, selectedTeacher.getId());
                stmt.setString(3, selectedDate);
                stmt.setString(4, selectedTime);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Randevu başarıyla oluşturuldu!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    onSuccess.run();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Randevu oluşturulurken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class TeacherItem {
        private int id;
        private String name;

        public TeacherItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name + " (ID: " + id + ")";
        }
    }
}
