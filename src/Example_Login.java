import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Example_Login {
    public static void main(String[] args) {
        try {
            JSONObject json = new JSONObject();
            json.put("command", "LOGIN");
            json.put("id", "test_user_3");
            json.put("password", "test_password");
//            json.put("access-token", "00000000-0000-0000-0000-000000000001");
            Socket socket = new Socket("localhost", 35014);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.write(json.toString());
            out.newLine();
            out.flush();

            while(true){
                String response_str = in.readLine();

                if(response_str == null){
                    Thread.sleep(10);
                    continue;
                }
                JSONObject response = new  JSONObject(response_str);

                System.out.println("reponse: " + response);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }
}
