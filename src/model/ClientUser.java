package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class ClientUser {
    private final String accessToken;
    private JSONArray friendList;

    private String id;
    private String name;
    private String nickname;
    private String email;
    private String birth;
    private String statusMessage;

    public JSONArray getFriendList() {
        return friendList;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getBirth() {
        return birth;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public ClientUser(String accessToken) {
        try {
            this.accessToken = accessToken;
            getUserInfo();
            getFriendListInfo();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void getUserInfo() {
        try {
            Socket socket = new Socket("localhost", 35014);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("command", "GET_USER_INFO");
            jsonObject.put("access-token", this.accessToken);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.write(jsonObject.toString());
            out.newLine();
            out.flush();

            String response_str = in.readLine();

            JSONObject response = new JSONObject(response_str);
            JSONObject userInfo = response.getJSONObject("body");

            this.id = userInfo.getString("user_id");
            this.name = userInfo.getString("name");
            this.nickname = userInfo.getString("nickname");
            this.email = userInfo.getString("email");
            this.birth = userInfo.getString("birthday");
            this.statusMessage = userInfo.getString("status_message");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getFriendListInfo() {
        try {
            Socket socket = new Socket("localhost", 35014);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("command", "GET_FRIENDS");
            jsonObject.put("access-token", this.accessToken);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.write(jsonObject.toString());
            out.newLine();
            out.flush();

            String response_str = in.readLine();
            if (response_str != null) {
                JSONObject response = new JSONObject(response_str);
                friendList = response.getJSONArray("body");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editInfo(String nickname, String statusMessage) {
        this.nickname = nickname;
        this.statusMessage = statusMessage;
        try {
            Socket socket = new Socket("localhost", 35014);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("command", "EDIT_INFO");
            jsonObject.put("access-token", this.accessToken);
            jsonObject.put("nickname", nickname);
            jsonObject.put("statusMessage", statusMessage);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.write(jsonObject.toString());
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

class always_connect_class extends Thread {
    BufferedReader in;
    public always_connect_class(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {

    }
}
