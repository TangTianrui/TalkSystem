package Package;

/**
 * @author TangTianrui
 * @create 2020-06-13-16:14
 */
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//服务器端
/**
 * Map<String,Socket>
 * 用户注册：username:yyy
 * 群聊：G:hello
 * 私聊：P:yyy-hhh
 */

class  ReadFromClient implements Runnable{
    private Socket client;
    private Map<String,Socket> clientMap;
    public ReadFromClient(Socket client, Map<String, Socket> clientMap) {
        //构造函数
        super();
        this.client = client;
        this.clientMap = clientMap;
    }

    public void run() {
        try {
            //拿到客户端输入流，读取用户信息
            Scanner scanner = new Scanner(client.getInputStream());
            String string = null;
            while(true){
                if(scanner.hasNext()) {
                    //如果有下一行，则读取一行
                    string = scanner.nextLine();
                    Pattern pattern = Pattern.compile("\r\n|\r|\n");//判断回车标记
                    Matcher matcher = pattern.matcher(string);
                    string = matcher.replaceAll("");
                    //用户注册
                    if(string.startsWith("user")) {
                        //获取用户名
                        String useName = string.split("\\:")[1];//当格式为user：时，后面的输入就是注册的用户名
                        userRegist(useName, client);//注册名字的方法
                        continue;
                    }
                    //群聊
                    else if(string.startsWith("G")) {
                        String message = string.split("\\:")[1];//当格式为G:时，后面的输入就是群聊发出去的内容
                        gropChat(message);//发送群聊信息的方法
                        continue;
                    }
                    //私聊
                    else if(string.startsWith("P")) {
                        String temp = string.split("\\:")[1];
                        //取得用户名
                        String useName = temp.split("\\-")[0];
                        //取得消息内容
                        String message = temp.split("\\-")[1];
                        //私聊需要满足格式为P：用户名-聊天内容。
                        privateChat(useName, message);//实现私聊的方法
                        continue;
                    }
                    //用户退出
                    else if(string.contains("下线")) {
                        String useName = getUseName(client);//先根据Socket知道用户名
                        System.out.println("用户名为"+useName +"的用户下线了！！！");
                        try {
                            PrintStream printStream = new PrintStream(client.getOutputStream());
                            printStream.println("用户名为"+useName +"的用户下线了！！！");
                            //发送给所有客户端，实现下线的广播
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        clientMap.remove(useName);//清除用户信息
                        continue;
                    }
                }
            }
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //获取key值（即由端口号找到用户名）
    public String getUseName(Socket socket) {
        String useName = null;//初始化名字
        for(String getKey : clientMap.keySet()) {
            if(clientMap.get(getKey).equals(socket)) {
                //利用高级for循环，通过比对每一个key值和name的关系，得到用户名
                useName = getKey;
            }
        }
        return useName;
    }
    //注册实现
    public void userRegist(String useName,Socket client) {
        System.out.println("用户姓名为：" + useName);
        System.out.println("用户socket为：" + client);
        System.out.println("用户名为"+ useName +"的用户上线了！");
        try {
            PrintStream printStream = new PrintStream(client.getOutputStream());
            printStream.println("用户名为" + useName + "的用户上线了！");
            //将这句话传输给客户端，实现上线的广播
        }catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("当前用户数为："+ (clientMap.size()+1) +"人");
        clientMap.put(useName, client);//将用户名和socket的关系存入hashmap中，可以以后查询
    }
    //群聊实现
    public void gropChat(String message) {
        //定义迭代器
        Iterator<Entry<String, Socket>> iterable = clientMap.entrySet().iterator();
        for(Map.Entry<String, Socket> stringSocketEntry:clientMap.entrySet()) {
            try {
                Socket socket = stringSocketEntry.getValue();
                //通过高级for循环得到socket
                PrintStream printStream = new PrintStream(socket.getOutputStream(),true);
                //定义输出流
                System.out.println(stringSocketEntry.getKey()+"说: "+message);
                printStream.println(stringSocketEntry.getKey()+"说: "+message);
                //将某人的信息发送给客户端，实现群聊的功能
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
    //私聊实现
    public void privateChat(String useName,String message) {
        //根据对应的useName找到对应的Socket
        Socket privateSocket = clientMap.get(useName);
        try {
            PrintStream printStream = new PrintStream(privateSocket.getOutputStream());
            printStream.println("用户名为"+getUseName(client)+"的用户对你说："+message);
            //创建输出流，并且根据用户名查到的socket对其的客户端私发消息
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}