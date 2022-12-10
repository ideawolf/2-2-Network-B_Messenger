import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class Register extends JFrame {

    Register() {
        setSize(400, 600);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);

        // 아이디와 닉네임, 이름, 이메일, 생년월일 입력
        JPanel registerFormPanel = new JPanel(new GridLayout(7, 1, 0, 20));
        registerFormPanel.setBorder(new EmptyBorder(100, 20, 100, 20));
        registerFormPanel.setBackground(Color.WHITE);

        JPanel newIdForm = new JPanel(new FlowLayout(FlowLayout.CENTER));
        newIdForm.setBackground(Color.WHITE);
        JLabel newIdDesc = new JLabel("  아이디 : ");
        newIdDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JTextField newIdField = new JTextField(20);
        JButton newIdCheck = new JButton("중복체크");

        newIdField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                newIdCheck.setEnabled(true);
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        newIdCheck.addActionListener(e -> {
            String newId = newIdField.getText();
            // db에서 id 목록 가져와서 newId랑 비교


            // 중복체크 성공하면 setEnabled false로 바꾸기
            newIdCheck.setEnabled(false);
        });

        newIdForm.add(newIdDesc);
        newIdForm.add(newIdField);
        newIdForm.add(newIdCheck);

        JPanel newNicknameForm = new JPanel(new GridLayout(1, 2));
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

        registerButton.addActionListener(e -> {
            try {
                JSONObject json = new JSONObject();
                json.put("command", "REGISTER");
                json.put("name", "test_name");
                json.put("password", "test_password");
                json.put("nickname", "test_nickname");
                json.put("email", "test_email@email.com");
                Socket socket = new Socket("localhost", 35014);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                out.write(json.toString());
                out.newLine();
                out.flush();

//
//            int attempts = 0;
//            while(!in.ready() && attempts < 1000)
//            {
//                attempts++;
//                Thread.sleep(10);
//            }

                String response_str = in.readLine();

                JSONObject response = new JSONObject(response_str);

                int status = response.getInt("status");
                String body = response.getString("body");

                System.out.println("Status : " + status);
                System.out.println("body : " + body);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        });

        registerFormPanel.add(newNameForm);
        registerFormPanel.add(newIdForm);
        registerFormPanel.add(newPwdForm);
        registerFormPanel.add(newNicknameForm);
        registerFormPanel.add(newEmailForm);
        registerFormPanel.add(newBirthForm);
        registerFormPanel.add(registerButton);

        add(registerFormPanel);

        setVisible(true);
    }

}
