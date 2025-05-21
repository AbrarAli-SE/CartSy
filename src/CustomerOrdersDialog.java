import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class CustomerOrdersDialog extends JDialog {
    public CustomerOrdersDialog(JFrame parent, int customerId) {
        super(parent, "My Orders", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary, 2, true),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        mainPanel.setPreferredSize(new Dimension(900, 500));

        // Top bar with title and close button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("My Orders");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(primary);

        JButton closeBtn = new JButton("X");
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setForeground(primary);
        closeBtn.setFont(new Font("Arial", Font.BOLD, 20));
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

        String[] columns = {"Order ID", "Date", "Total", "Payment", "Items"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT order_id, order_date, total, payment_type FROM orders WHERE customer_id=? ORDER BY order_date DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String date = rs.getTimestamp("order_date").toString();
                double total = rs.getDouble("total");
                String payment = rs.getString("payment_type");

                // Get items for this order
                StringBuilder items = new StringBuilder();
                String itemQuery = "SELECT oi.quantity, p.name FROM order_items oi JOIN products p ON oi.product_id=p.product_id WHERE oi.order_id=?";
                PreparedStatement itemPs = conn.prepareStatement(itemQuery);
                itemPs.setInt(1, orderId);
                ResultSet itemRs = itemPs.executeQuery();
                while (itemRs.next()) {
                    items.append(itemRs.getString("name"))
                         .append(" x").append(itemRs.getInt("quantity"))
                         .append("; ");
                }

                model.addRow(new Object[]{orderId, date, "Pkr " + total, payment, items.toString()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

        JTable table = new JTable(model);
        table.setRowHeight(28);
        JScrollPane scrollPane = new JScrollPane(table);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
