import java.net.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

//import com.auth0.jwt.JWT;
import model.Room;
import org.json.JSONArray;
import org.json.JSONObject;

public class Server {
    static Map<UUID, String> user_token_Map = new HashMap<>();
    static Map<String, BufferedWriter> user_to_buffwriter = new HashMap<>();

    static ArrayList<Room> rooms = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        user_token_Map.put(UUID.fromString("00000000-0000-0000-0000-000000000001"), "test_user_1");
        user_token_Map.put(UUID.fromString("00000000-0000-0000-0000-000000000002"), "test_user_2");
        user_token_Map.put(UUID.fromString("00000000-0000-0000-0000-000000000003"), "test_user_3");

        ServerSocket listener = new ServerSocket(35014);

        ExecutorService pool = Executors.newFixedThreadPool(20);

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
                    if (input != null) {
                        System.out.println(socket.getInetAddress().toString() + " 클라이언트가 전송함 : " + input);

                        JSONObject receive_json = new JSONObject(input);

                        JSONObject response = new JSONObject();
                        response.put("status", 400);
                        response.put("body", "Server Do Nothing");
                        if (receive_json.getString("command").equals("LOGIN")) {
                            response = login(receive_json);
                            answerToClient(response);
                            user_to_buffwriter.put(logged_in_user_id, out);
                        } else {    // Socket 유지 할 필요 없음
                            if (receive_json.getString("command").equals("REGISTER")) {
                                response = register(receive_json);
                            }
                            if (receive_json.getString("command").equals("UPDATE_ONLINE")) {
                                response = update_online(receive_json);
                            }
                            if (receive_json.getString("command").equals("GET_FRIENDS")) {
                                response = get_friends(receive_json);
                            }
                            if (receive_json.getString("command").equals("GET_USER_ROOM")) {
                                response = get_user_room(receive_json);
                            }
                            if (receive_json.getString("command").equals("GET_ALL_ID")) {
                                response = get_all_id(receive_json);
                            }
                            if (receive_json.getString("command").equals("GET_USER_INFO")) {
                                response = get_user_info(receive_json);
                            }
                            if (receive_json.getString("command").equals("CREATE_ROOM")) {
                                response = create_room(receive_json);
                            }
                            if (receive_json.getString("command").equals("EDIT_INFO")) {
                                response = edit_info(receive_json);
                            }
                            if(receive_json.getString("command").equals("ADD_FRIEND")){
                                response = add_friend(receive_json);
                            }
                            if(receive_json.getString("command").equals("SEND_MESSAGE")){
                                response = send_message(receive_json);
                            }
                            if(receive_json.getString("command").equals("SEARCH")){
                                response = get_search_result(receive_json);
                            }
                            if(receive_json.getString("command").equals("LOAD_MYROOM")){
                                response = load_myroom(receive_json);
                            }
                            if(receive_json.getString("command").equals("INVITE_ROOM")){
                                response = invite_room(receive_json);
                            }
                            if(receive_json.getString("command").equals("ACCEPT_INVITE")){
                                response = accept_invite(receive_json);
                            }
                            if(receive_json.getString("command").equals("REJECT_INVITE")){
                                response = reject_invite(receive_json);
                            }
                            if(receive_json.getString("command").equals("LEAVE_ROOM")){
                                response = leave_room(receive_json);
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
            if (user_to_buffwriter.containsKey(to_user)) {
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
            String localDateTimeFormat = localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);

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


            if (updateResult > 0) {
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

        public JSONObject add_friend(JSONObject receive_json) throws SQLException {
            JSONObject response = new JSONObject();
            response.put("status", 400);
            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query = "INSERT INTO friend (from_user_id, to_user_id)\n" +
                    "VALUES ( ?, ?);";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, receive_json.getString("id"));
            ps.setString(2, receive_json.getString("friend_id"));

            int updateResult = ps.executeUpdate();

            if(updateResult > 0){
                response.put("status", 200);
                response.put("body", "Add Success");
            } else {
                response.put("status", 400);
                response.put("body", "Add failed");
            }


            con.close();

            return response;
        }

        public JSONObject update_online(JSONObject receive_json) throws SQLException, IOException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            LocalDateTime localDateTime = LocalDateTime.now();
            String localDateTimeFormat = localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);

            UUID access_token = UUID.fromString(receive_json.getString("access-token"));

            String userid = user_token_Map.get(access_token);

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query = "update user set isOnline = ? where user_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, receive_json.getString("isOnline"));
            ps.setString(2, userid);
            ps.executeUpdate();

            String query2 = "update user set last_online = ? where user_id = ?";
            PreparedStatement ps2 = con.prepareStatement(query2);
            ps2.setString(1, localDateTimeFormat);
            ps2.setString(2, userid);
            ps2.executeUpdate();

            String query3 = "select from_user_id FROM friend WHERE to_user_id=?;";
            PreparedStatement ps3 = con.prepareStatement(query3);
            ps3.setString(1, userid);
            ResultSet rs = ps3.executeQuery();

            JSONObject res_broadcast = new JSONObject();
            res_broadcast.put("command", "info_edited");
            while (rs.next()) {
                String from_user_id = rs.getString("from_user_id");
                broadcast(from_user_id, res_broadcast);
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
                    userinfo.put("last_online", rs2.getString("last_online"));
                    userinfo.put("status_message", rs2.getString("status_message"));
                    userinfo.put("isOnline", rs2.getString("isOnline"));

                    friendsArray.put(new JSONObject(userinfo));
                }

            }


            response.put("body", friendsArray);
            response.put("status", 200);


            con.close();

            return response;
        }

