import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CustomerLogin extends JDialog {
    public CustomerLogin(JFrame parent) {
        super(parent, "Customer Login", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary, 2, true),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(400, 380));

        // Top bar with close button
        JButton closeBtn = new JButton("X");
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setForeground(primary);
        closeBtn.setFont(new Font("Arial", Font.BOLD, 20));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.setToolTipText("Close");
        closeBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        closeBtn.addActionListener(e -> dispose());
        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeBtn.setForeground(Color.RED);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeBtn.setForeground(primary);
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(closeBtn, BorderLayout.EAST);

        JLabel title = new JLabel("Customer Login", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(primary);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(300, 38));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 38));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 19));
        loginBtn.setBackground(primary);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setMaximumSize(new Dimension(200, 45));
        loginBtn.setBorder(BorderFactory.createLineBorder(primary, 18, true));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(220, 0, 0));
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setPreferredSize(new Dimension(350, 40));

        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String pass = new String(passwordField.getPassword());
            if (email.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("All fields required!");
                return;
            }
            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT * FROM users WHERE email=? AND password=? AND role='Customer'";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, email);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int customerId = rs.getInt("id");
                    String customerName = rs.getString("name");
                    dispose();
                    new CustomerDashboard(customerName, customerId);
                    parent.dispose();
                } else {
                    errorLabel.setText("Invalid Credentials!");
                }
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

        panel.add(topPanel);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));
        panel.add(emailField);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));
        panel.add(loginBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(errorLabel);

        add(panel);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}