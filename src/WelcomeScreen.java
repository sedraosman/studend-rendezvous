import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WelcomeScreen extends JFrame {
    public WelcomeScreen() {
        setTitle("Randevu Sistemi");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color backgroundColor = new Color(245, 249, 255); 
        Color buttonColor = new Color(60, 120, 180);     
        Color exitButtonColor = new Color(100, 100, 100);

        getContentPane().setBackground(backgroundColor);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(" Hoş Geldiniz", JLabel.CENTER);
        titleLabel.setFont(new Font("Century", Font.BOLD, 26));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20)); 
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton loginButton = createStyledButton("Giriş Yap", buttonColor);
        loginButton.addActionListener((ActionEvent e) -> {
            dispose();
            new Login();
        });

        JButton registerButton = createStyledButton("Kayıt Ol", buttonColor);
        registerButton.addActionListener((ActionEvent e) -> {
            dispose();
            new Register();
        });

        JButton exitButton = createStyledButton("Çıkış", exitButtonColor);
        exitButton.addActionListener((ActionEvent e) -> System.exit(0));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Century", Font.BOLD, 18));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WelcomeScreen::new);
    }
}
