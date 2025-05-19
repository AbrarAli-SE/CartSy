import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;

public class ManageInventoryDialog extends JDialog {
    public ManageInventoryDialog(JFrame parent, int sellerId) {
        super(parent, "Manage Inventory", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary, 2, true),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        mainPanel.setPreferredSize(new Dimension(700, 440));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Manage Inventory");
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

        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Product ID", "Name", "Quantity"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT product_id, name, quantity FROM products WHERE seller_id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getInt("quantity")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel updatePanel = new JPanel();
        updatePanel.setLayout(new BoxLayout(updatePanel, BoxLayout.X_AXIS));
        updatePanel.setOpaque(false);

        JTextField productIdField = new JTextField();
        productIdField.setMaximumSize(new Dimension(100, 32));
        JTextField qtyField = new JTextField();
        qtyField.setMaximumSize(new Dimension(100, 32));
        JButton updateBtn = new JButton("Update Quantity");
        updateBtn.setBackground(primary);
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateBtn.setFocusPainted(false);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(220, 0, 0));
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        errorLabel.setPreferredSize(new Dimension(350, 40));

        updatePanel.add(new JLabel("Product ID: "));
        updatePanel.add(productIdField);
        updatePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        updatePanel.add(new JLabel("New Qty: "));
        updatePanel.add(qtyField);
        updatePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        updatePanel.add(updateBtn);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.add(updatePanel);
        bottomPanel.add(errorLabel);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        updateBtn.addActionListener(e -> {
            String productIdStr = productIdField.getText().trim();
            String qtyStr = qtyField.getText().trim();
            if (productIdStr.isEmpty() || qtyStr.isEmpty()) {
                errorLabel.setText("Both fields required!");
                return;
            }
            try {
                int productId = Integer.parseInt(productIdStr);
                int qty = Integer.parseInt(qtyStr);
                try (Connection conn = DBConnection.getConnection()) {
                    String query = "UPDATE products SET quantity=? WHERE product_id=? AND seller_id=?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setInt(1, qty);
                    ps.setInt(2, productId);
                    ps.setInt(3, sellerId);
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        // Update the table model directly
                        for (int i = 0; i < model.getRowCount(); i++) {
                            if ((int) model.getValueAt(i, 0) == productId) {
                                model.setValueAt(qty, i, 2); // Update quantity column
                                break;
                            }
                        }
                        errorLabel.setText("Quantity updated!");
                    } else {
                        errorLabel.setText("Product not found or not yours!");
                    }
                }
            } catch (NumberFormatException ex) {
                errorLabel.setText("Invalid input!");
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

        add(mainPanel);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}