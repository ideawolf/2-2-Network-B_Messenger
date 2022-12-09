import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Register extends JFrame {

    public static void main(String[] args) {
        new Register();
    }
    Register() {
        setSize(400,600);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);

        // 아이디와 닉네임, 이름, 이메일, 생년월일 입력
        JPanel registerFormPanel = new JPanel(new GridLayout(7,1,0,20));
        registerFormPanel.setBorder(new EmptyBorder(100,20,100,20));
        registerFormPanel.setBackground(Color.WHITE);

        JPanel newIdForm = new JPanel(new FlowLayout(FlowLayout.CENTER));
        newIdForm.setBackground(Color.WHITE);
        JLabel newIdDesc = new JLabel("  아이디 : ");
        newIdDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JTextField newIdField = new JTextField(20);

        newIdForm.add(newIdDesc);
        newIdForm.add(newIdField);

        JPanel newNicknameForm = new JPanel(new GridLayout(1,2));
        newNicknameForm.setBackground(Color.WHITE);
        JLabel newNicknameDesc = new JLabel("  닉네임 : ");
        newNicknameDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JTextField newNicknameField = new JTextField(20);

        newNicknameForm.add(newNicknameDesc);
        newNicknameForm.add(newNicknameField);

        JPanel newNameForm = new JPanel(new FlowLayout(FlowLayout.CENTER));
        newNameForm.setBackground(Color.WHITE);
        JLabel newNameDesc = new JLabel("    이름 : ");
        newNameDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JTextField newNameField = new JTextField(20);

        newNameForm.add(newNameDesc);
        newNameForm.add(newNameField);

        JPanel newEmailForm = new JPanel(new FlowLayout(FlowLayout.CENTER));
        newEmailForm.setBackground(Color.WHITE);
        JLabel newEmailDesc = new JLabel("  이메일 : ");
        newEmailDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JTextField newEmailField = new JTextField(20);

        newEmailForm.add(newEmailDesc);
        newEmailForm.add(newEmailField);

        JPanel newBirthForm = new JPanel(new FlowLayout(FlowLayout.CENTER));
        newBirthForm.setBackground(Color.WHITE);
        JLabel newBirthDesc = new JLabel("생년월일 : ");
        newBirthDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JTextField newBirthField = new JTextField(20);

        newBirthForm.add(newBirthDesc);
        newBirthForm.add(newBirthField);

        JPanel newPwdForm = new JPanel(new FlowLayout(FlowLayout.CENTER));
        newPwdForm.setBackground(Color.WHITE);
        JLabel newPwdDesc = new JLabel("비밀번호 : ");
        newPwdDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JPasswordField newPwdField = new JPasswordField(20);

        newPwdForm.add(newPwdDesc);
        newPwdForm.add(newPwdField);

        JButton registerButton = new JButton("회원가입 완료");
        registerButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        registerButton.setHorizontalAlignment(JLabel.CENTER);
        registerButton.setForeground(Color.BLACK);
        registerButton.setBackground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(new LineBorder(new Color(0x8EAADB), 2, true));

        registerFormPanel.add(newNameForm);
        registerFormPanel.add(newIdForm);
        registerFormPanel.add(newPwdForm);
        registerFormPanel.add(newNicknameForm);
        registerFormPanel.add(newEmailForm);
        registerFormPanel.add(newBirthForm);
        registerFormPanel.add(registerButton);

        add(registerFormPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

}