        public JSONObject get_search_result(JSONObject receive_json) throws SQLException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query = "select user_id FROM user WHERE name like ? ;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, "%" + receive_json.getString("search_keyword") + "%");
            ResultSet rs = ps.executeQuery();

            JSONArray searchedArray = new JSONArray();

            while (rs.next()) {
                String query2 = "select * FROM user WHERE user_id =?;";
                PreparedStatement ps2 = con.prepareStatement(query2);
                ps2.setString(1, rs.getString("user_id"));
                ResultSet rs2 = ps2.executeQuery();

                while (rs2.next()) {
                    HashMap<String, String> userinfo = new HashMap<>();
                    userinfo.put("user_id", rs2.getString("user_id"));
                    userinfo.put("name", rs2.getString("name"));
                    userinfo.put("nickname", rs2.getString("nickname"));
                    userinfo.put("email", rs2.getString("email"));
                    userinfo.put("isOnline", String.valueOf(rs2.getInt("isOnline")));
                    userinfo.put("last_online", rs2.getString("last_online"));
                    userinfo.put("status_message", rs2.getString("status_message"));

                    searchedArray.put(new JSONObject(userinfo));
                }

            }


            response.put("body", searchedArray);
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

        public JSONObject invite_room(JSONObject receive_json) throws SQLException, IOException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            UUID access_token = UUID.fromString(receive_json.getString("access-token"));

            String userid = user_token_Map.get(access_token);

