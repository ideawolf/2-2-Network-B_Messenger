package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientUser {

    JSONArray friendList;

    String id = "아이디";
    String name = "이름";
    String nickname = "별명";
    String email = "이메일";
    String birth = "생일";

    int isOnline;

    String lastOnline;

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

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(String lastOnline) {
        this.lastOnline = lastOnline;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public ClientUser(String accessToken) {
        try {
            JSONObject json = new JSONObject();
            json.put("command", "GET_FRIENDS");
            json.put("access-token", accessToken);
            Socket socket = new Socket("localhost", 35014);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.write(json.toString());
            out.newLine();
            out.flush();

            String response_str = in.readLine();

            JSONObject response = new JSONObject(response_str);
            System.out.println("reponse: " + response);

            friendList = response.getJSONArray("body");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        try {
            JSONObject json = new JSONObject();
            json.put("command", "GET_USER_INFO");
            json.put("access-token", accessToken);
            Socket socket = new Socket("localhost", 35014);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.write(json.toString());
            out.newLine();
            out.flush();
//
//            int attempts = 0;
//            while(!in.ready() && attempts < 1000)
//            {
//                attempts++;
//                Thread.sleep(10);
//            }

            String response_str = in.readLine();

            JSONObject response = new JSONObject(response_str);
            JSONObject userInfo = response.getJSONObject("body");

            System.out.println(userInfo);

            setId(userInfo.getString("user_id"));
            setName(userInfo.getString("name"));
            setNickname(userInfo.getString("nickname"));
            setEmail(userInfo.getString("email"));
            setBirth(userInfo.getString("birthday"));
            setIsOnline(Integer.parseInt(userInfo.getString("isOnline")));
            setLastOnline(userInfo.getString("last_online"));

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
