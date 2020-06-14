package Package;

import java.net.Socket;

/**
 * @author TangTianrui
 * @create 2020-06-13-16:13
 */
public class MainClient {
    //主客户端
    public static void main(String[] args) {
        String serverName = "127.0.0.1";//定义ip地址
        int port = 3456;//定义端口
        Socket client;
        try {
            client = new Socket(serverName,port);//将ip地址和端口封装到socket中
            Thread outThread = new Thread(new OutToServer(client));//创建两个输出和输入进线程，两个可以同时进行
            Thread inThread = new Thread(new ReadFromServer(client));
            outThread.start();//将两个线程开启
            inThread.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("可能端口被占用，需要换一个端口！");
        }
    }
}
