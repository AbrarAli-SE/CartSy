
import javax.swing.*;

import java.awt.*;

class AdminLogin extends JDialog {
    AdminLogin(JFrame parent) {
        super(parent, "Admin Login", true);
        setUndecorated(true);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0,153,204), 2, true),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(350, 300));

        // Cross (close) button
        JButton closeBtn = new JButton("X"); // Unicode for 'X'
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setForeground(new Color(0, 153, 204));
        closeBtn.setFont(new Font("Arial", Font.BOLD, 18));
        closeBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(closeBtn, BorderLayout.EAST);

        JLabel title = new JLabel("Admin Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(new Color(0, 153, 204));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(300, 35));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 35));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        JButton loginBtn = new JButton("Login");
        UiHelper.styleButton(loginBtn, new Color(0, 153, 204));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            if (user.equals("admin") && pass.equals("123")) {
                dispose();
                AdminDashboard dashboard = new AdminDashboard();
                dashboard.setExtendedState(JFrame.MAXIMIZED_BOTH);
                dashboard.setVisible(true);
                parent.dispose();
            } else {
                errorLabel.setText("Invalid username or password!");
            }
        });

        panel.add(topPanel);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(usernameField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(loginBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(errorLabel);

        add(panel);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}