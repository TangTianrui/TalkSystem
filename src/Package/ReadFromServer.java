package Package;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author TangTianrui
 * @create 2020-06-13-16:10
 */
class ReadFromServer implements Runnable{
    private Socket client;
    public ReadFromServer(Socket client) {
        super();
        this.client = client;
    }
    public void run() {
        try {
            Scanner scanner = new Scanner(client.getInputStream());
            scanner.useDelimiter("\n");
            while(scanner.hasNext()) {
                System.out.println(scanner.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}