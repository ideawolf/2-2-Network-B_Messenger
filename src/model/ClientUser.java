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

    public ClientUser(String accessToken) {
        try {
            JSONObject json = new JSONObject();
            json.put("command", "GET_FRIENDS");
            json.put("access-token", "00000000-0000-0000-0000-000000000001");
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
    }
}
