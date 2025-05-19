import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CustomerRegister extends JDialog {
    public CustomerRegister(JFrame parent) {
        super(parent, "Customer Registration", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary, 2, true),
                BorderFactory.createEmptyBorder(24, 32, 24, 32)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(370, 370));

        JLabel title = new JLabel("Customer Registration", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(primary);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));
        panel.add(labelAndField("Name:", nameField));
        panel.add(labelAndField("Email:", emailField));
        panel.add(labelAndField("Username:", usernameField));
        panel.add(labelAndField("Password:", passwordField));

        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        registerBtn.setBackground(primary);
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(registerBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(errorLabel);

        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword());
            if (name.isEmpty() || email.isEmpty() || username.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("All fields required!");
                return;
            }
            try (Connection conn = DBConnection.getConnection()) {
                String check = "SELECT * FROM users WHERE email=? OR username=?";
                PreparedStatement ps = conn.prepareStatement(check);
                ps.setString(1, email);
                ps.setString(2, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    errorLabel.setText("Email or Username already exists!");
                    return;
                }
                String query = "INSERT INTO users (name, email, username, password, role) VALUES (?, ?, ?, ?, 'Customer')";
                ps = conn.prepareStatement(query);
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, username);
                ps.setString(4, pass);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registration Successful! Please login.");
                dispose();
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

        add(panel);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private JPanel labelAndField(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        p.add(l);
        p.add(field);
        p.add(Box.createRigidArea(new Dimension(0, 7)));
        return p;
    }
}