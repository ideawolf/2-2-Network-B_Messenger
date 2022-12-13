import function.FileIO;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.StringTokenizer;

import model.ClientUser;
import org.json.JSONObject;
import org.json.JSONArray;

public class ChatMain extends JFrame {


    ClientUser USER;

    JPanel friendList;
    JScrollPane friendListScroll;
    private JLabel usernameLabel;
    private JLabel userNicknameLabel;
    private JLabel userStatusMessageLabel;
    public File recentFile;

    HashMap<Integer, ChatRoom> chatRoom = new HashMap<>();

    public void reloadFriendList() {
        USER.getFriendListInfo();

        friendList.removeAll();
        if (USER.getFriendList() != null) {
            for (int i = 0; i < USER.getFriendList().length(); i++)
                friendList.add(new friend(USER.getFriendList().getJSONObject(i), this));
        }
        friendListScroll.setViewportView(friendList);
    }

    public void reloadUserInfo() {
        USER.getUserInfo();
        usernameLabel.setText(USER.getName());
        userNicknameLabel.setText(USER.getNickname());
        userStatusMessageLabel.setText(USER.getStatusMessage());
    }

    public void receiveFile(String sender_id, String name, String fileName) {
        int option = JOptionPane.showOptionDialog(null, name + "(으)로부터 파일을 받으시겠습니까?", "알림", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"확인", "취소"}, "확인");
        if (option == 0) {
            try {
                JSONObject json = new JSONObject();
                json.put("command", "ACCEPT_FILE");
                json.put("access-token", USER.getAccessToken());
                json.put("to_sender", sender_id);

                Socket socket = new Socket("localhost", 35014);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.write(json.toString());
                out.newLine();
                out.flush();

                FileIO.FileReceive(fileName);

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void sendFile() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        FileIO.FileSend(recentFile);
    }

    public void deleteRoom(int room_id) {
        JOptionPane.showOptionDialog(null, "채팅방에 남은 인원이 없습니다.", "채팅 종료",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"나가기"}, "나가기");
        chatRoom.get(room_id).dispose();
        chatRoom.remove(room_id);
    }

