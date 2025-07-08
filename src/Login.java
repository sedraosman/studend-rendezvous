import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public Login() {
        
        setTitle("Giriş Ekranı");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color backgroundColor = new Color(245, 249, 255);
        Color buttonColor = new Color(70, 130, 180);
        getContentPane().setBackground(backgroundColor);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("LOGIN", JLabel.CENTER);
        titleLabel.setFont(new Font("Century", Font.BOLD, 36));
        titleLabel.setForeground(Color.DARK_GRAY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(60, 150, 60, 150));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Kullanıcı Adı:");
        usernameLabel.setFont(new Font("Century", Font.PLAIN, 20));
        usernameLabel.setForeground(Color.DARK_GRAY);
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField("");
        usernameField.setPreferredSize(new Dimension(300, 40));
        usernameField.setFont(new Font("Century", Font.PLAIN, 18));
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Şifre:");
        passwordLabel.setFont(new Font("Century", Font.PLAIN, 20));
        passwordLabel.setForeground(Color.DARK_GRAY);
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setFont(new Font("Century", Font.PLAIN, 18));
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Century", Font.BOLD, 20));
        loginButton.setBackground(buttonColor);
        
        loginButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);

        
        loginButton.addActionListener(this::authenticateUser);

        setVisible(true);
    }

    private void authenticateUser(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String sql = "SELECT id, role, fullname FROM Users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this,
                        rs.getString("fullname") + " olarak giriş başarılı!",
                        "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    int userId = rs.getInt("id");
                    String role = rs.getString("role");

                    if ("student".equals(role)) {
                        new StudentDashboard(userId, rs.getString("fullname"));
                    } else {
                        new InstructorDashboard(userId, rs.getString("fullname"));
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Geçersiz kullanıcı adı veya şifre.",
                        "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Veritabanı hatası: " + ex.getMessage(),
                "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}