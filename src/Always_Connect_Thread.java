import org.json.JSONObject;

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
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
