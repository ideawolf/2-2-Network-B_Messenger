import model.ClientUser;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChatRoom extends JFrame {

    ImageIcon file_upload_img = new ImageIcon("images/file_upload.png");
    JTextArea chatList;
    JPanel friendList;
    JScrollPane friendListScroll;
    int ROOM_ID;

    ClientUser USER;

    public void receiveMessage(JSONObject response)
    {
        chatList.setText(chatList.getText() + "\n"
                + response.getString("body"));
    }

    public void leftMessage(JSONObject response)
    {
        chatList.setText(chatList.getText() + "\n"
                + response.getString("leave_user_name") + "님이 나갔습니다.");
    }

    ChatRoom(int room_id, ClientUser user, ChatMain chatMain) { // 채팅방
        setSize(900, 600);
        setResizable(false);
        setLayout(null);
        setBackground(Color.gray);
        setTitle(room_id + "번방");
        getContentPane().setBackground(new Color(0xF4F3FF));
        USER = user;
        ROOM_ID = room_id;

        chatList = new JTextArea();
        chatList.setBounds(25,25,550,350);
        chatList.setEditable(false);
        chatList.setBackground(Color.white);

        try {
            JSONObject json = new JSONObject();
            json.put("command", "SEND_MESSAGE");
            json.put("access-token", USER.getAccessToken());

            json.put("room_id", room_id);
            json.put("msg", USER.getName() + "님이 들어오셨습니다.");


//            json.put("access-token", "00000000-0000-0000-0000-000000000001");
            Socket socket = new Socket("localhost", 35014);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.write(json.toString());
            out.newLine();
            out.flush();

            String response_str = in.readLine();

            JSONObject response = new  JSONObject(response_str);

            System.out.println("reponse: " + response);

            if(response.getInt("status") == 200)
            {
                chatList.setText(USER.getName() + "님이 들어오셨습니다.");
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        // 파일 전송 버튼
        JButton fileSendButton = new JButton(file_upload_img);
        fileSendButton.setBounds(25,425,90,90);
        fileSendButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        fileSendButton.setHorizontalAlignment(JLabel.CENTER);
        fileSendButton.setForeground(Color.BLACK);
        fileSendButton.setBackground(Color.WHITE);
        fileSendButton.setFocusPainted(false);
        fileSendButton.setBorder(new LineBorder(new Color(0x8EAADB), 2, true));

        // 텍스트 입력 area
        JTextArea sendArea = new JTextArea();
        sendArea.setBounds(150,425,300,90);

        // 전송 버튼
        JButton sendButton = new JButton("보내기");
        sendButton.setBounds(475,425,90,90);
        sendButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        sendButton.setHorizontalAlignment(JLabel.CENTER);
        sendButton.setForeground(Color.BLACK);
        sendButton.setBackground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(new LineBorder(new Color(0x8EAADB), 2, true));

        sendButton.addActionListener(e -> {
            try {
                JSONObject json = new JSONObject();
                json.put("command", "SEND_MESSAGE");
                json.put("access-token", USER.getAccessToken());

                json.put("room_id", room_id);
                json.put("msg", USER.getName() + " : " + sendArea.getText());


//            json.put("access-token", "00000000-0000-0000-0000-000000000001");
                Socket socket = new Socket("localhost", 35014);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.write(json.toString());
                out.newLine();
                out.flush();

                String response_str = in.readLine();

                JSONObject response = new  JSONObject(response_str);

                System.out.println("response: " + response);

                if(response.getInt("status") == 200)
                {
                    chatList.setText(chatList.getText() + "\n" + USER.getName() + " : " + sendArea.getText());
                    sendArea.setText("");
                }

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        JLabel friendListDesc = new JLabel("친구목록");
        friendListDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        friendListDesc.setBounds(600, 25, 250, 30);
        friendListDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(friendListDesc);

        friendList = new JPanel();
        friendList.setBackground(new Color(0xF4F3FF));
        friendList.setLayout(new BoxLayout(friendList, BoxLayout.Y_AXIS));
        friendListScroll = new JScrollPane(friendList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        friendListScroll.getVerticalScrollBar().setUnitIncrement(15);
        friendListScroll.setBounds(600, 90, 250, 430);
        add(friendListScroll);

        // 여기서 친구 리스트를 불러온 뒤 반복문을 통해 데이터를 넣음
        // friend 클래스에 친구 이름을 넣어서 리스트에 추가함
        if (USER.getFriendList() != null) {
            for (int i = 0; i < USER.getFriendList().length(); i++)
                friendList.add(new friend(USER.getFriendList().getJSONObject(i)));
        }

        add(chatList);
        add(fileSendButton);
        add(sendArea);
        add(sendButton);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                int option = JOptionPane.showOptionDialog(null, "                채팅방을 나가시겠습니까?", "알림", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"나가기", "취소"}, "나가기");
                if (option == 0) {
                    e.getWindow().dispose();
                    // 채팅방을 나갔다는 알림을 서버에게 전해주기
                    try {
                        JSONObject json = new JSONObject();
                        json.put("command", "LEAVE_ROOM");
                        json.put("access-token", USER.getAccessToken());
                        json.put("room_id", room_id);

                        Socket socket = new Socket("localhost", 35014);
                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        out.write(json.toString());
                        out.newLine();
                        out.flush();

                        String response_str = in.readLine();

                        JSONObject response = new  JSONObject(response_str);

                        System.out.println("response: " + response);

                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        fileSendButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(this);
            chatMain.recentFile = fileChooser.getSelectedFile();
            StringTokenizer tokenizer = new StringTokenizer(fileChooser.getSelectedFile().toString(), "\\");
            ArrayList<String> splitStr = new ArrayList<>();
            while(tokenizer.hasMoreTokens()) {
                splitStr.add(tokenizer.nextToken());
            }
            int option = JOptionPane.showOptionDialog(null, splitStr.get(splitStr.size() - 1) + " 파일을 전송하시겠습니까?", "알림", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"보내기", "취소"}, "보내기");
            if (option == 0) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("command", "SEND_FILE_ROOM");
                    json.put("access-token", USER.getAccessToken());
                    json.put("room_id", room_id);

                    json.put("file_name", splitStr.get(splitStr.size() - 1));

                    Socket socket = new Socket("localhost", 35014);
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                    out.write(json.toString());
                    out.newLine();
                    out.flush();

                    String response_str = in.readLine();

                    JSONObject response = new JSONObject(response_str);

                    System.out.println("response: " + response);


                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    JSONObject json = new JSONObject();
                    json.put("command", "SEND_MESSAGE");
                    json.put("access-token", USER.getAccessToken());

                    json.put("room_id", room_id);
                    json.put("msg", USER.getName() + "님이 파일을 전송합니다");

                    Socket socket = new Socket("localhost", 35014);
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    out.write(json.toString());
                    out.newLine();
                    out.flush();

                    String response_str = in.readLine();

                    JSONObject response = new  JSONObject(response_str);

                    System.out.println("response: " + response);

                    if(response.getInt("status") == 200)
                    {
                        chatList.setText(chatList.getText() + "\n" + USER.getName() + "님이 파일을 전송합니다");
                        sendArea.setText("");
                    }

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }
        });


        setVisible(true);

    }

    class friend extends JPanel {

        friend(JSONObject friend) {
            super(null);
            Container comp = this;
            setPreferredSize(new Dimension(500,51));
            setMaximumSize(new Dimension(500, 51));
            setBorder(new EmptyBorder(10, 20, 10, 0));
            setBackground(Color.WHITE);

            JLabel name = new JLabel(friend.getString("name"));
            name.setBounds(10, 10, 90, 30);
            name.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            add(name);

            JLabel isOnline = new JLabel();
            isOnline.setBounds(100, 10, 50, 30);
            isOnline.setFont(new Font("맑은 고딕", Font.BOLD, 10));
            isOnline.setHorizontalAlignment(SwingConstants.RIGHT);
            if (friend.getString("isOnline").equals("1")) {
                isOnline.setText("온라인");
                isOnline.setForeground(Color.GREEN);
            } else {
                isOnline.setText("오프라인");
                isOnline.setForeground(Color.RED);
            }
            add(isOnline);

            JLabel line = new JLabel();
            line.setBounds(0, 50, 300, 1);
            line.setBackground(Color.GRAY);
            line.setOpaque(true);
            add(line);

            /*
             * 우클릭 메뉴
             */
            PopupMenu pm1 = new PopupMenu("Edit");

            MenuItem item1 = new MenuItem("See detail");
            MenuItem item2 = new MenuItem("Invite");

            pm1.add(item1);
            pm1.add(item2);

            item1.addActionListener(e -> {
                // 상세 정보 화면 호출
                new detailInfo(friend.getString("user_id"),
                        friend.getString("name"),
                        friend.getString("nickname"),
                        friend.getString("email"),
                        friend.getString("isOnline"),
                        friend.getString("last_online"));
            });

            item2.addActionListener(e -> {
                // 채팅 초대
                if (friend.getString("isOnline").equals("1")) {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("command", "INVITE_ROOM");
                        json.put("access-token", USER.getAccessToken());
                        json.put("room_id", ROOM_ID);

                        JSONArray invite_user_list = new JSONArray();
                        invite_user_list.put(friend.getString("user_id"));

                        json.put("userlist", invite_user_list);

//            json.put("access-token", "00000000-0000-0000-0000-000000000001");
                        Socket socket = new Socket("localhost", 35014);
                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        out.write(json.toString());
                        out.newLine();
                        out.flush();

                        String response_str = in.readLine();

                        JSONObject response = new  JSONObject(response_str);

                        System.out.println("response: " + response);

                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3)
                        pm1.show(comp, e.getX(), e.getY());
                }
            });
            add(pm1);
        }
    }

    class detailInfo extends JFrame {

        detailInfo(String id, String name, String nickname, String email, String state, String conTime) {
            super("유저 상세정보");
            state = state.equals("0") ? "오프라인" : "온라인";
            setSize(300, 225);
            setResizable(false);
            setLocationRelativeTo(null);
            setLayout(null);
            getContentPane().setBackground(Color.WHITE);

            JLabel userIdLabel = new JLabel("아이디 : " + id);
            userIdLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            userIdLabel.setBounds(50, 25, 200, 30);
            userIdLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(userIdLabel);

            JLabel usernameLabel = new JLabel("이름 : " + name);
            usernameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            usernameLabel.setBounds(50, 50, 200, 30);
            usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(usernameLabel);

            JLabel userNicknameLabel = new JLabel("별명 : " + nickname);
            userNicknameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            userNicknameLabel.setBounds(50, 75, 200, 30);
            userNicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(userNicknameLabel);

            JLabel userEmailLabel = new JLabel("이메일 : " + email);
            userEmailLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            userEmailLabel.setBounds(50, 100, 200, 30);
            userEmailLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(userEmailLabel);

            JLabel userOnOffStateLabel = new JLabel("접속 상태 : " + state);
            userOnOffStateLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            userOnOffStateLabel.setBounds(50, 125, 200, 30);
            userOnOffStateLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(userOnOffStateLabel);

            conTime = LocalDateTime.parse(conTime).format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

            JLabel lastConTimeLabel = new JLabel("마지막 접속 : " + conTime);
            lastConTimeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            lastConTimeLabel.setBounds(40, 150, 220, 30);
            lastConTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(lastConTimeLabel);

            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setVisible(true);
        }
    }
}