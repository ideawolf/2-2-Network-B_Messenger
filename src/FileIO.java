import java.io.*;
import java.net.*;

public class FileIO {
    public static final int DEFAULT_BUFFER_SIZE = 10000;

    public static void FileSend(File file) {

        // 상대 ip랑 포트번호 서버로부터 알아오는 메소드 만들어지면 수정
        String serverIP = "127.0.0.1";
        int port = 35014;

        if (!file.exists()) {
            System.out.println("File not Exist.");
            System.exit(0);
        }

        long fileSize = file.length();
        long totalReadBytes = 0;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int readBytes;
        double startTime = 0;

        try {
            FileInputStream fis = new FileInputStream(file);
            Socket socket = new Socket(serverIP, port);
            if(!socket.isConnected()){
                System.out.println("Socket Connect Error.");
                System.exit(0);
            }

            startTime = System.currentTimeMillis();
            OutputStream os = socket.getOutputStream();
            while ((readBytes = fis.read(buffer)) > 0) {
                os.write(buffer, 0, readBytes);
                totalReadBytes += readBytes;
                System.out.println("In progress: " + totalReadBytes + "/"
                        + fileSize + " Byte(s) ("
                        + (totalReadBytes * 100 / fileSize) + " %)");
            }

            System.out.println("File transfer completed.");
            fis.close();
            os.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        double endTime = System.currentTimeMillis();
        double diffTime = (endTime - startTime)/ 1000;
        double transferSpeed = (fileSize / 1000) / diffTime;

        System.out.println("time: " + diffTime+ " second(s)");
        System.out.println("Average transfer speed: " + transferSpeed + " KB/s");
    }

    public static void FileReceive(String fileName) {
        int port = 30514;  //int port =  9999;

        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("This server is listening... (Port: " + port  + ")");
            Socket socket = server.accept();  //새로운 연결 소켓 생성 및 accept대기
            InetSocketAddress isaClient = (InetSocketAddress) socket.getRemoteSocketAddress();

            System.out.println("A client("+isaClient.getAddress().getHostAddress()+
                    " is connected. (Port: " +isaClient.getPort() + ")");

            FileOutputStream fos = new FileOutputStream(fileName);
            InputStream is = socket.getInputStream();

            double startTime = System.currentTimeMillis();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int readBytes;
            while ((readBytes = is.read(buffer)) != -1) {
                fos.write(buffer, 0, readBytes);

            }
            double endTime = System.currentTimeMillis();
            double diffTime = (endTime - startTime)/ 1000;

            System.out.println("time: " + diffTime+ " second(s)");

            is.close();
            fos.close();
            socket.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}