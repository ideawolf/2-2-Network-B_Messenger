import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ChatRoom extends JFrame {

    ImageIcon file_upload_img = new ImageIcon("images/file_upload.png");



    ChatRoom(int room_id) {
        setSize(600, 540);
        setLayout(new FlowLayout());
        setBackground(Color.gray);

        JTextArea chatList = new JTextArea();
        chatList.setPreferredSize(new Dimension(560, 400));
        chatList.setEditable(false);
        chatList.setBackground(Color.white);

        JButton fileSendButton = new JButton(file_upload_img);
        fileSendButton.setPreferredSize(new Dimension(80, 80));

        JTextArea sendArea = new JTextArea();
        sendArea.setPreferredSize(new Dimension(390, 80));

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

        fileSendButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(this);
            StringTokenizer tokenizer = new StringTokenizer(fileChooser.getSelectedFile().toString(), "\\");
            ArrayList<String> splitStr = new ArrayList<>();
            while(tokenizer.hasMoreTokens()) {
                splitStr.add(tokenizer.nextToken());
            }
            int option = JOptionPane.showOptionDialog(null, splitStr.get(splitStr.size() - 1) + " 파일을 전송하시겠습니까?", "알림", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"보내기", "취소"}, "보내기");
            if (option == 0) {
                chatList.append("유저가 파일을 전송하려고 합니다\n");


                //function.FileIO.FileSend(fileChooser.getSelectedFile());
            }
        });


        setVisible(true);

    }


}