            int room_id = receive_json.getInt("room_id");

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            LocalDateTime localDateTime = LocalDateTime.now();
            String localDateTimeFormat = localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);


            String query2 = "INSERT INTO has_room (user_id, room_id) VALUES (?, ?);";
            PreparedStatement ps2 = con.prepareStatement(query2);
            ps2.setString(1, userid);
            ps2.setInt(2, room_id);
            int res = ps2.executeUpdate();
            if (res > 0) {
                System.out.println("Room Created: " + room_id);

                JSONArray userListJson = receive_json.getJSONArray("userlist");
                ArrayList<String> userList = new ArrayList<String>();

                if (userListJson != null) {
                    for (int i = 0; i < userListJson.length(); i++) {
                        userList.add(userListJson.getString(i));
                    }
                }

                for (String user : userList) {

                    String query = "select * FROM has_room WHERE room_id=? AND user_id = ?;";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setInt(1, room_id);
                    ps.setString(2, user);
                    ResultSet rs = ps.executeQuery();

                    JSONObject failedResponse = new JSONObject();

//                    while(rs.next()){
//                        failedResponse.put("body", user + " is already invited.");
//                        failedResponse.put("status", 400);
//
//                        answerToClient(failedResponse);
//                    }

                    String query3 = "INSERT INTO has_room (user_id, room_id) VALUES (?, ?);";
                    PreparedStatement ps3 = con.prepareStatement(query3);
                    ps3.setString(1, user);
                    ps3.setInt(2, room_id);
                    int res3 = ps3.executeUpdate();

                    JSONObject invitedResponse = new JSONObject();
                    invitedResponse.put("command", "invited");
                    invitedResponse.put("body", "you are invited in " + room_id);
                    invitedResponse.put("room_id", room_id);
                    if (res3 > 0) {
                        System.out.println(user + " is invited in " + room_id);

                        broadcast(user, invitedResponse);
                    }
                }
            }

            response.put("body", "Ok");
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
            String localDateTimeFormat = localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);

            String query = "INSERT INTO room (last_time)" +
                    "VALUES (?);";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, localDateTimeFormat);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            while (rs.next()) {
                int created_room_id = rs.getInt(1);

                String query2 = "INSERT INTO has_room (user_id, room_id, IsAccept) VALUES (?, ?, 1);";
                PreparedStatement ps2 = con.prepareStatement(query2);
                ps2.setString(1, userid);
                ps2.setInt(2, created_room_id);
                int res = ps2.executeUpdate();
                if (res > 0) {
                    System.out.println("Room Created: " + created_room_id);

                    JSONArray userListJson = receive_json.getJSONArray("userlist");
                    ArrayList<String> userList = new ArrayList<String>();

                    if (userListJson != null) {
                        for (int i = 0; i < userListJson.length(); i++) {
                            userList.add(userListJson.getString(i));
                        }
                    }

                    for (String user : userList) {
                        String query3 = "INSERT INTO has_room (user_id, room_id) VALUES (?, ?);";
                        PreparedStatement ps3 = con.prepareStatement(query3);
                        ps3.setString(1, user);
                        ps3.setInt(2, created_room_id);
                        int res3 = ps3.executeUpdate();

                        JSONObject invitedResponse = new JSONObject();
                        invitedResponse.put("command", "invited");
                        invitedResponse.put("body", "you are invited in " + created_room_id);
                        invitedResponse.put("room_id", created_room_id);
                        if (res3 > 0) {
                            System.out.println(user + " is invited in " + created_room_id);

                            broadcast(user, invitedResponse);
                        }
                    }

                    response.put("room_id", created_room_id);
                }

            }
            response.put("body", "Ok");
            response.put("status", 200);

            con.close();

            return response;
        }

        public JSONObject accept_invite(JSONObject receive_json) throws SQLException, IOException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            UUID access_token = UUID.fromString(receive_json.getString("access-token"));

            String userid = user_token_Map.get(access_token);

            int room_id = receive_json.getInt("room_id");

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query = "UPDATE has_room SET IsAccept = 1 WHERE room_id = ? AND user_id = ?;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, room_id);
            ps.setString(2, userid);

            int res = ps.executeUpdate();

            if (res > 0) {
                System.out.println(userid + " accepted invite from : (room_id: " + room_id + ")");
            }

            response.put("body", "Ok");
            response.put("status", 200);

            con.close();

            return response;
        }

        public JSONObject reject_invite(JSONObject receive_json) throws SQLException, IOException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            UUID access_token = UUID.fromString(receive_json.getString("access-token"));

            String userid = user_token_Map.get(access_token);

            int room_id = receive_json.getInt("room_id");

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query = "UPDATE has_room SET IsAccept = 0 WHERE room_id = ? AND user_id = ?;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, room_id);
            ps.setString(2, userid);

            int res = ps.executeUpdate();

            if (res > 0) {
                System.out.println(userid + " rejected invite from : (room_id: " + room_id + ")");
            }

            String query2 = "select user_id FROM has_room WHERE room_id = ? AND IsAccept = 1;";
            PreparedStatement ps2 = con.prepareStatement(query2);
            ps2.setInt(1, room_id);
            ResultSet rs2 = ps2.executeQuery();

            System.out.println(rs2.getString("user_id"));

            String query3 = "select count(user_id) FROM has_room WHERE room_id = ? AND IsAccept = 1;";
            PreparedStatement ps3 = con.prepareStatement(query3);
            ps3.setInt(1, room_id);
            ResultSet rs3 = ps3.executeQuery();
            int remainNum = rs3.getInt(1);

            System.out.println(remainNum);

            JSONObject res_broadcast = new JSONObject();
            res_broadcast.put("command", "invite_rejected");
            res_broadcast.put("remain", remainNum);
            res_broadcast.put("room_id", room_id);
            while (rs2.next()) {
                System.out.println(rs2.getString("user_id"));
                String in_user_id = rs2.getString("user_id");
                broadcast(in_user_id, res_broadcast);
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


        public JSONObject edit_info(JSONObject receive_json) throws SQLException, IOException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            UUID access_token = UUID.fromString(receive_json.getString("access-token"));

            String userid = user_token_Map.get(access_token);

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query2 = "update user set nickname = ?, status_message = ? where user_id = ?;";
            PreparedStatement ps2 = con.prepareStatement(query2);
            ps2.setString(1, receive_json.getString("nickname"));
            ps2.setString(2, receive_json.getString("statusMessage"));
            ps2.setString(3, userid);
            ps2.executeUpdate();

            String query = "select from_user_id FROM friend WHERE to_user_id=?;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, userid);
            ResultSet rs = ps.executeQuery();

            JSONObject res_broadcast = new JSONObject();
            res_broadcast.put("command", "info_edited");
            while (rs.next()) {
                String from_user_id = rs.getString("from_user_id");
                broadcast(from_user_id, res_broadcast);
            }

            response.put("status", 200);

            con.close();

            return response;
        }


        public JSONObject send_message(JSONObject receive_json) throws SQLException, IOException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            UUID access_token = UUID.fromString(receive_json.getString("access-token"));

            String sender_id = user_token_Map.get(access_token);

            int room_id = receive_json.getInt("room_id");
            String msg = receive_json.getString("msg");

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query = "select user_id FROM has_room WHERE room_id=? AND IsAccept = 1;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, room_id);
            ResultSet rs = ps.executeQuery();

            LocalDateTime localDateTime = LocalDateTime.now();
            String localDateTimeFormat = localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);

            JSONArray user_id_arr = new JSONArray();
            JSONObject res_broadcast = new JSONObject();
            res_broadcast.put("command", "recieve_message");
            res_broadcast.put("body", msg);
            res_broadcast.put("sender", sender_id);
            res_broadcast.put("room_id", room_id);
            res_broadcast.put("time", localDateTimeFormat);
            while (rs.next()) {
                String to_user_id = rs.getString("user_id");
                broadcast(to_user_id, res_broadcast);
                user_id_arr.put(rs.getString("user_id"));
            }

            response.put("body", user_id_arr);
            response.put("status", 200);

            con.close();


            return response;
        }

        public JSONObject load_myroom(JSONObject receive_json) throws SQLException, IOException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            UUID access_token = UUID.fromString(receive_json.getString("access-token"));

            String user_id = user_token_Map.get(access_token);

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query = "select room_id FROM has_room WHERE user_id=?;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, user_id);
            ResultSet rs = ps.executeQuery();

            LocalDateTime localDateTime = LocalDateTime.now();
            String localDateTimeFormat = localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);

