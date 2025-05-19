import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

class RemoveSellerDialog extends JDialog {
    RemoveSellerDialog(JFrame parent) {
        super(parent, "Remove Seller", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primary, 2, true),
            BorderFactory.createEmptyBorder(18, 28, 18, 28)
        ));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(370, 220));

        // Top bar with title and close button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Remove Seller");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
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

        JTextField sellerIdField = new JTextField();
        sellerIdField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        mainPanel.add(labelAndField("Enter Seller Code:", sellerIdField));

        JButton removeBtn = new JButton("Remove Seller");
        removeBtn.setFont(new Font("Segoe UI", Font.BOLD, 17));
        removeBtn.setBackground(primary);
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFocusPainted(false);
        removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeBtn.setMaximumSize(new Dimension(180, 38));
        removeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        removeBtn.setBorder(BorderFactory.createLineBorder(primary, 14, true));

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(220, 0, 0));
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setPreferredSize(new Dimension(350, 40));

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(removeBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        mainPanel.add(errorLabel);

        removeBtn.addActionListener(e -> {
            String sellerCode = sellerIdField.getText().trim();
            if (sellerCode.isEmpty()) {
                errorLabel.setText("Seller Code required!");
                return;
            }
            try (Connection conn = DBConnection.getConnection()) {
                String query = "DELETE u FROM users u JOIN sellers s ON u.id = s.user_id WHERE s.seller_code = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, sellerCode);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Seller removed!");
                    dispose();
                } else {
                    errorLabel.setText("Seller not found!");
                }
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

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