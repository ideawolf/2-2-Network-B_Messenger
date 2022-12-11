import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

//import com.auth0.jwt.JWT;
import model.Response;
import model.Room;
import org.json.JSONArray;
import org.json.JSONObject;

public class OneServer {
    String ownUser;
    BufferedWriter out;

    public OneServer(String ownUser, BufferedWriter out) {
        this.ownUser = ownUser;
        this.out = out;
    }

    public void run() throws InterruptedException, IOException {
        while(true){
            Thread.sleep(100);

            JSONObject testJson = new JSONObject();

            testJson.put("body", "Test, You are " + ownUser);

            out.write(testJson.toString());
            out.newLine();
            out.flush();
        }
    }

}
