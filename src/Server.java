import java.net.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.*;

public class Server {
    public static void main(String[] args) throws IOException, SQLException {
        ServerSocket listener = new ServerSocket(35014);

        ExecutorService pool = Executors.newFixedThreadPool(20);

        Connection con = DriverManager.getConnection("jdbc:sqlite:/Users/ideawolf/Documents/GitHub/B_Messenger/db.sqlite3");


        // PostID is Auto_increment.
        String query = "INSERT INTO user (user_id, password, nickname, email)\n" +
                "VALUES ( ?, ?, ?, ?);";

        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, "userId1");
        ps.setString(2, "userPassword1");
        ps.setString(3, "Nickname1");
        ps.setString(4, "user1@email.com");

        ps.executeUpdate();

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

        }


    }
}
