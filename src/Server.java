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
import org.json.JSONObject;

public class Server {
    static Map<UUID, String> user_token_Map = new HashMap<>();
    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(35014);

        ExecutorService pool = Executors.newFixedThreadPool(20);

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
                    System.out.println(socket.getInetAddress().toString() + " 클라이언트가 전송함 : " + input);

                    JSONObject receive_json = new JSONObject(input);

                    JSONObject response = new JSONObject();

                    if(receive_json.getString("command").equals("REGISTER")){
                        response = register(receive_json);
                    }
                    if(receive_json.getString("command").equals("LOGIN")){
                        response = login(receive_json);
                    }


                    answerToClient(response);
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

        }


        public JSONObject register(JSONObject receive_json) throws SQLException {
            JSONObject response = new JSONObject();
            response.put("status", 400);

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");

            String query = "INSERT INTO user (user_id, password, nickname, email)\n" +
                    "VALUES ( ?, ?, ?, ?);";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, receive_json.getString("name"));
            ps.setString(2, receive_json.getString("password"));
            ps.setString(3, receive_json.getString("nickname"));
            ps.setString(4, receive_json.getString("email"));

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
            ps.setString(1, receive_json.getString("name"));
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

    }
}
