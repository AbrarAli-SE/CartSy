import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class CheckoutDialog extends JDialog {
    public CheckoutDialog(JFrame parent, int customerId, DefaultTableModel cartModel) {
        super(parent, "Checkout", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary, 2, true),
                BorderFactory.createEmptyBorder(18, 28, 18, 28)
        ));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(400, 350));

        JLabel title = new JLabel("Checkout");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(primary);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel totalLabel = new JLabel();
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Calculate total
        double total = 0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            Object priceObj = cartModel.getValueAt(i, 2);
            Object qtyObj = cartModel.getValueAt(i, 3);
            double price = (priceObj instanceof Number) ? ((Number) priceObj).doubleValue() : Double.parseDouble(priceObj.toString());
            int qty = (qtyObj instanceof Number) ? ((Number) qtyObj).intValue() : Integer.parseInt(qtyObj.toString());
            total += price * qty;
        }
        totalLabel.setText("Total: ₹" + total);

        String[] paymentOptions = {"Cash on Delivery", "Online Payment"};
        JComboBox<String> paymentBox = new JComboBox<>(paymentOptions);
        paymentBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        paymentBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JTextField cardField = new JTextField();
        cardField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cardField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cardField.setVisible(false);

        paymentBox.addActionListener(e -> {
            cardField.setVisible(paymentBox.getSelectedIndex() == 1);
            mainPanel.revalidate();
            mainPanel.repaint();
        });

        JButton payBtn = new JButton("Confirm & Pay");
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        payBtn.setBackground(primary);
        payBtn.setForeground(Color.WHITE);
        payBtn.setFocusPainted(false);
        payBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        payBtn.addActionListener(e -> {
            // Recalculate total in case cart changed
            double currentTotal = 0;
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                Object priceObj = cartModel.getValueAt(i, 2);
                Object qtyObj = cartModel.getValueAt(i, 3);
                double price = (priceObj instanceof Number) ? ((Number) priceObj).doubleValue() : Double.parseDouble(priceObj.toString());
                int qty = (qtyObj instanceof Number) ? ((Number) qtyObj).intValue() : Integer.parseInt(qtyObj.toString());
                currentTotal += price * qty;
            }
            totalLabel.setText("Total: ₹" + currentTotal);

            String paymentType = (String) paymentBox.getSelectedItem();
            String cardInfo = cardField.getText().trim();
            if (paymentType.equals("Online Payment") && cardInfo.isEmpty()) {
                statusLabel.setText("Enter card info!");
                return;
            }
            if (cartModel.getRowCount() == 0) {
                statusLabel.setText("Cart is empty!");
                return;
            }
            try (Connection conn = DBConnection.getConnection()) {
                // Insert order
                String orderQuery = "INSERT INTO orders (customer_id, total, payment_type, card_info) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, customerId);
                ps.setDouble(2, currentTotal);
                ps.setString(3, paymentType);
                ps.setString(4, paymentType.equals("Online Payment") ? cardInfo : null);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                int orderId = 0;
                if (rs.next()) orderId = rs.getInt(1);

                // Insert order items and clear cart
                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    int productId = Integer.parseInt(cartModel.getValueAt(i, 0).toString());
                    int qty = Integer.parseInt(cartModel.getValueAt(i, 3).toString());
                    double price = Double.parseDouble(cartModel.getValueAt(i, 2).toString());
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
                }

                JOptionPane.showMessageDialog(this, "Order placed successfully!");
                dispose();
                new BillDialog(parent, orderId);
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        mainPanel.add(title);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        mainPanel.add(totalLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        mainPanel.add(new JLabel("Payment Method:"));
        mainPanel.add(paymentBox);
        mainPanel.add(cardField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        mainPanel.add(payBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        mainPanel.add(statusLabel);

        add(mainPanel);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}