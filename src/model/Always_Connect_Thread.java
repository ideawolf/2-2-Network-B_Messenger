package model;

import org.json.JSONObject;

import java.io.BufferedReader;

public class Always_Connect_Thread extends Thread {
    BufferedReader in;

    public Always_Connect_Thread(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try{
            while(true){
                String response_str = in.readLine();

                if(response_str == null){
                    Thread.sleep(10);
                    continue;
                }
                JSONObject response = new  JSONObject(response_str);

                System.out.println("New Message: " + response);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
