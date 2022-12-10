import java.net.*;
import java.io.*;
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
                var in = new Scanner(socket.getInputStream());
                var out = new PrintWriter(socket.getOutputStream(), true);

                while (in.hasNextLine()) {
                    String input = in.nextLine();
                    System.out.println(socket.getInetAddress().toString() + " 클라이언트가 전송함 : " + input);

                    JSONObject receive_json = new JSONObject(input);

                    Response response = new Response(400, new JSONObject());

                    if(receive_json.getString("command").equals("REGISTER")){
                        response = register(receive_json, socket);
                    }

                    System.out.println("result: " + response.toString());

                    out.println(response);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        public Response register(JSONObject receive_json, Socket socket) throws SQLException {
            Response response;
            JSONObject response_json = new JSONObject();
            int response_status = 400;

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
                response_json.put("result", "success");
                response_status = 200;
            } else {
                response_json.put("result", "error");
            }

            response = new Response(response_status, response_json);

            return response;
        }


        public Response check_id(JSONObject receive_json, Socket socket) throws SQLException {
            Response response;
            JSONObject response_json = new JSONObject();
            int response_status = 400;

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
                response_json.put("result", "success");
                response_status = 200;
            } else {
                response_json.put("result", "error");
            }

            response = new Response(response_status, response_json);

            return response;
        }


    }
}