//            LocalDateTime last_time = LocalDateTime.parse(rs.getString())

            JSONArray room_list = new JSONArray();

            while (rs.next()) {
                JSONObject room = new JSONObject();
                int room_id = rs.getInt("room_id");
                room.put("id", room_id);

                String query2 = "select * FROM room WHERE id =?;";
                PreparedStatement ps2 = con.prepareStatement(query2);

                ps2.setInt(1, room_id);

                ResultSet rs2 = ps2.executeQuery();

                while (rs2.next()) {
                    room.put("last_time", rs2.getString("last_time"));
                }

                room_list.put(room);
            }

            response.put("body", room_list);
            response.put("status", 200);

            con.close();


            return response;
        }

        public JSONObject leave_room(JSONObject receive_json) throws SQLException, IOException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            UUID access_token = UUID.fromString(receive_json.getString("access-token"));

            String userid = user_token_Map.get(access_token);

            int room_id = receive_json.getInt("room_id");

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query = "DELETE FROM has_room WHERE user_id = ? AND room_id = ?;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, userid);
            ps.setInt(2, room_id);

            int res = ps.executeUpdate();

            if (res > 0) {
                System.out.println(userid + " leave room from : (room_id: " + room_id + ")");

                String query2 = "SELECT DISTINCT user_id FROM has_room WHERE room_id=? AND IsAccept = 1;";
                PreparedStatement ps2 = con.prepareStatement(query2);
                ps2.setInt(1, room_id);

                ResultSet rs2 = ps2.executeQuery();

                LocalDateTime localDateTime = LocalDateTime.now();
                String localDateTimeFormat = localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);

                JSONObject leave_broadcast = new JSONObject();

                leave_broadcast.put("command", "someone_leave");
                leave_broadcast.put("body", userid + " leave room " + room_id);
                leave_broadcast.put("leave_user_id", userid);
                leave_broadcast.put("room_id", room_id);
                leave_broadcast.put("time", localDateTimeFormat);

                while(rs2.next()){
                    String to_user_id = rs2.getString("user_id");
                    broadcast(to_user_id, leave_broadcast);
                }


            }

            response.put("body", "Ok");
            response.put("status", 200);

            con.close();

            return response;
        }


    }
}
