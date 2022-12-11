import function.Encrypt;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Login extends JFrame {

    Login() throws IOException {
        setSize(400,600);
        setLayout(new GridLayout(3,1));
        setLocationRelativeTo(null);

        // 로고 이미지
        BufferedImage logoImg = ImageIO.read(new File("images/logo.png"));

        Image image = new ImageIcon(logoImg).getImage();
        Image newImg = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);

        JLabel logoLabel = new JLabel(new ImageIcon(newImg));
        logoLabel.setVerticalAlignment(JLabel.BOTTOM);
        logoLabel.setBackground(Color.WHITE);
        logoLabel.setOpaque(true);
        add(logoLabel);

        // 아이디, 패스워드 입력
        JPanel fieldPanel = new JPanel(new GridLayout(3,1,30,10));
        fieldPanel.setBorder(new EmptyBorder(30,60,30,60));
        fieldPanel.setBackground(Color.WHITE);

        JLabel fieldDesc = new JLabel("아이디와 비밀번호를 입력하세요");
        fieldDesc.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        fieldDesc.setHorizontalAlignment(JLabel.CENTER);
        JTextField idField = new JTextField();
        JPasswordField pwField = new JPasswordField();

        fieldPanel.add(fieldDesc);
        fieldPanel.add(idField);
        fieldPanel.add(pwField);

        add(fieldPanel);

        // 로그인 버튼, 회원가입, 비밀번호 찾기
        JPanel buttonPanel = new JPanel(new GridLayout(3,1,30,0));
        buttonPanel.setBorder(new EmptyBorder(0,80,80,80));
        buttonPanel.setBackground(Color.WHITE);

        JButton loginButton = new JButton("로그인");
        loginButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        loginButton.setHorizontalAlignment(JLabel.CENTER);
        loginButton.setForeground(Color.BLACK);
        loginButton.setBackground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(new LineBorder(new Color(0x8EAADB), 2, true));

        loginButton.addActionListener(e -> {
            try {
                JSONObject json = new JSONObject();
                json.put("command", "LOGIN");
                json.put("id", idField.getText());
                json.put("password", Encrypt.getEncrpyt(Arrays.toString(pwField.getPassword())));
                Socket socket = new Socket("localhost", 35014);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.write(json.toString());
                out.newLine();
                out.flush();

                String response_str = in.readLine();

                JSONObject response = new JSONObject(response_str);

                //if (response.get())

                System.out.println("reponse : " + response);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton registerButton = new JButton("회원가입");
        registerButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        registerButton.setHorizontalAlignment(JLabel.CENTER);
        registerButton.setFocusPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setBorderPainted(false);

        registerButton.addActionListener(e -> new Register());

        JButton pwdButton = new JButton("비밀번호 변경");
        pwdButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        pwdButton.setHorizontalAlignment(JLabel.CENTER);
        pwdButton.setFocusPainted(false);
        pwdButton.setContentAreaFilled(false);
        pwdButton.setBorderPainted(false);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(pwdButton);



        add(buttonPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
