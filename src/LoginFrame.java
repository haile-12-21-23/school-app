import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private DBHelper db;

    public LoginFrame() {
        setTitle("Login");
        setLocation(20, 10);

        setSize(260, 180);
        setPreferredSize(getPreferredSize());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 4));

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();

        add(new JLabel("Username:"));
        add(userField);

        add(new JLabel("Password:"));
        add(passField);

        JButton loginBtn = new JButton("Login");
        add(new JLabel()); // empty space
        loginBtn.setSize(40, 30);
        add(loginBtn);

        try {
            db = new DBHelper();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            e.printStackTrace();
        }

        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            try {
                if (db.login(user, pass)) {
                    JOptionPane.showMessageDialog(this, "Login successful!");

                    // Open MainFrame
                    new MainFrame(db).setVisible(true);
                    dispose(); // close login

                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}