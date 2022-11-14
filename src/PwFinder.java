import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class PwFinder {

    private String username;

    private String id;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getId(){
        return idField.getText();
    }

    private JPanel pwFinderPanel;
    private JButton pwFinderButton;
    private JTextField usernameField;
    private JTextField idField;
    private JLabel pwFinderDesc;

    public PwFinder(JFrame frame) throws IOException {

        pwFinderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                setUsername(getUsername());
                setId(getId());

                System.out.println(username);
                System.out.println(id);

                try {
                    new Login(frame);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        frame.setContentPane(pwFinderPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
