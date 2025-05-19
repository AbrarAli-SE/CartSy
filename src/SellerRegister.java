

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

public class SellerRegister extends JDialog {
    public SellerRegister(JFrame parent) {
        super(parent, "Seller Registration", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primary, 2, true),
            BorderFactory.createEmptyBorder(18, 28, 18, 28)
        ));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(400, 420));

        // Top bar with title and close button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Seller Registration");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(primary);

        JButton closeBtn = new JButton("X");
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setForeground(primary);
        closeBtn.setFont(new Font("Arial", Font.BOLD, 18));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.setToolTipText("Close");
        closeBtn.addActionListener(e -> dispose());
        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeBtn.setForeground(Color.RED);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeBtn.setForeground(primary);
            }
        });

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(closeBtn, BorderLayout.EAST);

        mainPanel.add(topPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField fatherNameField = new JTextField();
        JTextField cnicField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);
        nameField.setFont(fieldFont);
        emailField.setFont(fieldFont);
        usernameField.setFont(fieldFont);
        fatherNameField.setFont(fieldFont);
        cnicField.setFont(fieldFont);
        passwordField.setFont(fieldFont);

        mainPanel.add(labelAndField("Name:", nameField));
        mainPanel.add(labelAndField("Email:", emailField));
        mainPanel.add(labelAndField("Username:", usernameField));
        mainPanel.add(labelAndField("Father's Name:", fatherNameField));
        mainPanel.add(labelAndField("CNIC:", cnicField));
        mainPanel.add(labelAndField("Password:", passwordField));

        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        registerBtn.setBackground(primary);
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerBtn.setMaximumSize(new Dimension(200, 40));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setBorder(BorderFactory.createLineBorder(primary, 16, true));

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(220, 0, 0));
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setPreferredSize(new Dimension(350, 40));

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(registerBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        mainPanel.add(errorLabel);

        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String fatherName = fatherNameField.getText().trim();
            String cnic = cnicField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || email.isEmpty() || username.isEmpty() || fatherName.isEmpty() || cnic.isEmpty() || password.isEmpty()) {
                errorLabel.setText("All fields are required!");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String userQuery = "INSERT INTO users (name, email, password, role, username, father_name, cnic) VALUES (?, ?, ?, 'Seller', ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(userQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, password);
                ps.setString(4, username);
                ps.setString(5, fatherName);
                ps.setString(6, cnic);
                ps.executeUpdate();

                java.sql.ResultSet rs = ps.getGeneratedKeys();
                int userId = 0;
                if (rs.next()) userId = rs.getInt(1);

                String sellerId = "SELLER" + (1000 + new Random().nextInt(9000));
                String sellerQuery = "INSERT INTO sellers (user_id, seller_code) VALUES (?, ?)";
                PreparedStatement ps2 = conn.prepareStatement(sellerQuery);
                ps2.setInt(1, userId);
                ps2.setString(2, sellerId);
                ps2.executeUpdate();

                JOptionPane.showMessageDialog(this, "Registration Successful!\nSeller ID: " + sellerId);
                dispose();
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

        add(mainPanel);
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