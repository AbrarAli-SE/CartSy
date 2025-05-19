import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RunAdsDialog extends JDialog {
    public RunAdsDialog(JFrame parent, int sellerId) {
        super(parent, "Run Ads", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary, 2, true),
                BorderFactory.createEmptyBorder(18, 28, 18, 28)
        ));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(400, 300));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Run Ads");
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

        JTextField adContentField = new JTextField();
        adContentField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField daysField = new JTextField();
        daysField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        mainPanel.add(labelAndField("Ad Content:", adContentField));
        mainPanel.add(labelAndField("Run for (days):", daysField));

        JButton runBtn = new JButton("Run Ad");
        runBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        runBtn.setBackground(primary);
        runBtn.setForeground(Color.WHITE);
        runBtn.setFocusPainted(false);
        runBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        runBtn.setMaximumSize(new Dimension(200, 40));
        runBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        runBtn.setBorder(BorderFactory.createLineBorder(primary, 16, true));

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(220, 0, 0));
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setPreferredSize(new Dimension(350, 40));

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(runBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        mainPanel.add(errorLabel);

        runBtn.addActionListener(e -> {
            String adContent = adContentField.getText().trim();
            String daysStr = daysField.getText().trim();
            if (adContent.isEmpty() || daysStr.isEmpty()) {
                errorLabel.setText("All fields are required!");
                return;
            }
            try {
                int days = Integer.parseInt(daysStr);
                try (Connection conn = DBConnection.getConnection()) {
                    String query = "INSERT INTO ads (seller_id, ad_content, start_date, end_date, is_active) VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL ? DAY), 1)";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setInt(1, sellerId);
                    ps.setString(2, adContent);
                    ps.setInt(3, days);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Ad is now running!");
                    dispose();
                }
            } catch (NumberFormatException ex) {
                errorLabel.setText("Invalid days!");
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