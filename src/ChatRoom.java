import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatRoom extends Frame {
    public static void main(String[] args) {
        new ChatRoom();
    }

    ImageIcon file_upload_img = new ImageIcon("images/file_upload.png");

    ChatRoom() {
        setSize(600, 600);
        setLayout(new FlowLayout());

        TextArea chatList = new TextArea("하고 싶은 말을 적으세요.", 30, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);
        chatList.setEditable(false);

        JButton fileSendButton = new JButton(file_upload_img);
        fileSendButton.setPreferredSize(new Dimension(40, 40));

        TextArea sendArea = new TextArea("적을 내용", 4, 60, TextArea.SCROLLBARS_NONE);

        JButton sendButton = new JButton("보내기");
        sendButton.setPreferredSize(new Dimension(80, 80));

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
                }
            }
        });

        fileSendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        setVisible(true);

    }


}