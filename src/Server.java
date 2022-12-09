import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.concurrent.*;
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

                while (in.hasNextLine()) {
                    String input = in.nextLine();
                    System.out.println(socket.getInetAddress().toString() + " 클라이언트가 전송함 : " + input);

                    JSONObject recieve_json = new JSONObject(input);

                    boolean status = false;

                    if(recieve_json.getString("command").equals("REGISTER")){
                        status = register(recieve_json);
                    }

                    System.out.println("result: " + status);

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }


        public boolean register(JSONObject input) throws SQLException {
            Connection con = DriverManager.getConnection("jdbc:sqlite:db.sqlite3");
            // PostID is Auto_increment.
            String query = "INSERT INTO user (user_id, password, nickname, email)\n" +
                    "VALUES ( ?, ?, ?, ?);";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, input.getString("name"));
            ps.setString(2, input.getString("password"));
            ps.setString(3, input.getString("nickname"));
            ps.setString(4, input.getString("email"));

            int updateResult = ps.executeUpdate();

            if(updateResult > 0){
                return true;
            }

            return false;
        }


    }
}
