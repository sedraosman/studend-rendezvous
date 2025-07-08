import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AvailabilityFrame extends JFrame {
    private JComboBox<String> startTimeComboBox, endTimeComboBox;
    private JButton saveButton, clearButton;
    private int instructorId;
    private JSpinner dateSpinner;

    public AvailabilityFrame(int instructorId) {
        this.instructorId = instructorId;

        setTitle("Müsait Saat Ekle");
        setSize(450, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

       
        Font labelFont = new Font("Century ", Font.BOLD, 15);
        Font comboFont = new Font("Century ", Font.PLAIN, 14);

        
        JLabel dateLabel = new JLabel("Tarih:");
        dateLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        add(dateLabel, gbc);

        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(comboFont);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        add(dateSpinner, gbc);

        JLabel startLabel = new JLabel("Başlangıç Saati:");
        startLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        add(startLabel, gbc);

        String[] times = generateTimeSlots();
        startTimeComboBox = new JComboBox<>(times);
        startTimeComboBox.setFont(comboFont);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        add(startTimeComboBox, gbc);

        JLabel endLabel = new JLabel("Bitiş Saati:");
        endLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        add(endLabel, gbc);

        endTimeComboBox = new JComboBox<>(times);
        endTimeComboBox.setFont(comboFont);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        add(endTimeComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 5));
        buttonPanel.setBackground(Color.WHITE);

        saveButton = new JButton("Kaydet");
        saveButton.setFont(new Font("Century Schoolbook", Font.BOLD, 16));
        saveButton.setBackground(new Color(40, 110, 180));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(this::saveAvailability);

        clearButton = new JButton("Temizle");
        clearButton.setFont(new Font("Century Schoolbook", Font.BOLD, 16));
        clearButton.setBackground(new Color(200, 60, 60));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 15, 15, 15);
        add(buttonPanel, gbc);

        setVisible(true);
    }

    private String[] generateTimeSlots() {
        String[] slots = new String[48];
        for (int i = 0; i < 24; i++) {
            slots[i * 2] = String.format("%02d:00", i);
            slots[i * 2 + 1] = String.format("%02d:30", i);
        }
        return slots;
    }

    private void saveAvailability(ActionEvent e) {
        Date selectedDate = (Date) dateSpinner.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(selectedDate);

        String start = (String) startTimeComboBox.getSelectedItem();
        String end = (String) endTimeComboBox.getSelectedItem();

        if (start == null || end == null || start.isEmpty() || end.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Başlangıç ve bitiş saatini seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (start.compareTo(end) >= 0) {
            JOptionPane.showMessageDialog(this, "Bitiş saati başlangıç saatinden sonra olmalıdır.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO Availability (instructor_id, date, start_time, end_time) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, instructorId);
            stmt.setString(2, date);
            stmt.setString(3, start);
            stmt.setString(4, end);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Müsait saat kaydedildi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            clearForm();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        dateSpinner.setValue(new Date());
        startTimeComboBox.setSelectedIndex(0);
        endTimeComboBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AvailabilityFrame(1));
    }
}
