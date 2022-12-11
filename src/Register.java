import function.Encrypt;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

public class Register extends JFrame {

    Register() {
        setSize(400, 550);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        // 아이디와 닉네임, 이름, 이메일, 생년월일 입력
        JLabel newIdDesc = new JLabel("  아이디 : ");
        newIdDesc.setBounds(25, 50, 100, 30);
        newIdDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));

        JTextField newIdField = new JTextField(20);
        newIdField.setBounds(125, 50, 150, 30);

        JButton newIdCheck = new JButton("중복체크");
        newIdCheck.setBounds(275, 50, 100, 30);
        newIdCheck.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        newIdCheck.setHorizontalAlignment(JLabel.CENTER);
        newIdCheck.setForeground(Color.BLACK);
        newIdCheck.setBackground(Color.WHITE);
        newIdCheck.setFocusPainted(false);
        newIdCheck.setBorder(new LineBorder(new Color(0x8EAADB), 2, true));

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

        add(newIdDesc);
        add(newIdField);
        add(newIdCheck);


        JLabel newPwdDesc = new JLabel("비밀번호 : ");
        newPwdDesc.setBounds(25, 100, 100, 30);
        newPwdDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JPasswordField newPwdField = new JPasswordField(20);
        newPwdField.setBounds(125, 100, 250, 30);

        add(newPwdDesc);
        add(newPwdField);

        JLabel newPwdCheckDesc = new JLabel("비밀번호 확인 : ");
        newPwdCheckDesc.setBounds(25, 150, 100, 30);
        newPwdCheckDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JPasswordField newPwdCheckField = new JPasswordField(20);
        newPwdCheckField.setBounds(125, 150, 250, 30);

        add(newPwdCheckDesc);
        add(newPwdCheckField);

        JLabel newNameDesc = new JLabel("    이름 : ");
        newNameDesc.setBounds(25, 200, 100, 30);
        newNameDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JTextField newNameField = new JTextField(20);
        newNameField.setBounds(125, 200, 250, 30);

        add(newNameDesc);
        add(newNameField);

        JLabel newNicknameDesc = new JLabel("  닉네임 : ");
        newNicknameDesc.setBounds(25, 250, 100, 30);
        newNicknameDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JTextField newNicknameField = new JTextField(20);
        newNicknameField.setBounds(125, 250, 250, 30);

        add(newNicknameDesc);
        add(newNicknameField);

        JLabel newEmailDesc = new JLabel("  이메일 : ");
        newEmailDesc.setBounds(25, 300, 100, 30);
        newEmailDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JTextField newEmailField = new JTextField(20);
        newEmailField.setBounds(125, 300, 250, 30);

        add(newEmailDesc);
        add(newEmailField);

        JLabel newBirthDesc = new JLabel("생년월일 : ");
        newBirthDesc.setBounds(25, 350, 100, 30);
        newBirthDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JTextField newBirthField = new JTextField(20);
        newBirthField.setBounds(125, 350, 250, 30);

        add(newBirthDesc);
        add(newBirthField);


        JButton registerButton = new JButton("회원가입 완료");
        registerButton.setBounds(100, 400, 200, 30);
        registerButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        registerButton.setHorizontalAlignment(JLabel.CENTER);
        registerButton.setForeground(Color.BLACK);
        registerButton.setBackground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(new LineBorder(new Color(0x8EAADB), 2, true));

        registerButton.addActionListener(e -> {

            // 회원가입 valid check

            if (newIdCheck.isEnabled()) {
                JOptionPane.showOptionDialog(null, "ID 중복 체크를 해주세요", "알림", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"닫기"}, "닫기");
                return;
            }
            if (newPwdField.getPassword().length == 0) {
                JOptionPane.showOptionDialog(null, "비밀번호를 입력해주세요", "알림", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"닫기"}, "닫기");
                return;
            }
            if (!Objects.equals(Arrays.toString(newPwdField.getPassword()), Arrays.toString(newPwdCheckField.getPassword()))) {
                JOptionPane.showOptionDialog(null, "두 비밀번호가 다릅니다", "알림", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"닫기"}, "닫기");
                return;
            }
            if (newNameField.getText().length() == 0) {
                JOptionPane.showOptionDialog(null, "이름을 입력해주세요", "알림", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"닫기"}, "닫기");
                return;
            }
            if (newNicknameField.getText().length() == 0) {
                JOptionPane.showOptionDialog(null, "별명을 입력해주세요", "알림", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"닫기"}, "닫기");
                return;
            }
            if (newEmailField.getText().length() == 0) {
                JOptionPane.showOptionDialog(null, "이메일을 입력해주세요", "알림", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"닫기"}, "닫기");
                return;
            }
            if (newBirthField.getText().length() == 0) {
                JOptionPane.showOptionDialog(null, "생일을 입력해주세요", "알림", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"닫기"}, "닫기");
                return;
            }

            // System.out.println(Encrypt.getEncrpyt(Arrays.toString(newPwdField.getPassword())));

            try {
                JSONObject json = new JSONObject();
                json.put("command", "REGISTER");
                json.put("id", newIdField.getText());
                json.put("name", newNameField.getText());
                json.put("password", Encrypt.getEncrpyt(Arrays.toString(newPwdField.getPassword())));
                json.put("nickname", newNicknameField.getText());
                json.put("email", newEmailField.getText());
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

                JOptionPane.showOptionDialog(null, "회원가입 완료", "알림", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"닫기"}, "닫기");
                dispose();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        add(registerButton);
        setVisible(true);
    }
}