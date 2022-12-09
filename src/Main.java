import javax.swing.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) throws IOException {
        JSONObject json = new JSONObject();
        json.put("command", "REGISTER");
        json.put("name", "test_name");
        json.put("password", "test_password");
        json.put("nickname", "test_nickname");
        json.put("email", "test_email@email.com");
        Socket s = new Socket("localhost", 35014);
        try (OutputStreamWriter out = new OutputStreamWriter(
                s.getOutputStream(), StandardCharsets.UTF_8)) {
            out.write(json.toString());
        }

        new Login();
    }
}