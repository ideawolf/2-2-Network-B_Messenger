import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;

public class Always_Connect_Thread extends Thread {
    BufferedReader in;

    ChatMain chatMain;

    public Always_Connect_Thread(BufferedReader in, ChatMain chatMain) {
        this.in = in;
        this.chatMain = chatMain;
    }

    @Override
    public void run() {
        try{
            while(true){
                String response_str = in.readLine();

                if(response_str == null){
                    Thread.sleep(10);
                    continue;
                }
                JSONObject response = new  JSONObject(response_str);

                System.out.println("New Message: " + response);

                if(response.getString("command").equals("info_edited"))
                {
                    chatMain.reloadFriendList();
                }
                if(response.getString("command").equals("invited"))
                {
                    chatMain.invited(response.getInt("room_id"));
                }
                if(response.getString("command").equals("invite_rejected"))
                {
                    if(response.getInt("remain") == 1)
                    {
                        chatMain.deleteRoom(response.getInt("room_id"));
                    }
                }
                if(response.getString("command").equals("file_receive")) {
                    chatMain.receiveFile(response.getString("sender_id"), response.getString("file_name"));
                }
                if(response.getString("command").equals("file_send")) {
                    chatMain.sendFile();
                }
                if(response.get("command").equals("receive_message"))
                {
                    if(!response.getString("sender").equals(chatMain.USER.getId()))
                        chatMain.receiveMessage(response);
                }
                if(response.get("command").equals("someone_leave"))
                {
                    if(response.getInt("remain") == 1)
                    {
                        chatMain.deleteRoom(response.getInt("room_id"));
                    }
                    chatMain.leftMessage(response);
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
