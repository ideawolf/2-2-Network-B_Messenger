import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
public class ChatMain extends JFrame {

    public static void main(String[] args) {
        new ChatMain();
    }

    ChatMain() {
        setSize(800,600);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        // 내 정보
        // 상세정보 : Id, 이름, 별명, 오늘의 한마디
        // 메뉴 : 내 정보 변경

        // 호출되면 내 정보를 불러와서 각 변수에 넣어줌
        String username = "유저명";
        String userNickname = "유저 별명";
        String dailyWordLabel = "오늘의 한마디";

        // 유저 이름 집어넣어야됨
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        usernameLabel.setBounds(0,25,200,30);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(usernameLabel);

        JLabel userNicknameLabel = new JLabel(userNickname);
        userNicknameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        userNicknameLabel.setBounds(0, 50, 200, 30);
        userNicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(userNicknameLabel);

        JLabel userDailyWordLabel = new JLabel(dailyWordLabel);
        userDailyWordLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        userDailyWordLabel.setBounds(0,75,200,30);
        userDailyWordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(userDailyWordLabel);

        JButton userInfoEdit = new JButton("정보 변경");
        userInfoEdit.setBounds(250,50,100,30);
        userInfoEdit.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        userInfoEdit.setHorizontalAlignment(JLabel.CENTER);
        userInfoEdit.setForeground(Color.BLACK);
        userInfoEdit.setBackground(Color.WHITE);
        userInfoEdit.setFocusPainted(false);
        userInfoEdit.setBorder(new LineBorder(new Color(0x8EAADB), 2, true));
        add(userInfoEdit);

        // 친구 리스트
        // 메뉴(우클릭) : 상세 정보, 1대1 채팅
        // 상세정보 : Id, 이름, 온라인 여부, 최종 접속 시간

        JLabel friendListDesc = new JLabel("친구목록");
        friendListDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        friendListDesc.setBounds(100,100,200,50);
        friendListDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(friendListDesc);

        JPanel friendList = new JPanel();
        friendList.setBackground(new Color(0xF4F3FF));
        friendList.setLayout(new BoxLayout(friendList, BoxLayout.Y_AXIS));
        JScrollPane friendListScroll = new JScrollPane(friendList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        friendListScroll.getVerticalScrollBar().setUnitIncrement(15);
        friendListScroll.setBounds(50,150,300,250);
        add(friendListScroll);

        // 여기서 친구 리스트를 불러온 뒤 반복문을 통해 데이터를 넣음
        // friend 클래스에 친구 이름을 넣어서 리스트에 추가함
        friendList.add(new friend("류관곤"));
        friendList.add(new friend("류관곤"));
        friendList.add(new friend("류관곤"));
        friendList.add(new friend("류관곤"));
        friendList.add(new friend("류관곤"));
        friendList.add(new friend("류관곤"));
        friendList.add(new friend("류관곤"));
        friendList.add(new friend("류관곤"));
        friendList.add(new friend("류관곤"));
        friendList.add(new friend("류관곤"));
        friendList.add(new friend("류관곤"));
        friendList.add(new friend("류관곤"));
        friendList.add(new friend("류관곤"));
        friendList.add(new friend("류관곤"));
        friendList.add(new friend("류관곤"));

        // 리스트 아래에 실시간 공공정보


        // 유저 검색
        // 검색된 유저 리스트
        // 메뉴 : 상세 정보, 친구 등록

        JLabel userSearchDesc = new JLabel("유저검색");
        userSearchDesc.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        userSearchDesc.setBounds(425,25,50,30);
        userSearchDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(userSearchDesc);

        JTextField userSearchField = new JTextField(20);
        userSearchField.setBounds(500,25,250,30);
        add(userSearchField);

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
            }
        });

        // 검색결과
        JPanel userSearchList = new JPanel();
        userSearchList.setBackground(new Color(0xF4F3FF));
        userSearchList.setLayout(new BoxLayout(userSearchList, BoxLayout.Y_AXIS));
        JScrollPane userSearchListScroll = new JScrollPane(userSearchList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        userSearchListScroll.getVerticalScrollBar().setUnitIncrement(15);
        userSearchListScroll.setBounds(425,100,325,400);
        add(userSearchListScroll);

        // 반복문 시작

        // 반복문 끝

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    static class friend extends JLabel {

        friend(String friendName)
        {
            super(friendName);
            setFont(new Font("맑은 고딕", Font.BOLD, 12));
            Container comp = this;
            setBorder(new EmptyBorder(10,20,10,0));

            /*
             * 우클릭 메뉴
             */
            PopupMenu pm1 = new PopupMenu("Edit");

            MenuItem item1 = new MenuItem("See detail");
            MenuItem item2 = new MenuItem("1:1 Chatting");

            pm1.add(item1);
            pm1.add(item2);

            item1.addActionListener(e -> {
                // 상세 정보 화면 호출
                new detailInfo("대상 아이디");
            });

            item2.addActionListener(e -> {
                // 채팅 시작
            });

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if(e.getButton()==MouseEvent.BUTTON3)
                        pm1.show(comp, e.getX(), e.getY());
                }
            });
            add(pm1);
        }
    }

    static class searched extends JLabel {

        searched(String userName){
            super(userName);
            setFont(new Font("맑은 고딕", Font.BOLD, 12));
            Container comp = this;
            setBorder(new EmptyBorder(20,20,20,0));

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
                new detailInfo("대상 아이디");
            });

            item2.addActionListener(e -> {
                // 채팅 시작
            });

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if(e.getButton()==MouseEvent.BUTTON3)
                        pm1.show(comp, e.getX(), e.getY());
                }
            });
            add(pm1);
        }
    }

    static class detailInfo extends JFrame {

        detailInfo(String Id) {
            super("유저 상세정보");
            setSize(300,225);
            setResizable(false);
            setLocationRelativeTo(null);
            setLayout(null);
            getContentPane().setBackground(Color.WHITE);

            JLabel userIdLabel = new JLabel("아이디 : " + "ID");
            userIdLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            userIdLabel.setBounds(50,25,200,30);
            userIdLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(userIdLabel);

            JLabel usernameLabel = new JLabel("이름 : " + "이름");
            usernameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            usernameLabel.setBounds(50,50,200,30);
            usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(usernameLabel);

            JLabel userNicknameLabel = new JLabel("별명 : " + "별명");
            userNicknameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            userNicknameLabel.setBounds(50,75,200,30);
            userNicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(userNicknameLabel);

            JLabel userOnOffStateLabel = new JLabel("온/오프라인");
            userOnOffStateLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            userOnOffStateLabel.setBounds(50,100,200,30);
            userOnOffStateLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(userOnOffStateLabel);

            JLabel lastConTimeLabel = new JLabel("시간");
            lastConTimeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            lastConTimeLabel.setBounds(50,125,200,30);
            lastConTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(lastConTimeLabel);

            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setVisible(true);
        }
    }
}
