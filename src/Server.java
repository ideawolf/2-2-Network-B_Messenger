import java.net.*;
import java.io.*;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

//import com.auth0.jwt.JWT;
import model.Response;
import model.Room;
import org.json.JSONArray;
import org.json.JSONObject;

public class Server {
    static Map<UUID, String> user_token_Map = new HashMap<>();
    static Map<String, BufferedWriter> user_to_buffwriter = new HashMap<>();

    static ArrayList<Room> rooms = new ArrayList<>();
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
        private String logged_in_user_id;
        ServerConnection(Socket socket) {
            this.socket = socket;
        }
        BufferedWriter out;

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                while (true) {
                        String input = in.readLine();
                        if(input != null){
                            System.out.println(socket.getInetAddress().toString() + " 클라이언트가 전송함 : " + input);

                            JSONObject receive_json = new JSONObject(input);

                            JSONObject response = new JSONObject();
                            response.put("status", 400);
                            response.put("body", "Server Do Nothing");
                            if(receive_json.getString("command").equals("LOGIN")){
                                response = login(receive_json);
                                answerToClient(response);
                                user_to_buffwriter.put(logged_in_user_id, out);

                            } else {    // Socket 유지 할 필요 없음
                                if(receive_json.getString("command").equals("REGISTER")){
                                    response = register(receive_json);
                                }
                                if(receive_json.getString("command").equals("GET_FRIENDS")){
                                    response = get_friends(receive_json);
                                }
                                if(receive_json.getString("command").equals("GET_USER_ROOM")){
                                    response = get_user_room(receive_json);
                                }
                                if(receive_json.getString("command").equals("GET_ALL_ID")){
                                    response = get_all_id(receive_json);
                                }
                                if(receive_json.getString("command").equals("GET_USER_INFO")){
                                    response = get_user_info(receive_json);
                                }
                                if(receive_json.getString("command").equals("CREATE_ROOM")){
                                    response = create_room(receive_json);
                                }
                                answerToClient(response);

                                socket.close();
                            }
                        }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void broadcast(String to_user, JSONObject response_json) throws IOException {
            if(user_to_buffwriter.containsKey(to_user)){
                BufferedWriter broadout = user_to_buffwriter.get(to_user);

                broadout.write(response_json.toString());
                broadout.newLine();
                broadout.flush();
            }
        }

        public void answerToClient(JSONObject response) throws IOException {

            out.write(response.toString());
            out.newLine();
            out.flush();

            System.out.println("result 전송함: " + response.toString());
        }


        public JSONObject register(JSONObject receive_json) throws SQLException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            LocalDateTime localDateTime = LocalDateTime.now();
            String localDateTimeFormat = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query = "INSERT INTO user (user_id, password, name, nickname, email, birthday, isOnline, last_online)\n" +
                    "VALUES ( ?, ?, ?, ?, ?, ?, 0, ?);";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, receive_json.getString("id"));
            ps.setString(2, receive_json.getString("password"));
            ps.setString(3, receive_json.getString("name"));
            ps.setString(4, receive_json.getString("nickname"));
            ps.setString(5, receive_json.getString("email"));
            ps.setString(6, receive_json.getString("birthday"));
            ps.setString(7, localDateTimeFormat);

            int updateResult = ps.executeUpdate();


            if(updateResult > 0){
                response.put("status", 200);
                response.put("body", "Register Success");
            } else {
                response.put("status", 400);
                response.put("body", "Register failed");
            }


            con.close();

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

                logged_in_user_id = rs.getString("user_id");

                user_token_Map.put(uuid, rs.getString("user_id"));

            } else {

                response.put("status", 400);
                response.put("body", "Login Failed");

            }


            con.close();

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


            con.close();

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

            con.close();


            return response;
        }

        public JSONObject get_all_id(JSONObject receive_json) throws SQLException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

//            UUID access_token = UUID.fromString(receive_json.getString("access-token"));
//
//            String userid = user_token_Map.get(access_token);

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query = "select user_id FROM user";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            JSONArray user_id_arr = new JSONArray();

            while (rs.next()) {

                user_id_arr.put(rs.getString("user_id"));
            }


            response.put("body", user_id_arr);
            response.put("status", 200);

            con.close();


            return response;
        }

        public JSONObject create_room(JSONObject receive_json) throws SQLException, IOException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            UUID access_token = UUID.fromString(receive_json.getString("access-token"));

            String userid = user_token_Map.get(access_token);

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            LocalDateTime localDateTime = LocalDateTime.now();
            String localDateTimeFormat = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String query = "INSERT INTO room (last_time)" +
                    "VALUES (?);";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, localDateTimeFormat);

            ResultSet rs = ps.getGeneratedKeys();

            while (rs.next()) {
                int created_room_id = rs.getInt(1);

                String query2 = "INSERT INTO has_room (user_id, room_id) VALUES (?, ?);";
                PreparedStatement ps2 = con.prepareStatement(query2);
                ps2.setString(1, userid);
                ps2.setInt(2, created_room_id);
                int res = ps2.executeUpdate();
                if(res > 0 ){
                    System.out.println("Room Created: " + created_room_id);

                    JSONArray userListJson = receive_json.getJSONArray("userlist");
                    ArrayList<String> userList = new ArrayList<String>();

                    if (userListJson != null) {
                        for (int i=0;i<userListJson.length();i++){
                            userList.add(userListJson.getString(i));
                        }
                    }

                    for(String user : userList){
                        String query3 = "INSERT INTO has_room (user_id, room_id) VALUES (?, ?);";
                        PreparedStatement ps3 = con.prepareStatement(query3);
                        ps3.setString(1, user);
                        ps3.setInt(2, created_room_id);
                        int res2 = ps2.executeUpdate();

                        JSONObject invitedResponse = new JSONObject();
                        invitedResponse.put("command", "invited");
                        invitedResponse.put("body", "you are invited in " + created_room_id);
                        invitedResponse.put("room_id", created_room_id);
                        if(res2 > 0 ) {
                            System.out.println(user + " is invited in " + created_room_id);

                            broadcast(user, invitedResponse);
                        }
                    }
                }

            }
            response.put("body", "Ok");
            response.put("status", 200);

            con.close();

            return response;
        }

        public JSONObject get_user_info(JSONObject receive_json) throws SQLException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            UUID access_token = UUID.fromString(receive_json.getString("access-token"));

            String userid = user_token_Map.get(access_token);

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query2 = "select * FROM user WHERE user_id =?;";
            PreparedStatement ps2 = con.prepareStatement(query2);
            ps2.setString(1, userid);
            ResultSet rs2 = ps2.executeQuery();

            HashMap<String, String> userinfo = new HashMap<>();
            userinfo.put("user_id", rs2.getString("user_id"));
            userinfo.put("name", rs2.getString("name"));
            userinfo.put("nickname", rs2.getString("nickname"));
            userinfo.put("email", rs2.getString("email"));
            userinfo.put("birthday", rs2.getString("birthday"));
            userinfo.put("isOnline", String.valueOf(rs2.getInt("isOnline")));
            userinfo.put("last_online", rs2.getString("last_online"));
            userinfo.put("status_message", rs2.getString("status_message"));

            JSONObject userObject = new JSONObject(userinfo);

            response.put("body", userObject);
            response.put("status", 200);

            con.close();

            return response;
        }
    }
}
