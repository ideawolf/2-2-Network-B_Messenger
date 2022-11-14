import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Login {

    private static String userId;
    private static char[] userPassword;

    public static void setUserId(String userId) {
        Login.userId = userId;
    }

    public static void setUserPassword(char[] userPassword) {
        Login.userPassword = userPassword;
    }

    private JPanel loginPanel;
    private JPanel loginLogo;
    private JPanel loginForm;
    private JPanel loginOption;
    private JPasswordField pwField;
    private JTextField idField;

    public char[] getPw() {
        return pwField.getPassword();
    }

    public String getId() {
        return idField.getText();
    }

    private JButton registerButton;
    private JButton passSearchButton;
    private JButton loginButton;

    public Login(JFrame frame) throws IOException {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login.setUserId(getId());
                Login.setUserPassword(getPw());
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new Register(frame);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        BufferedImage logoImg = ImageIO.read(new File("src/images/logo.png"));

        Image image = new ImageIcon(logoImg).getImage();
        Image newImg = image.getScaledInstance(240, 240, Image.SCALE_SMOOTH);

        JLabel logoLabel = new JLabel(new ImageIcon(newImg));

        loginLogo.setLayout(new BorderLayout());
        loginLogo.add(logoLabel, BorderLayout.SOUTH);

        frame.setContentPane(loginPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
