import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Scanner;
import java.util.concurrent.*;

import model.Response;
import org.json.JSONObject;

public class Server {
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
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (true) {
                    String input = in.readLine();
                    System.out.println(socket.getInetAddress().toString() + " 클라이언트가 전송함 : " + input);

                    JSONObject receive_json = new JSONObject(input);

                    JSONObject response = new JSONObject();

                    if(receive_json.getString("command").equals("REGISTER")){
                        response = register(receive_json, socket);
                    }

                    System.out.println("result: " + response.toString());

                    out.write(response.toString());
                    out.newLine();
                    out.flush();

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        public JSONObject register(JSONObject receive_json, Socket socket) throws SQLException {
            JSONObject response = new JSONObject();
            response.put("status", "400");

            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");
            // PostID is Auto_increment.
            String query = "INSERT INTO user (user_id, password, nickname, email)\n" +
                    "VALUES ( ?, ?, ?, ?);";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, receive_json.getString("name"));
            ps.setString(2, receive_json.getString("password"));
            ps.setString(3, receive_json.getString("nickname"));
            ps.setString(4, receive_json.getString("email"));

            int updateResult = ps.executeUpdate();


            if(updateResult > 0){
                response.put("status", "200");
                response.put("body", "Register Success");
            } else {
                response.put("body", "Register 400");
                response.put("body", "Register failed");
            }

            return response;
        }


    }
}
