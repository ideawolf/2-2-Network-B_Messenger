import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ChatMain {
    private JPanel mainPanel;
    private JTextField searchField;
    private JScrollPane friendList;
    private JButton searchButton;

    public ChatMain(JFrame frame) throws IOException {

        PopupMenu pm1 = new PopupMenu("Edit");

        MenuItem item1 = new MenuItem("Copy");
        MenuItem item2 = new MenuItem("Cut");
        MenuItem item3 = new MenuItem("Paste");

        pm1.add(item1);
        pm1.add(item2);
        pm1.add(item3);

        friendList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==MouseEvent.BUTTON3)
                    pm1.show(friendList, e.getX(), e.getY());
            }
        });
        friendList.add(pm1);


        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
