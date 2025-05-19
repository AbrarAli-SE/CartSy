import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BillDialog extends JDialog {
    public BillDialog(JFrame parent, int orderId) {
        super(parent, "Order Bill", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary, 2, true),
                BorderFactory.createEmptyBorder(18, 28, 18, 28)
        ));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(400, 400));

        JLabel title = new JLabel("Order Bill");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(primary);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea billArea = new JTextArea();
        billArea.setEditable(false);
        billArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        billArea.setBackground(Color.WHITE);

        try (Connection conn = DBConnection.getConnection()) {
            String orderQuery = "SELECT o.order_id, u.name, o.total, o.payment_type, o.card_info, o.order_date " +
                    "FROM orders o JOIN users u ON o.customer_id = u.id WHERE o.order_id=?";
            PreparedStatement ps = conn.prepareStatement(orderQuery);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StringBuilder bill = new StringBuilder();
                bill.append("Order ID: ").append(rs.getInt("order_id")).append("\n");
                bill.append("Customer: ").append(rs.getString("name")).append("\n");
                bill.append("Date: ").append(rs.getTimestamp("order_date")).append("\n");
                bill.append("Payment: ").append(rs.getString("payment_type")).append("\n");
                if (rs.getString("card_info") != null)
                    bill.append("Card: ").append(rs.getString("card_info")).append("\n");
                bill.append("\nItems:\n");

                String itemsQuery = "SELECT oi.product_id, p.name, oi.quantity, oi.price FROM order_items oi JOIN products p ON oi.product_id = p.product_id WHERE oi.order_id=?";
                PreparedStatement itemsPs = conn.prepareStatement(itemsQuery);
                itemsPs.setInt(1, orderId);
                ResultSet itemsRs = itemsPs.executeQuery();
                while (itemsRs.next()) {
                    bill.append(itemsRs.getString("name"))
                        .append(" x").append(itemsRs.getInt("quantity"))
                        .append(" @ ₹").append(itemsRs.getDouble("price"))
                        .append("\n");
                }
                bill.append("\nTotal: ₹").append(rs.getDouble("total"));
                billArea.setText(bill.toString());
            }
        } catch (Exception ex) {
            billArea.setText("Error: " + ex.getMessage());
        }

        mainPanel.add(title);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        mainPanel.add(new JScrollPane(billArea));

        add(mainPanel);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}