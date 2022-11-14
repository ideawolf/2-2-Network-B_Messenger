import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        frame.setContentPane(registerPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
