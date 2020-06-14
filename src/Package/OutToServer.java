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
    private boolean flag=true;//判断是否还实现读入信息的标志
    public OutToServer(Socket client) {
        //构造函数，将传入的socket作为该方法的对象
        super();
        this.client = client;
    }

    public void run() {
        PrintStream printStream;
        try {
            //建立一个读取的输出流
            printStream = new PrintStream(client.getOutputStream(),true);
            Scanner scanner = new Scanner(System.in);
            //建立读取键盘输入的输入流
            scanner.useDelimiter("\n");
            while(flag) {
                System.out.println("用户注册格式----username:yyy");
                System.out.println("群聊格式--------G:hello");
                System.out.println("私聊------------P:yyy-hhh");
                System.out.println("输入“下线”，结束群聊！");
                System.out.println("请输入：");
                while(scanner.hasNext()) {
                    String string = scanner.nextLine();
                    printStream.println(string);
                    // 从读取键盘输入并且传递给服务端
                    if(string.equals("下线")) {
                        System.out.println("客户端退出");
                        printStream.close();//当在键盘中输入下线时，关闭输出流
                        scanner.close();//关闭输入流
                        //client.close();
                        flag=false;//将读入的标志置为false
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

