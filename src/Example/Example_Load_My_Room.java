package Example;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;


// test_user_1
// 00000000-0000-0000-0000-000000000001

// test_user_2
// 00000000-0000-0000-0000-000000000002

// test_user_3
// 00000000-0000-0000-0000-000000000003
public class Example_Load_My_Room {
    public static void main(String[] args) {
        try {
            JSONObject json = new JSONObject();
            json.put("command", "LOAD_MYROOM");
            json.put("access-token", "00000000-0000-0000-0000-000000000003");


//            json.put("access-token", "00000000-0000-0000-0000-000000000001");
            Socket socket = new Socket("localhost", 35014);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.write(json.toString());
            out.newLine();
            out.flush();

            while (true) {
                String response_str = in.readLine();

                if (response_str == null) {
                    Thread.sleep(10);
                    continue;
                }
                JSONObject response = new JSONObject(response_str);

                System.out.println("response: " + response);

                JSONArray room_list = response.getJSONArray("body");

                System.out.println(room_list);
                System.out.println(room_list.getJSONObject(0).getString("last_time"));

                LocalDateTime last_time = LocalDateTime.parse(room_list.getJSONObject(0).getString("last_time"));

                System.out.println(last_time);

                System.out.println(last_time.getHour() + " : " + last_time.getMinute() + " : " + last_time.getSecond());
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
