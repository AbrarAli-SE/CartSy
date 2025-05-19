import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

class MainMenu extends JFrame  {
    MainMenu() {
        setTitle("CARTSY - Online Shopping Mall");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        JLabel logo = new JLabel("CARTSY", SwingConstants.CENTER);
        logo.setFont(new Font("Serif", Font.BOLD, 36));
        logo.setForeground(new Color(0, 102, 204));
        logo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(logo, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(6, 1, 15, 15));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));

        JLabel loginAs = new JLabel("Login as:", SwingConstants.CENTER);
        loginAs.setFont(new Font("Arial", Font.BOLD, 20));
        centerPanel.add(loginAs);

        JButton adminBtn = new JButton("Admin");
        JButton sellerBtn = new JButton("Seller");
        JButton customerBtn = new JButton("Customer");
        UiHelper.styleButton(adminBtn, new Color(0, 153, 204));
        UiHelper.styleButton(sellerBtn, new Color(0, 153, 204));
        UiHelper.styleButton(customerBtn, new Color(0, 153, 204));

        centerPanel.add(adminBtn);
        centerPanel.add(sellerBtn);
        centerPanel.add(customerBtn);

        JLabel registerLink = new JLabel("Register Yourself", SwingConstants.CENTER);
        registerLink.setForeground(Color.BLUE);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.setFont(new Font("Arial", Font.PLAIN, 14));
        centerPanel.add(registerLink);

        panel.add(centerPanel, BorderLayout.CENTER);
        add(panel);
        setVisible(true);

        adminBtn.addActionListener(e -> new AdminLogin(this));
        sellerBtn.addActionListener(e -> new SellerLogin(this));
        customerBtn.addActionListener(e -> new CustomerLogin(this));
        registerLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new RegisterScreen(MainMenu.this);
            }
        });
    }

}