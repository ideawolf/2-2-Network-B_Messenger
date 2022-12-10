import javax.swing.*;
import java.awt.*;

public class ChatRoom extends Frame {
    ChatRoom() {
        setSize(600, 600);
        setLayout(new FlowLayout());

        TextArea chatList = new TextArea("하고 싶은 말을 적으세요.", 30, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);
        chatList.setEditable(false);

        TextArea sendArea = new TextArea("적을 내용", 4, 65, TextArea.SCROLLBARS_NONE);

        JButton sendButton = new JButton("보내기");
        sendButton.setPreferredSize(new Dimension(80, 80));


        add(chatList);
        add(sendArea);
        add(sendButton);

        setVisible(true);
    }
}