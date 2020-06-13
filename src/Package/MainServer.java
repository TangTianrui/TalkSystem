package Package;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author TangTianrui
 * @create 2020-06-13-16:14
 */
public class MainServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(3456);
        //存取用户信息（用户名和Socket）
        Map<String, Socket> map = new HashMap<String, Socket>();
        //线程池，线程大小为20
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        System.out.println("等待客户连接中...");
        try {
            for(int i = 0;i < 20;i ++) {
                Socket socket = serverSocket.accept();
                System.out.println("有新的用户连接："+socket.getInetAddress()+socket.getPort());
                executorService.execute(new ReadFromClient(socket,map));
            }
            executorService.shutdown();
            serverSocket.close();
        } catch (Exception e) {
        }
    }
}