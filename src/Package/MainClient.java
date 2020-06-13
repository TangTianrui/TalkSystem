package Package;

import java.net.Socket;

/**
 * @author TangTianrui
 * @create 2020-06-13-16:13
 */
public class MainClient {
    public static void main(String[] args) {
        String serverName = "127.0.0.1";
        int port = 3456;
        Socket client;
        try {
            client = new Socket(serverName,port);
            Thread ouThread = new Thread(new OutToServer(client));
            Thread inThread = new Thread(new ReadFromServer(client));
            ouThread.start();
            inThread.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("可能端口被占用，需要换一个端口！");
        }
    }
}
