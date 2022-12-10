import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import model.Response;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        try {
            JSONObject json = new JSONObject();
            json.put("command", "REGISTER");
            json.put("name", "test_name");
            json.put("password", "test_password");
            json.put("nickname", "test_nickname");
            json.put("email", "test_email@email.com");
            Socket socket = new Socket("localhost", 35014);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            out.write(json.toString());
            out.newLine();
            out.flush();

            int attempts = 0;
            while(!in.ready() && attempts < 1000)
            {
                attempts++;
                Thread.sleep(10);
            }

            String response_str = in.readLine();

            JSONObject response = new JSONObject(response_str);

            System.out.println(response.toString());


            new Login();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}