    public void invited(int room_id) {
        int option = JOptionPane.showOptionDialog(null, "채팅에 초대되었습니다.", "알림",
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"거부", "수락"}, "수락");
        try {
            JSONObject json = new JSONObject();
            if(option == 0)
            {
                json.put("command", "REJECT_INVITE");
            }
            else{
                json.put("command", "ACCEPT_INVITE");
            }
            json.put("access-token", USER.getAccessToken());
            json.put("room_id", room_id);

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

            if(response.getInt("status") == 200 && option == 1)
            {
                chatRoom.put(room_id, new ChatRoom(room_id, USER, this));
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void receiveMessage(JSONObject response) {
        chatRoom.get(response.getInt("room_id")).receiveMessage(response);
    }

    public void leftMessage(JSONObject response) {
        if(chatRoom.get(response.getInt("room_id")) != null)
            chatRoom.get(response.getInt("room_id")).leftMessage(response);
    }

    public static void main(String[] args) {
        new ChatMain("00000000-0000-0000-0000-000000000001");
    }

    ChatMain(String accessToken) {
        setSize(800, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        USER = new ClientUser(accessToken);
        System.out.println(USER.getAccessToken());

        // 내 정보
        // 상세정보 : Id, 이름, 별명, 오늘의 한마디
        // 메뉴 : 내 정보 변경

        // 호출되면 내 정보를 불러와서 각 변수에 넣어줌
        String username = USER.getName();
        String userNickname = USER.getNickname();
        String userStatusMessage = USER.getStatusMessage();


        // 유저 이름 집어넣어야됨
        usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        usernameLabel.setBounds(0, 25, 200, 30);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(usernameLabel);

        userNicknameLabel = new JLabel(userNickname);
        userNicknameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        userNicknameLabel.setBounds(0, 50, 200, 30);
        userNicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(userNicknameLabel);

        userStatusMessageLabel = new JLabel(userStatusMessage);
        userStatusMessageLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        userStatusMessageLabel.setBounds(0, 75, 200, 30);
        userStatusMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(userStatusMessageLabel);

        JButton userInfoEdit = new JButton("정보 변경");
        userInfoEdit.setBounds(250, 50, 75, 30);
        userInfoEdit.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        userInfoEdit.setHorizontalAlignment(JLabel.CENTER);
        userInfoEdit.setForeground(Color.BLACK);
        userInfoEdit.setBackground(Color.WHITE);
        userInfoEdit.setFocusPainted(false);
        userInfoEdit.setBorder(new LineBorder(new Color(0x8EAADB), 2, true));

        userInfoEdit.addActionListener(e -> {
            new infoEditor(USER.getNickname(), USER.getStatusMessage(), this);
        });

        add(userInfoEdit);

        // 친구 리스트
        // 메뉴(우클릭) : 상세 정보, 1대1 채팅
        // 상세정보 : Id, 이름, 온라인 여부, 최종 접속 시간

        JLabel friendListDesc = new JLabel("친구목록");
        friendListDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        friendListDesc.setBounds(100, 100, 200, 50);
        friendListDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(friendListDesc);

        friendList = new JPanel();
        friendList.setBackground(new Color(0xF4F3FF));
        friendList.setLayout(new BoxLayout(friendList, BoxLayout.Y_AXIS));
        friendListScroll = new JScrollPane(friendList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        friendListScroll.getVerticalScrollBar().setUnitIncrement(15);
        friendListScroll.setBounds(50, 150, 300, 250);
        add(friendListScroll);


        // 여기서 친구 리스트를 불러온 뒤 반복문을 통해 데이터를 넣음
        // friend 클래스에 친구 이름을 넣어서 리스트에 추가함
        if (USER.getFriendList() != null) {
            for (int i = 0; i < USER.getFriendList().length(); i++)
                friendList.add(new friend(USER.getFriendList().getJSONObject(i), this));
        }

        // 리스트 아래에 실시간 공공정보

        // 공공 데이터
        // 1시간 단위의 데이터로 현재 시간과 가까운 날씨 예보를 받아와서 온도만 출력
        // 장소는 가천대학교를 가정하여 성남시 수정구 복정동의 위도 및 경도를 사용
        JLabel dataLabel = new JLabel("가천대학교 향후 6시간 기온 정보");
        dataLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        dataLabel.setBounds(100, 400, 200, 50);
        dataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(dataLabel);

        JSONArray weatherData = getWeatherData();
        String[] header = new String[6];
        String[][] contents = new String[1][6];
        for (int i = 0; i < weatherData.length(); i++) {
            header[i] = weatherData.getJSONObject(i).getString("fcstTime");
            header[i] = header[i].substring(0, 2) + ":" + header[i].substring(2, 4);
            contents[0][i] = weatherData.getJSONObject(i).getString("fcstValue") + "°C";
            //System.out.println(weatherData.getJSONObject(i));
        }

        JTable jTable = new JTable(contents, header);
        // 데이터 내용 가운데 정렬
        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < jTable.getColumnModel().getColumnCount(); i++) {
            jTable.getColumnModel().getColumn(i).setCellRenderer(dtcr);
        }
        JScrollPane scrolledTable = new JScrollPane(jTable);    //스크롤 될 수 있도록 JScrollPane 적용
        scrolledTable.setBackground(new Color(0xF4F3FF));
        scrolledTable.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 1),
                new EmptyBorder(10, 10, 10, 10)));
        //scrolledTable.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        scrolledTable.setBounds(50, 440, 300, 60);
        add(scrolledTable);

        // 유저 검색
        // 검색된 유저 리스트
        // 메뉴 : 상세 정보, 친구 등록

        JLabel userSearchDesc = new JLabel("유저검색");
        userSearchDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        userSearchDesc.setBounds(425, 25, 50, 30);
        userSearchDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(userSearchDesc);

        JTextField userSearchField = new JTextField(20);
        userSearchField.setBounds(500, 25, 225, 30);
        add(userSearchField);

        // 검색결과
        JPanel userSearchList = new JPanel();
        userSearchList.setBackground(new Color(0xF4F3FF));
        userSearchList.setLayout(new BoxLayout(userSearchList, BoxLayout.Y_AXIS));
        JScrollPane userSearchListScroll = new JScrollPane(userSearchList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        userSearchListScroll.getVerticalScrollBar().setUnitIncrement(15);
        userSearchListScroll.setBounds(425, 100, 325, 400);
        add(userSearchListScroll);

        // 초기 userSearchList
        try {
            JSONObject json = new JSONObject();
            json.put("command", "SEARCH");
            json.put("search_keyword", userSearchField.getText());
            Socket socket = new Socket("localhost", 35014);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            out.write(json.toString());
            out.newLine();
            out.flush();

            String response_str = in.readLine();

            JSONObject response = new JSONObject(response_str);

            System.out.println("response: " + response);

            JSONArray searchList = response.getJSONArray("body");

            for (int i = 0; i < searchList.length(); i++) {
                userSearchList.add(new searched(searchList.getJSONObject(i), USER.getId()));
            }
            userSearchListScroll.setViewportView(userSearchList);


        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }


        userSearchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                search();
            }

            public void removeUpdate(DocumentEvent e) {
                search();
            }

            public void insertUpdate(DocumentEvent e) {
                search();
            }

            public void search() {
                System.out.println("changed!");
                // 검색창에 입력할때마다 검색
                try {
                    JSONObject json = new JSONObject();
                    json.put("command", "SEARCH");
                    json.put("search_keyword", userSearchField.getText());
                    Socket socket = new Socket("localhost", 35014);
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                    out.write(json.toString());
                    out.newLine();
                    out.flush();

                    String response_str = in.readLine();

                    JSONObject response = new JSONObject(response_str);

                    System.out.println("response: " + response);

                    JSONArray searchList = response.getJSONArray("body");

                    if (searchList != null) {
                        userSearchList.removeAll();
                        if (!userSearchField.getText().equals("")) {
                            for (int i = 0; i < searchList.length(); i++)
                                userSearchList.add(new searched(searchList.getJSONObject(i), USER.getId()));

                            userSearchListScroll.setViewportView(userSearchList);
                        }
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                int option = JOptionPane.showOptionDialog(null, "               메신저를 종료하시겠습니까?", "알림", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"나가기", "취소"}, "나가기");
                if (option == 0) {
                    USER.updateOnline("0");
                    System.exit(0);
                }
            }
        });
    }

    class friend extends JPanel {

        friend(JSONObject friend, ChatMain chatMain) {
            super(null);
            Container comp = this;
            setPreferredSize(new Dimension(600,51));
            setMaximumSize(new Dimension(600, 51));
            setBorder(new EmptyBorder(10, 20, 10, 0));
            setBackground(new Color(0xF4F3FF));

            JLabel name = new JLabel(friend.getString("name"));
            name.setBounds(10, 10, 90, 30);
            name.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            add(name);

            JLabel isOnline = new JLabel();
            isOnline.setBounds(100, 10, 50, 30);
            isOnline.setFont(new Font("맑은 고딕", Font.BOLD, 10));
            isOnline.setHorizontalAlignment(SwingConstants.CENTER);
            if (friend.getString("isOnline").equals("1")) {
                isOnline.setText("온라인");
                isOnline.setForeground(Color.GREEN);
            } else {
                isOnline.setText("오프라인");
                isOnline.setForeground(Color.RED);
            }
            add(isOnline);

            JLabel dailyWord = new JLabel(friend.getString("status_message"));
            dailyWord.setBounds(150, 10, 125, 30);
            dailyWord.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            dailyWord.setHorizontalAlignment(SwingConstants.RIGHT);
            add(dailyWord);

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
            MenuItem item2 = new MenuItem("1:1 Chatting");
            MenuItem item3 = new MenuItem("Send a file");

            pm1.add(item1);
            pm1.add(item2);
            pm1.add(item3);

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
                // 채팅 시작
                if (friend.getString("isOnline").equals("1")) {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("command", "CREATE_ROOM");
                        json.put("access-token", USER.getAccessToken());

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

                        chatRoom.put(response.getInt("room_id"), new ChatRoom(response.getInt("room_id"), USER, chatMain));

                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }

            });

            item3.addActionListener(e -> {
                // 파일 보내기
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
                        json.put("command", "SEND_FILE");
                        json.put("access-token", USER.getAccessToken());

                        JSONArray invite_user_list = new JSONArray();
                        invite_user_list.put(friend.getString("user_id"));
                        json.put("userlist", invite_user_list);
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

    class searched extends JPanel {

        searched(JSONObject user, String myId) {
            super(null);
            Container comp = this;
            setPreferredSize(new Dimension(300,51));
            setMaximumSize(new Dimension(650, 51));
            setBorder(new EmptyBorder(10, 20, 10, 0));
            setBackground(new Color(0xF4F3FF));

            JLabel name = new JLabel(user.getString("name"));
            name.setBounds(10, 10, 90, 30);
            name.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            add(name);

            JLabel dailyWord = new JLabel(user.getString("status_message"));
            dailyWord.setBounds(100, 10, 200, 30);
            dailyWord.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            dailyWord.setHorizontalAlignment(SwingConstants.RIGHT);
            add(dailyWord);

            JLabel line = new JLabel();
            line.setBounds(0, 50, 325, 1);
            line.setBackground(Color.GRAY);
            line.setOpaque(true);
            add(line);

            /*
             * 우클릭 메뉴
             */
            PopupMenu pm1 = new PopupMenu("Edit");

            MenuItem item1 = new MenuItem("See detail");
            MenuItem item2 = new MenuItem("Add friend");

            pm1.add(item1);
            pm1.add(item2);

            item1.addActionListener(e -> {
                // 상세 정보 화면 호출
                new detailInfo(user.getString("user_id"),
                        user.getString("name"),
                        user.getString("nickname"),
                        user.getString("email"),
                        user.getString("isOnline"),
                        user.getString("last_online"));
            });

            item2.addActionListener(e -> {
                // 이미 있는 친구인지 확인
                JSONArray friendList = USER.getFriendList();
                for (int i = 0; i < friendList.length(); i++) {
                    if (friendList.getJSONObject(i).getString("user_id").equals(user.getString("user_id"))) {
                        JOptionPane.showOptionDialog(null, "이미 친구입니다", "알림",
                                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"닫기"}, "닫기");
                        return;
                    }
                }
                // 친구 추가
                try {
                    JSONObject json = new JSONObject();
                    json.put("command", "ADD_FRIEND");
                    json.put("id", myId);
                    json.put("friend_id", user.getString("user_id"));
                    Socket socket = new Socket("localhost", 35014);
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    out.write(json.toString());
                    out.newLine();
                    out.flush();

                    String response_str = in.readLine();

                    JSONObject response = new JSONObject(response_str);

                    int status = response.getInt("status");
                    String body = response.getString("body");

                    System.out.println("Status : " + status);
                    System.out.println("body : " + body);

                    reloadFriendList();

                    JOptionPane.showOptionDialog(null, "친구추가 완료", "알림",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"닫기"}, "닫기");
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
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

    class infoEditor extends JFrame {

        infoEditor(String nickname, String statusMessage, ChatMain chatMain) {
            super("정보 변경");
            setSize(400, 300);
            setResizable(false);
            setLocationRelativeTo(null);
            setLayout(null);
            getContentPane().setBackground(Color.WHITE);

            JLabel description = new JLabel("정보 수정");
            description.setBounds(50, 25, 300, 30);
            description.setFont(new Font("맑은 고딕", Font.BOLD, 16));
            description.setHorizontalAlignment(SwingConstants.CENTER);
            add(description);

            // 별명과 오늘의 한마디 변경 가능
            JLabel nicknameDesc = new JLabel("별명 : ");
            nicknameDesc.setBounds(50, 75, 100, 30);
            nicknameDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            nicknameDesc.setHorizontalAlignment(SwingConstants.CENTER);
            add(nicknameDesc);

            JTextField nicknameField = new JTextField(nickname);
            nicknameField.setBounds(150, 75, 200, 30);
            nicknameField.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            nicknameField.setHorizontalAlignment(SwingConstants.CENTER);
            add(nicknameField);

            JLabel statusMessageDesc = new JLabel("오늘의 한마디 :");
            statusMessageDesc.setBounds(50, 125, 100, 30);
            statusMessageDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            statusMessageDesc.setHorizontalAlignment(SwingConstants.CENTER);
            add(statusMessageDesc);

            JTextField statusMessageField = new JTextField(statusMessage);
            statusMessageField.setBounds(150, 125, 200, 30);
            statusMessageField.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            statusMessageField.setHorizontalAlignment(SwingConstants.CENTER);
            add(statusMessageField);

            JButton editButton = new JButton("변경!");
            editButton.setBounds(100, 175, 200, 30);
            editButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            editButton.setHorizontalAlignment(SwingConstants.CENTER);
            editButton.setForeground(Color.BLACK);
            editButton.setBackground(Color.WHITE);
            editButton.setFocusPainted(false);
            editButton.setBorder(new LineBorder(new Color(0x8EAADB), 2, true));
            add(editButton);

            editButton.addActionListener(e -> {
                if (!(nickname.equals(nicknameField.getText()) && statusMessage.equals(statusMessageField.getText()))) {
                    USER.editInfo(nicknameField.getText(), statusMessageField.getText());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    chatMain.reloadUserInfo();
                    // 모든 유저들에게 별명과 상메가 바뀌었다는 메시지를 보내야함
                    dispose();
                }
            });


            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setVisible(true);
        }
    }

    public static JSONArray getWeatherData() {

        // 현재 시간으로부터 1시간을 빼고 30분 단위를 맞출 수 있도록 또 시간을 뺀 이후 api 파라미터에 맞게 가공

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalTime now = LocalTime.now().minusHours(1);
        now = now.minusMinutes(now.getMinute() % 30);
        if (now.getHour() == 23) today = today.minusDays(1);
        String searchDate = today.format(formatter);

        formatter = DateTimeFormatter.ofPattern("HHmm");
        String searchTime = now.format(formatter);


        // 공공 데이터 포탈 api 예제 활용

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst"); /*URL*/
        urlBuilder.append("?").append(URLEncoder.encode("serviceKey", StandardCharsets.UTF_8)).append("=fJvYA1F%2BhGU1THwsH8VBFcneVsHQ95VLp5Osk145um6KQ%2FfYqEeVD4CWpeRGi8ZrwIMN7AUI3Ndto22tiGkgxw%3D%3D"); /*Service Key*/
        urlBuilder.append("&").append(URLEncoder.encode("pageNo", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("1", StandardCharsets.UTF_8)); /*페이지번호*/
        urlBuilder.append("&").append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("1000", StandardCharsets.UTF_8)); /*한 페이지 결과 수*/
        urlBuilder.append("&").append(URLEncoder.encode("dataType", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("JSON", StandardCharsets.UTF_8)); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&").append(URLEncoder.encode("base_date", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(searchDate, StandardCharsets.UTF_8)); /*‘21년 6월 28일 발표*/
        urlBuilder.append("&").append(URLEncoder.encode("base_time", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(searchTime, StandardCharsets.UTF_8)); /*06시 발표(정시단위) */
        urlBuilder.append("&").append(URLEncoder.encode("nx", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("62", StandardCharsets.UTF_8)); /*예보지점의 X 좌표값*/
        urlBuilder.append("&").append(URLEncoder.encode("ny", StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode("124", StandardCharsets.UTF_8)); /*예보지점의 Y 좌표값*/
        URL url;
        JSONArray ret = new JSONArray();
        try {
            url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            JSONArray jsonArray = new JSONObject(sb.toString()).getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (Objects.equals(obj.getString("category"), "T1H")) {
                    ret.put(obj);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
