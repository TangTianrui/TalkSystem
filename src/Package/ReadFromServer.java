package Package;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author TangTianrui
 * @create 2020-06-13-16:10
 */
//实现客户端读取服务端发送的信息
class ReadFromServer implements Runnable{
    private Socket client;
    public ReadFromServer(Socket client) {
        //构造函数
        super();
        this.client = client;
    }
    public void run() {
        try {
            Scanner scanner = new Scanner(client.getInputStream());
            //定义输入流，并且是读取客户端信息的输入流
            scanner.useDelimiter("\n");
            while(scanner.hasNext()) {
                //如果信息，则打印出来
                System.out.println(scanner.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}