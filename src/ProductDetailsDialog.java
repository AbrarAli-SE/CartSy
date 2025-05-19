import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProductDetailsDialog extends JDialog {
    public ProductDetailsDialog(JDialog parent, int customerId, int productId) {
        super(parent, "Product Details", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary, 2, true),
                BorderFactory.createEmptyBorder(40, 40, 40, 40) // Increased padding
        ));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(440, 400)); // Increased width

        // Top bar with title and close button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Product Details");
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

        JLabel nameLabel = new JLabel();
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLabel.setForeground(primary);

        JLabel descLabel = new JLabel();
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(Color.DARK_GRAY);

        JLabel priceLabel = new JLabel();
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        priceLabel.setForeground(new Color(0, 120, 0));

        JLabel qtyLabel = new JLabel();
        qtyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        addToCartBtn.setBackground(primary);
        addToCartBtn.setForeground(Color.WHITE);
        addToCartBtn.setFocusPainted(false);

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT name, description, price, quantity FROM products WHERE product_id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nameLabel.setText(rs.getString("name"));
                descLabel.setText("<html><div style='width:320px'>" + rs.getString("description") + "</div></html>");
                priceLabel.setText("₹" + rs.getDouble("price"));
                qtyLabel.setText("Available: " + rs.getInt("quantity"));
            }
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
        }

        addToCartBtn.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection()) {
                String check = "SELECT * FROM cart WHERE customer_id=? AND product_id=?";
                PreparedStatement ps = conn.prepareStatement(check);
                ps.setInt(1, customerId);
                ps.setInt(2, productId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    statusLabel.setText("Already in cart!");
                    return;
                }
                String insert = "INSERT INTO cart (customer_id, product_id, quantity) VALUES (?, ?, 1)";
                ps = conn.prepareStatement(insert);
                ps.setInt(1, customerId);
                ps.setInt(2, productId);
                ps.executeUpdate();
                statusLabel.setForeground(new Color(0, 120, 0));
                statusLabel.setText("Added to cart!");
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        mainPanel.add(nameLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(descLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(priceLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(qtyLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 18)));
        mainPanel.add(addToCartBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        mainPanel.add(statusLabel);

        add(mainPanel);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}