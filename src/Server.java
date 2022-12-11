import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.*;

//import com.auth0.jwt.JWT;
import model.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class Server {
    static Map<UUID, String> user_token_Map = new HashMap<>();
    public static void main(String[] args) throws IOException {
        user_token_Map.put(UUID.fromString("00000000-0000-0000-0000-000000000001"), "test_user_1");
        user_token_Map.put(UUID.fromString("00000000-0000-0000-0000-000000000002"), "test_user_1");
        user_token_Map.put(UUID.fromString("00000000-0000-0000-0000-000000000003"), "test_user_1");

        ServerSocket listener = new ServerSocket(35014);

        ExecutorService pool = Executors.newFixedThreadPool(3);

        while (true) {
            Socket socket = listener.accept();
            pool.execute(new ServerConnection(socket));
        }
    }

    private static class ServerConnection implements Runnable {
        private Socket socket;
        ServerConnection(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (true) {
                        String input = in.readLine();
                        if(input != null){
                            System.out.println(socket.getInetAddress().toString() + " 클라이언트가 전송함 : " + input);

                            JSONObject receive_json = new JSONObject(input);

                            JSONObject response = new JSONObject();
                            response.put("status", 400);
                            response.put("body", "Server Do Nothing");

                            if(receive_json.getString("command").equals("REGISTER")){
                                response = register(receive_json);
                            }
                            if(receive_json.getString("command").equals("LOGIN")){
                                response = login(receive_json);
                            }
                            if(receive_json.getString("command").equals("GET_FRIENDS")){
                                response = get_friends(receive_json);
                            }
                            if(receive_json.getString("command").equals("GET_USER_ROOM")){
                                response = get_user_room(receive_json);
                            }

                            answerToClient(response);
                        }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void answerToClient(JSONObject response) throws IOException {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            out.write(response.toString());
            out.newLine();
            out.flush();

            System.out.println("result 전송함: " + response.toString());

            socket.close();
        }


        public JSONObject register(JSONObject receive_json) throws SQLException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query = "INSERT INTO user (user_id, password, name, nickname, email)\n" +
                    "VALUES ( ?, ?, ?, ?, ?);";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, receive_json.getString("id"));
            ps.setString(2, receive_json.getString("password"));
            ps.setString(3, receive_json.getString("name"));
            ps.setString(4, receive_json.getString("nickname"));
            ps.setString(5, receive_json.getString("email"));

            int updateResult = ps.executeUpdate();


            if(updateResult > 0){
                response.put("status", 200);
                response.put("body", "Register Success");
            } else {
                response.put("status", 400);
                response.put("body", "Register failed");
            }

            return response;
        }

        public JSONObject login(JSONObject receive_json) throws SQLException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query = "select user_id FROM user WHERE user_id =? and password = ?;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, receive_json.getString("id"));
            ps.setString(2, receive_json.getString("password"));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                UUID uuid = UUID.randomUUID();

                response.put("status", 200);
                response.put("body", "Login Success");
                response.put("access-token", uuid);


                user_token_Map.put(uuid, rs.getString("user_id"));

            } else {

                response.put("status", 400);
                response.put("body", "Login Failed");

            }

            return response;
        }

        public JSONObject get_friends(JSONObject receive_json) throws SQLException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            UUID access_token = UUID.fromString(receive_json.getString("access-token"));

            String userid = user_token_Map.get(access_token);

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query = "select to_user_id FROM friend WHERE from_user_id =?;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, userid);
            ResultSet rs = ps.executeQuery();

            JSONArray friendsArray = new JSONArray();

            while (rs.next()) {
                String query2 = "select * FROM user WHERE user_id =?;";
                PreparedStatement ps2 = con.prepareStatement(query2);
                ps2.setString(1, rs.getString("to_user_id"));
                ResultSet rs2 = ps2.executeQuery();

                while (rs2.next()) {
                    HashMap<String, String> userinfo = new HashMap<>();
                    userinfo.put("user_id", rs2.getString("user_id"));
                    userinfo.put("name", rs2.getString("name"));
                    userinfo.put("nickname", rs2.getString("nickname"));
                    userinfo.put("email", rs2.getString("email"));

                    friendsArray.put(new JSONObject(userinfo));
                }

            }


            response.put("body", friendsArray);
            response.put("status", 200);

            return response;
        }


        public JSONObject get_user_room(JSONObject receive_json) throws SQLException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            UUID access_token = UUID.fromString(receive_json.getString("access-token"));

            String userid = user_token_Map.get(access_token);

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query = "select room_id FROM has_room WHERE user_id =?;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, userid);
            ResultSet rs = ps.executeQuery();

            JSONObject rooms = new JSONObject();

            while (rs.next()) {
                JSONArray roomInfoArray = new JSONArray();

                String query2 = "select user_id FROM has_room WHERE room_id =?;";
                PreparedStatement ps2 = con.prepareStatement(query2);
                ps2.setString(1, rs.getString("room_id"));
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    String query3 = "select * FROM user WHERE user_id =?;";
                    PreparedStatement ps3 = con.prepareStatement(query3);
                    ps3.setString(1, rs2.getString("user_id"));
                    ResultSet rs3 = ps3.executeQuery();


                    while (rs3.next()) {
                        HashMap<String, String> userinfo = new HashMap<>();
                        userinfo.put("user_id", rs3.getString("user_id"));
                        userinfo.put("name", rs3.getString("name"));
                        userinfo.put("nickname", rs3.getString("nickname"));
                        userinfo.put("email", rs3.getString("email"));

                        roomInfoArray.put(new JSONObject(userinfo));
                    }
                }
                rooms.put(rs.getString("room_id"), roomInfoArray);
            }


            response.put("body", rooms);
            response.put("status", 200);

            return response;
        }

    }
}
