import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        try {
            new Login();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}