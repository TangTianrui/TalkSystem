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
//创建主服务端
public class MainServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(3456);
        //存取用户信息（用户名和Socket）
        Map<String, Socket> map = new HashMap<String, Socket>();
        //线程池，线程大小为20
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        System.out.println("等待客户连接中...");
        try {
            for(int i = 0;i < 20;i ++) {//因为线程池大小为20，所以i不超过20
                Socket socket = serverSocket.accept();//接收客户端的连接，并在服务端打印
                System.out.println("有新的用户连接："+socket.getInetAddress()+socket.getPort());
                //执行读取客户端的操作，新建一个读取客户端的对象，并将新的插座和已经存在的用户名和插座的关系传入对象中。
                executorService.execute(new ReadFromClient(socket,map));
            }
            executorService.shutdown();//当所有线程都执行之后，关闭线程池
            serverSocket.close();
        } catch (Exception e) {
        }
    }
}