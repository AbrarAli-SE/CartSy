import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SellerLogin extends JDialog {
    private int attempts = 0;

    public SellerLogin(JFrame parent) {
        super(parent, "Seller Login", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary, 2, true),
                BorderFactory.createEmptyBorder(24, 32, 24, 32)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(370, 350));

        // Cross (close) button
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

        JLabel title = new JLabel("Seller Login", SwingConstants.CENTER);
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
            attempts++;
            if (attempts > 3) {
                errorLabel.setText("Too many failed attempts!");
                loginBtn.setEnabled(false);
                return;
            }
            try (Connection conn = DBConnection.getConnection()) {
                if (conn != null) {
                    String query = "SELECT * FROM users WHERE email=? AND password=? AND role='Seller'";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, email);
                    ps.setString(2, pass);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        int userId = rs.getInt("id");
                        String sellerName = rs.getString("name");
                        // Fetch seller_id from sellers table
                        String sellerQuery = "SELECT seller_id FROM sellers WHERE user_id = ?";
                        PreparedStatement sellerPs = conn.prepareStatement(sellerQuery);
                        sellerPs.setInt(1, userId);
                        ResultSet sellerRs = sellerPs.executeQuery();
                        if (sellerRs.next()) {
                            int sellerId = sellerRs.getInt("seller_id");
                            dispose();
                            new SellerDashboard(sellerName, sellerId); // Only sellerId
                            parent.dispose();
                        } else {
                            errorLabel.setText("Seller profile not found!");
                        }
                    } else {
                        errorLabel.setText("Invalid Credentials! (" + attempts + "/3)");
                    }
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