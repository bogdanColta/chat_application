package chat.chat_app.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class Server {
    public static void main(String[] args) throws Exception{

        System.out.println(InetAddress.getLocalHost());
        System.out.println("Introduce a valid port number: ");

        BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
        int port = Integer.parseInt(reader1.readLine());

        while (port < 0 && port > 65536) {
            System.out.println("The port number is not valid. Introduce a valid port number: ");
            port = Integer.parseInt(reader1.readLine());
        }

        MyChatServer server = new MyChatServer(port);
        System.out.println("Server started at port " + server.getPort());
        server.start();

        while (true) {
            if (reader1.readLine().equals("quit")) {
                server.stop();
                System.exit(-1);
                break;
            }
        }

    }
}
