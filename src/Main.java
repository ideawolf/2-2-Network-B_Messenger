import javax.swing.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import model.Response;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) throws IOException {
        JSONObject json = new JSONObject();
        json.put("command", "REGISTER");
        json.put("name", "test_name");
        json.put("password", "test_password");
        json.put("nickname", "test_nickname");
        json.put("email", "test_email@email.com");
        Socket socket = new Socket("localhost", 35014);
        try (OutputStreamWriter out = new OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8)) {
            out.write(json.toString());
        }
        var in = new Scanner(socket.getInputStream());
        while(true){
            Response response =
        }


        new Login();
    }
}