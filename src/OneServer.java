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
    static Map<String, BufferedWriter> userid_to_clientwriter = new HashMap<>();

    public static void setUserid_to_clientwriter(Map<String, BufferedWriter> userid_to_clientwriter) {
        OneServer.userid_to_clientwriter = userid_to_clientwriter;
    }

    public void run() throws InterruptedException, IOException {
        while(true){
            var out = userid_to_clientwriter.get("test_user_1");
            Thread.sleep(100);

            JSONObject testJson = new JSONObject();

            testJson.put("body", "Test");

            out.write(testJson.toString());
            out.newLine();
            out.flush();
        }
    }

}
