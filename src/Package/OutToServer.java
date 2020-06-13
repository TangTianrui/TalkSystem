package Package;

/**
 * @author TangTianrui
 * @create 2020-06-13-16:09
 */
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

class OutToServer implements Runnable{
    private Socket client;
    private boolean flag=true;
    public OutToServer(Socket client) {
        super();
        this.client = client;
    }

    public void run() {
        PrintStream printStream;
        try {
            printStream = new PrintStream(client.getOutputStream(),true);
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            while(flag) {
                System.out.println("用户注册格式----username:yyy");
                System.out.println("群聊格式--------G:hello");
                System.out.println("私聊------------P:yyy-hhh");
                System.out.println("输入“拜拜”，结束群聊！");
                System.out.println("请输入：");
                while(scanner.hasNext()) {
                    String string = scanner.nextLine();
                    printStream.println(string);
                    if(string.equals("拜拜")) {
                        System.out.println("客户端退出");
                        printStream.close();
                        scanner.close();
                        //client.close();
                        flag=false;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

