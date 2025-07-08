import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Register extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullnameField;
    private JComboBox<String> roleCombo;
    private JButton registerButton;

    public Register() {
        setTitle("Kayıt Ekranı");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color backgroundColor = new Color(245, 249, 255); 
        Color buttonColor = new Color(70, 130, 180);     
        getContentPane().setBackground(backgroundColor);

        JLabel titleLabel = new JLabel("Register", JLabel.CENTER);
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

        
        JLabel usernameLabel = new JLabel("Kullanıcı Adı:");
        usernameLabel.setFont(new Font("Century", Font.PLAIN, 20));
        usernameLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Century", Font.PLAIN, 18));
        usernameField.setPreferredSize(new Dimension(300, 40));
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        
        JLabel fullnameLabel = new JLabel("Ad Soyad:");
        fullnameLabel.setFont(new Font("Century", Font.PLAIN, 20));
        fullnameLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(fullnameLabel, gbc);

        fullnameField = new JTextField();
        fullnameField.setFont(new Font("Century", Font.PLAIN, 18));
        fullnameField.setPreferredSize(new Dimension(300, 40));
        gbc.gridx = 1;
        formPanel.add(fullnameField, gbc);
        
        JLabel roleLabel = new JLabel("Rol:");
        roleLabel.setFont(new Font("Century", Font.PLAIN, 20));
        roleLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(roleLabel, gbc);

        roleCombo = new JComboBox<>(new String[] {"student", "instructor"});
        roleCombo.setFont(new Font("Century", Font.PLAIN, 18));
        roleCombo.setPreferredSize(new Dimension(300, 40));
        gbc.gridx = 1;
        formPanel.add(roleCombo, gbc);
        
        JLabel passwordLabel = new JLabel("Şifre:");
        passwordLabel.setFont(new Font("Century", Font.PLAIN, 20));
        passwordLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Century", Font.PLAIN, 18));
        passwordField.setPreferredSize(new Dimension(300, 40));
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

      

        
       

        add(formPanel, BorderLayout.CENTER);

        
        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Century", Font.BOLD, 20));
        registerButton.setBackground(buttonColor);
        registerButton.setForeground(Color.WHITE);
        registerButton.setPreferredSize(new Dimension(160, 50));
        registerButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(registerButton);
        add(buttonPanel, BorderLayout.SOUTH);

        
        registerButton.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText().trim();
            char[] password = passwordField.getPassword();
            String fullname = fullnameField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();

            try (Connection conn = DBConnection.connect()) {
                String query = "INSERT INTO Users (username, password, fullname, role) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, username);
                    stmt.setString(2, new String(password));
                    stmt.setString(3, fullname);
                    stmt.setString(4, role);

                    int result = stmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(this, "Kayıt başarılı! Giriş ekranına yönlendiriliyorsunuz.");
                        dispose();
                        new Login();
                    } else {
                        JOptionPane.showMessageDialog(this, "Kayıt başarısız. Lütfen tekrar deneyin.");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage());
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new Register();
    }
}
