import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CustomerCatalogDialog extends JDialog {
    public CustomerCatalogDialog(JFrame parent, int customerId) {
        super(parent, "Product Catalog", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary, 2, true),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        mainPanel.setPreferredSize(new Dimension(900, 540));

        // Top bar with title and close button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Product Catalog");
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

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 18, 18));
        gridPanel.setBackground(Color.WHITE);

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT product_id, name, description, price FROM products";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int productId = rs.getInt("product_id");
                String name = rs.getString("name");
                String desc = rs.getString("description");
                double price = rs.getDouble("price");

                JPanel productCard = new JPanel();
                productCard.setLayout(new BoxLayout(productCard, BoxLayout.Y_AXIS));
                productCard.setBackground(new Color(245, 247, 255));
                productCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(primary, 1, true),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));

                JLabel nameLabel = new JLabel(name);
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                nameLabel.setForeground(primary);

                JLabel priceLabel = new JLabel("₹" + price);
                priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                priceLabel.setForeground(new Color(0, 120, 0));

                JButton detailsBtn = new JButton("View Details");
                detailsBtn.setBackground(primary);
                detailsBtn.setForeground(Color.WHITE);
                detailsBtn.setFocusPainted(false);

                detailsBtn.addActionListener(e -> new ProductDetailsDialog(this, customerId, productId));

                productCard.add(nameLabel);
                productCard.add(Box.createRigidArea(new Dimension(0, 8)));
                productCard.add(new JLabel("<html><div style='width:140px'>" + desc + "</div></html>"));
                productCard.add(Box.createRigidArea(new Dimension(0, 8)));
                productCard.add(priceLabel);
                productCard.add(Box.createRigidArea(new Dimension(0, 8)));
                productCard.add(detailsBtn);

                gridPanel.add(productCard);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}