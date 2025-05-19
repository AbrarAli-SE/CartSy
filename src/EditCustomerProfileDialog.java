import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class EditCustomerProfileDialog extends JDialog {
    public EditCustomerProfileDialog(JFrame parent, int customerId) {
        super(parent, "Edit Profile", true);
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

        JLabel title = new JLabel("Edit Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(primary);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField cardField = new JTextField();

        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);
        nameField.setFont(fieldFont);
        emailField.setFont(fieldFont);
        usernameField.setFont(fieldFont);
        cardField.setFont(fieldFont);

        // Load current info
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT name, email, username, card_info FROM users WHERE id=? AND role='Customer'";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                emailField.setText(rs.getString("email"));
                usernameField.setText(rs.getString("username"));
                cardField.setText(rs.getString("card_info"));
            }
        } catch (Exception ex) {
            // ignore for now
        }

        mainPanel.add(labelAndField("Name:", nameField));
        mainPanel.add(labelAndField("Email:", emailField));
        mainPanel.add(labelAndField("Username:", usernameField));
        mainPanel.add(labelAndField("Card Info:", cardField));

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        saveBtn.setBackground(primary);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(220, 0, 0));
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String card = cardField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || username.isEmpty()) {
                errorLabel.setText("Name, Email, and Username required!");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String query = "UPDATE users SET name=?, email=?, username=?, card_info=? WHERE id=? AND role='Customer'";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, username);
                ps.setString(4, card);
                ps.setInt(5, customerId);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Profile updated!");
                    dispose();
                } else {
                    errorLabel.setText("Update failed!");
                }
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(saveBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        mainPanel.add(errorLabel);

        add(mainPanel);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private JPanel labelAndField(String label, JTextField field) {
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