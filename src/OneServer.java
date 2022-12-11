import java.io.*;

//import com.auth0.jwt.JWT;
import org.json.JSONObject;

public class OneServer {
    String ownUser;
    BufferedWriter out;
    BufferedReader in;

    public OneServer(String ownUser, BufferedWriter out, BufferedReader in) {
        this.ownUser = ownUser;
        this.out = out;
        this.in = in;
    }

    public void run() throws InterruptedException, IOException {
        while(true){
            JSONObject testJson = new JSONObject();

            testJson.put("body", "Hello, You are " + ownUser);

            out.write(testJson.toString());
            out.newLine();
            out.flush();

            while(true){

            }
        }
    }

}
