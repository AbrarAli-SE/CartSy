

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UpdateProductDialog extends JDialog {
    public UpdateProductDialog(JFrame parent, int sellerId) {
        super(parent, "Update Product", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primary, 2, true),
            BorderFactory.createEmptyBorder(18, 28, 18, 28)
        ));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(400, 450));

        // Top bar with title and close button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Update Product");
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

        JTextField productIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField qtyField = new JTextField();

        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);
        productIdField.setFont(fieldFont);
        nameField.setFont(fieldFont);
        descField.setFont(fieldFont);
        priceField.setFont(fieldFont);
        qtyField.setFont(fieldFont);

        mainPanel.add(labelAndField("Product ID:", productIdField));
        mainPanel.add(labelAndField("New Name:", nameField));
        mainPanel.add(labelAndField("New Description:", descField));
        mainPanel.add(labelAndField("New Price:", priceField));
        mainPanel.add(labelAndField("New Quantity:", qtyField));

        JButton updateBtn = new JButton("Update Product");
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        updateBtn.setBackground(primary);
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false);
        updateBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        updateBtn.setMaximumSize(new Dimension(200, 40));
        updateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateBtn.setBorder(BorderFactory.createLineBorder(primary, 16, true));

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(220, 0, 0));
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setPreferredSize(new Dimension(350, 40));

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(updateBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        mainPanel.add(errorLabel);

        updateBtn.addActionListener(e -> {
            String productIdStr = productIdField.getText().trim();
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            String priceStr = priceField.getText().trim();
            String qtyStr = qtyField.getText().trim();

            if (productIdStr.isEmpty() || name.isEmpty() || desc.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
                errorLabel.setText("All fields are required!");
                return;
            }

            try {
                int productId = Integer.parseInt(productIdStr);
                double price = Double.parseDouble(priceStr);
                int qty = Integer.parseInt(qtyStr);

                try (Connection conn = DBConnection.getConnection()) {
                    String query = "UPDATE products SET name=?, description=?, price=?, quantity=? WHERE product_id=? AND seller_id=?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, name);
                    ps.setString(2, desc);
                    ps.setDouble(3, price);
                    ps.setInt(4, qty);
                    ps.setInt(5, productId);
                    ps.setInt(6, sellerId);
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Product Updated!");
                        dispose();
                    } else {
                        errorLabel.setText("Product not found or not yours!");
                    }
                }
            } catch (NumberFormatException ex) {
                errorLabel.setText("Invalid product ID, price, or quantity!");
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