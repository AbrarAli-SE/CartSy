import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CustomerAdsDialog extends JDialog {
    public CustomerAdsDialog(JFrame parent) {
        super(parent, "Current Ads", true);
        setUndecorated(true);

        Color primary = new Color(0, 153, 204);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary, 2, true),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        mainPanel.setPreferredSize(new Dimension(700, 400));

        // Top bar with title and close button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Current Ads");
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

        JPanel adsPanel = new JPanel();
        adsPanel.setLayout(new BoxLayout(adsPanel, BoxLayout.Y_AXIS));
        adsPanel.setBackground(Color.WHITE);

        boolean hasAds = false;
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT ad_content, start_date, end_date FROM ads WHERE is_active=1";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                hasAds = true;
                JPanel adCard = new JPanel();
                adCard.setLayout(new BoxLayout(adCard, BoxLayout.Y_AXIS));
                adCard.setBackground(new Color(245, 247, 255));
                adCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(primary, 1, true),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));

                JLabel contentLabel = new JLabel("<html><b>" + rs.getString("ad_content") + "</b></html>");
                contentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                JLabel dateLabel = new JLabel("From: " + rs.getDate("start_date") + " To: " + rs.getDate("end_date"));
                dateLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                adCard.add(contentLabel);
                adCard.add(dateLabel);
                adCard.add(Box.createRigidArea(new Dimension(0, 8)));

                adsPanel.add(adCard);
                adsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } catch (Exception ex) {
            adsPanel.add(new JLabel("Error: " + ex.getMessage()));
        }

        if (!hasAds) {
            JLabel noAds = new JLabel("No ads available at the moment.", SwingConstants.CENTER);
            noAds.setFont(new Font("Segoe UI", Font.BOLD, 18));
            noAds.setForeground(primary);
            adsPanel.add(noAds);
        }

        JScrollPane scrollPane = new JScrollPane(adsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}