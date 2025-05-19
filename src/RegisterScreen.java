import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterScreen extends JDialog {
    public RegisterScreen(JFrame parent) {
        super(parent, "Register", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary, 2, true),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(420, 450));

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

        JLabel title = new JLabel("Register", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(primary);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(300, 38));
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        nameField.setBorder(BorderFactory.createTitledBorder("Name"));

        JTextField emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(300, 38));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 38));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        JComboBox<String> roleCombo = new JComboBox<>(new String[] {"Seller", "Customer"});
        roleCombo.setMaximumSize(new Dimension(300, 38));
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        roleCombo.setBorder(BorderFactory.createTitledBorder("Role"));

        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 19));
        registerBtn.setBackground(primary);
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerBtn.setMaximumSize(new Dimension(200, 45));
        registerBtn.setBorder(BorderFactory.createLineBorder(primary, 18, true));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(220, 0, 0));
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setPreferredSize(new Dimension(350, 40));

        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword());
            String role = roleCombo.getSelectedItem().toString();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("All fields are required!");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                if (conn != null) {
                    String query = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, name);
                    ps.setString(2, email);
                    ps.setString(3, pass);
                    ps.setString(4, role);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Registration Successful!");
                    dispose();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                errorLabel.setText("Registration Failed! Email may already exist.");
            } catch (Exception ss) {
                errorLabel.setText("Error: " + ss.getMessage());
            }
        });

        panel.add(topPanel);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));
        panel.add(nameField);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(emailField);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(roleCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));
        panel.add(registerBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(errorLabel);

        add(panel);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
