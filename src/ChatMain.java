import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        setBackground(Color.WHITE);

        // 내 정보
        // 상세정보 : Id, 이름, 별명, 오늘의 한마디
        // 메뉴 : 내 정보 변경

        // 유저 이름 집어넣어야됨
        JLabel usernameLabel = new JLabel("유저 이름");
        usernameLabel.setBounds(0,0,200,30);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(usernameLabel);

        JLabel userNicknameLabel = new JLabel("유저 별명");
        userNicknameLabel.setBounds(0, 50, 200, 30);
        userNicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(userNicknameLabel);

        JLabel userDailyWordLabel = new JLabel("오늘의 한마디");
        userDailyWordLabel.setBounds(200,25,200,30);
        userDailyWordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(userDailyWordLabel);

        // 친구 리스트
        // 메뉴(우클릭) : 상세 정보, 1대1 채팅
        // 상세정보 : Id, 이름, 온라인 여부, 최종 접속 시간

        JLabel friendListDesc = new JLabel("친구목록");
        friendListDesc.setBounds(100,100,200,50);
        friendListDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(friendListDesc);

        JPanel friendList = new JPanel();
        friendList.setLayout(new BoxLayout(friendList, BoxLayout.Y_AXIS));
        JScrollPane friendListScroll = new JScrollPane(friendList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        friendListScroll.getVerticalScrollBar().setUnitIncrement(20);
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
        userSearchDesc.setBounds(425,25,50,30);
        userSearchDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(userSearchDesc);

        JTextField userSearchField = new JTextField(20);
        userSearchField.setBounds(500,25,250,30);
        add(userSearchField);

        userSearchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }
            public void removeUpdate(DocumentEvent e) {
                warn();
            }
            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                System.out.println("changed!");
            }
        });

        // 검색결과
        JPanel userSearchList = new JPanel();
        userSearchList.setLayout(new BoxLayout(userSearchList, BoxLayout.Y_AXIS));
        JScrollPane userSearchListScroll = new JScrollPane(userSearchList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        userSearchListScroll.getVerticalScrollBar().setUnitIncrement(20);
        userSearchListScroll.setBounds(450,100,300,450);
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
            Container comp = this;
            setBorder(new EmptyBorder(20,20,20,0));

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
}
