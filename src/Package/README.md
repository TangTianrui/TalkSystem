#TalkSystem 
 实现了简易的聊天系统，能够群聊，私聊以及用户上线下线的广播
 * MainServer
    * ReadFromClient
 * MainClient
    * OutToServer
    * ReadFromServer
###MainServer
* 服务端主程序
    * 实现了服务端的创造，并且能够容纳20个线程同时进行
```
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
```
#####ReadFromClient
* 实现了服务端读取客户端的信息
    * 实现了run方法，能够读取客户端的输入，并且根据输入的格式判断要实现的方法
```
  
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
```
   
* 根据端口号进行循环，得到用户的用户名
````
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
````
* 输入用户名，并且根据当前的socket将用户名和socket一并存入HashMap
````
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
````
* 如果判定条件满足群聊的输入形式，则将用户名和输出的内容发送给所有客户端，实现群聊
````
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
````
* 根据对比usename，得到想要私聊时，传输的socket，并将指定内容发送给指定的socket，实现了私聊
````
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
````
###MainClient
* 创建一个新的连接，并且通过两个线程来实现
````
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
````
#####ReadFromServer
* 如果服务端有发送给客户端消息，就读取出来，通过循环来实现持续打印
```` 
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
````
#####OutToServer
* 给服务端发送信息,根据正则表达式的分离字符串，得到分离后的信息，并且根据用户输入下线实现连接的断开。
````
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
````