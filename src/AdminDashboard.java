import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Color sidebarColor1 = new Color(0, 102, 204);
        Color sidebarColor2 = new Color(51, 153, 255);
        Color btnColor = new Color(51, 153, 255);
        Color btnHover = new Color(30, 130, 230);

        // Sidebar with gradient and rounded corners
        JPanel sidebar = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, sidebarColor1, 0, getHeight(), sidebarColor2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(270, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Admin Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        sidebar.add(title);

        String[] btnNames = {
                "Add Seller", "Remove Seller", "Update Seller Tier",
                "View Users", "View Sales", "Control Ads", "Back to Main Menu"
        };
        List<JButton> buttons = Arrays.asList(
                new JButton(btnNames[0]), new JButton(btnNames[1]), new JButton(btnNames[2]),
                new JButton(btnNames[3]), new JButton(btnNames[4]), new JButton(btnNames[5]), new JButton(btnNames[6]));

        for (int i = 0; i < buttons.size(); i++) {
            JButton btn = buttons.get(i);
            btn.setMaximumSize(new Dimension(220, 48));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            btn.setBackground(i == 6 ? new Color(220, 53, 69) : btnColor); // Red for "Back"
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setMargin(new Insets(10, 20, 10, 20));
            btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
            btn.setOpaque(true);

            // Rounded corners and hover effect
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (btn != buttons.get(6))
                        btn.setBackground(btnHover);
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (btn != buttons.get(6))
                        btn.setBackground(btnColor);
                }
            });

            sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
            sidebar.add(btn);
        }

        // Main content area with card style
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 247, 255));

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(500, 220));
        card.setOpaque(true);

        JLabel welcome = new JLabel("Welcome, Admin!", SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcome.setForeground(new Color(0, 102, 204));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Manage sellers, users, sales, and more from this dashboard.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(80, 80, 80));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(welcome);
        card.add(Box.createRigidArea(new Dimension(0, 18)));
        card.add(subtitle);

        contentPanel.add(card);

        // Layout
        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Button actions
        buttons.get(0).addActionListener(e -> new AddSellerDialog(this));
        buttons.get(1).addActionListener(e -> new RemoveSellerDialog(this));
        buttons.get(2).addActionListener(e -> new UpdateSellerTierDialog(this));
        buttons.get(3).addActionListener(e -> new ViewUsersDialog(this));
        buttons.get(4).addActionListener(e -> new ViewSalesDialog(this));
        buttons.get(5).addActionListener(e -> new ControlAdsDialog(this));
        buttons.get(6).addActionListener(e -> {
            dispose();
            new MainMenu().setVisible(true);
        });

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
}