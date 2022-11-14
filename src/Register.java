import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Register {

    private String newUsername;

    private String newId;

    private char[] newPassword;

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public void setNewId(String newId) {
        this.newId = newId;
    }

    public void setNewPassword(char[] newPassword) {
        this.newPassword = newPassword;
    }
    private JTextField newUsernameField;
    private JTextField newIdField;
    private JPasswordField newPasswordField;
    private JPasswordField newPasswordCheck;

    public String getNewUsername() {
        return newUsernameField.getText();
    }

    public String getNewId(){
        return newIdField.getText();
    }

    public char[] getNewPassword() {
        return newPasswordField.getPassword();
    }

    public char[] getNewPasswordCheck() {
        return newPasswordCheck.getPassword();
    }

    private JButton dupCheckButton;
    private JPanel registerPanel;
    private JButton registerButton;
    private JPanel registerLogo;

    public Register(JFrame frame) throws IOException {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Arrays.equals(getNewPassword(), getNewPasswordCheck()))
                {
                    setNewUsername(getNewUsername());
                    setNewId(getNewId());
                    setNewPassword(getNewPassword());
                }

                System.out.println(newUsername);
                System.out.println(newId);
                System.out.println(newPassword);

                try {
                    new Login(frame);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        BufferedImage logoImg = ImageIO.read(new File("src/images/logo.png"));

        Image image = new ImageIcon(logoImg).getImage();
        Image newImg = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);

        JLabel logoLabel = new JLabel(new ImageIcon(newImg));

        registerLogo.setLayout(new BorderLayout());
        registerLogo.add(logoLabel, BorderLayout.SOUTH);

        frame.setContentPane(registerPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
