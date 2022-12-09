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
        usernameLabel.setBounds(0,0,200,50);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(usernameLabel);

        JLabel userNicknameLabel = new JLabel("유저 별명");
        userNicknameLabel.setBounds(0, 50, 200, 50);
        userNicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(userNicknameLabel);

        JLabel userDailyWordLabel = new JLabel("오늘의 한마디");
        userDailyWordLabel.setBounds(200,25,200,50);
        userDailyWordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(userDailyWordLabel);

        JLabel friendListDesc = new JLabel("친구목록");
        friendListDesc.setBounds(100,100,200,50);
        friendListDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(friendListDesc);

        JPanel friendList = new JPanel();
        friendList.setLayout(new BoxLayout(friendList, BoxLayout.Y_AXIS));
        JScrollPane friendListScroll = new JScrollPane(friendList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        friendListScroll.getVerticalScrollBar().setUnitIncrement(20);
        friendListScroll.setBounds(50,150,300,400);
        add(friendListScroll);

        JLabel friend = new JLabel("류관곤");
        friend.setBorder(new EmptyBorder(20,20,20,0));
        JLabel friend2 = new JLabel("류관곤2");
        friend2.setBorder(new EmptyBorder(20,20,20,0));
        JLabel friend3 = new JLabel("류관곤");
        friend3.setBorder(new EmptyBorder(20,20,20,0));
        JLabel friend4 = new JLabel("류관곤2");
        friend4.setBorder(new EmptyBorder(20,20,20,0));
        JLabel friend5 = new JLabel("류관곤");
        friend5.setBorder(new EmptyBorder(20,20,20,0));
        JLabel friend6 = new JLabel("류관곤2");
        friend6.setBorder(new EmptyBorder(20,20,20,0));
        JLabel friend7 = new JLabel("류관곤2");
        friend7.setBorder(new EmptyBorder(20,20,20,0));
        friendList.add(friend);
        friendList.add(friend2);
        friendList.add(friend3);
        friendList.add(friend4);
        friendList.add(friend5);
        friendList.add(friend6);
        friendList.add(friend7);

        // 친구 리스트
        // 메뉴(우클릭) : 상세 정보, 1대1 채팅
        // 상세정보 : Id, 이름, 온라인 여부, 최종 접속 시간

        // 유저 검색
        // 검색된 유저 리스트
        // 메뉴 : 상세 정보, 친구 등록

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

}
