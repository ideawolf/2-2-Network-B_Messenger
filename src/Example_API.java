import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


// test_user_1
// 00000000-0000-0000-0000-000000000001

// test_user_2
// 00000000-0000-0000-0000-000000000002

// test_user_3
// 00000000-0000-0000-0000-000000000003
public class Example_API {
    public static void main(String[] args) {
        try {
            JSONObject json = new JSONObject();
            json.put("command", "GET_FRIENDS");
            json.put("access-token", "00000000-0000-0000-0000-000000000001");
            Socket socket = new Socket("localhost", 35014);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            out.write(json.toString());
            out.newLine();
            out.flush();

//
//            int attempts = 0;
//            while(!in.ready() && attempts < 1000)
//            {
//                attempts++;
//                Thread.sleep(10);
//            }

            String response_str = in.readLine();

            JSONObject response = new JSONObject(response_str);

            System.out.println("reponse: " + response);

            JSONArray friendList = response.getJSONArray("body");

            System.out.println(friendList);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    public void get_friend_list(){
        try {
            JSONObject json = new JSONObject();
            json.put("command", "GET_FRIENDS");
            json.put("access-token", "00000000-0000-0000-0000-000000000001");
            Socket socket = new Socket("localhost", 35014);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            out.write(json.toString());
            out.newLine();
            out.flush();

//
//            int attempts = 0;
//            while(!in.ready() && attempts < 1000)
//            {
//                attempts++;
//                Thread.sleep(10);
//            }

            String response_str = in.readLine();

            JSONObject response = new JSONObject(response_str);

            System.out.println("reponse: " + response);

            JSONArray friendList = response.getJSONArray("body");

            System.out.println(friendList);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}
