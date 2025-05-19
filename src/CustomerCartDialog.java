import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class CustomerCartDialog extends JDialog {
    public CustomerCartDialog(JFrame parent, int customerId) {
        super(parent, "My Cart", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary, 2, true),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        mainPanel.setPreferredSize(new Dimension(800, 440));

        // Top bar with title and close button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("My Cart");
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

        String[] columns = {"Product ID", "Name", "Price", "Quantity"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT c.product_id, p.name, p.price, c.quantity FROM cart c JOIN products p ON c.product_id = p.product_id WHERE c.customer_id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setBackground(primary);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteBtn.setFocusPainted(false);

        JButton orderBtn = new JButton("Order Selected");
        orderBtn.setBackground(new Color(0, 180, 80));
        orderBtn.setForeground(Color.WHITE);
        orderBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderBtn.setFocusPainted(false);

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Delete selected logic (existing)
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                statusLabel.setText("Select a product to delete!");
                return;
            }
            int productId = (int) model.getValueAt(row, 0);
            try (Connection conn = DBConnection.getConnection()) {
                String query = "DELETE FROM cart WHERE customer_id=? AND product_id=?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, customerId);
                ps.setInt(2, productId);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    model.removeRow(row);
                    statusLabel.setForeground(new Color(0, 120, 0));
                    statusLabel.setText("Removed from cart!");
                } else {
                    statusLabel.setText("Error removing item!");
                }
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Order selected logic
        orderBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                statusLabel.setText("Select a product to order!");
                return;
            }
            int productId = (int) model.getValueAt(row, 0);
            int qty = (int) model.getValueAt(row, 3);
            double price = (double) model.getValueAt(row, 2);
            double total = price * qty;
            try (Connection conn = DBConnection.getConnection()) {
                // Insert order
                String orderQuery = "INSERT INTO orders (customer_id, total, payment_type) VALUES (?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, customerId);
                ps.setDouble(2, total);
                ps.setString(3, "Cash on Delivery");
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                int orderId = 0;
                if (rs.next()) orderId = rs.getInt(1);

                // Insert order item
                String itemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
                PreparedStatement itemPs = conn.prepareStatement(itemQuery);
                itemPs.setInt(1, orderId);
                itemPs.setInt(2, productId);
                itemPs.setInt(3, qty);
                itemPs.setDouble(4, price);
                itemPs.executeUpdate();

                // Remove from cart
                String delCart = "DELETE FROM cart WHERE customer_id=? AND product_id=?";
                PreparedStatement delPs = conn.prepareStatement(delCart);
                delPs.setInt(1, customerId);
                delPs.setInt(2, productId);
                delPs.executeUpdate();

                // Remove from table
                model.removeRow(row);
                statusLabel.setForeground(new Color(0, 120, 0));
                statusLabel.setText("Order placed successfully!");
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.add(deleteBtn);
        btnPanel.add(orderBtn);

        bottomPanel.add(btnPanel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        bottomPanel.add(statusLabel);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}