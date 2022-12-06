package chat.chat_app.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyChatServer implements ChatServer, Runnable {
    private static int port;
    private static MyChatServer server;
    private Thread s1;
    private List<ChatClientHandler> clients;
    private ServerSocket serverSocket;

    public MyChatServer(int port) {
        server = this;
        MyChatServer.port = port;
    }

    @Override
    public void start() {
        s1 = new Thread(this);
        s1.start();
    }

    @Override
    public void stop() {
        try {
            server.serverSocket.close();
        } catch (IOException e) {
            System.out.println("error");
        }

        try {
            s1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPort() {
        return port;
    }

    public void handleChatMessage(ChatClientHandler from, String message) {
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).sendChat(from.getUsername(), message);
        }
    }

    public void addClient(ChatClientHandler client) {
        clients.add(client);
    }

    public void removeClient(ChatClientHandler client) {
        clients.remove(client);
    }

    @Override
    public void run() {
        this.clients = new ArrayList<>();

        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Could not start server at port " + port);
        }

        boolean run = true;

        while (run) {
            try {
                Socket s = serverSocket.accept();
                ChatClientHandler clientHandler = new ChatClientHandler(s, server);
                addClient(clientHandler);
                Thread thread = new Thread(clientHandler);
                thread.start();
            } catch (IOException e) {
                run = false;
            }
        }
    }
}


class ChatClientHandler implements Runnable {
    private final BufferedReader in;
    private final PrintWriter out;
    private final Socket socket;
    private final MyChatServer server;
    private String username;

    public ChatClientHandler(Socket socket, MyChatServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendChat(String from, String message) {
        String[] s = message.split("~");
        try {
            out.println("FROM~" + from + "~" + s[1]);
        } catch (Exception e) {
            close();
        }
    }

    public String getUsername() {
        return username;
    }

    public void close() {
        try {
            server.removeClient(this);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String line;
        try {
            username = in.readLine();
            while ((line = in.readLine()) != null) {
                if (line.matches("SAY~.*")) {
                    server.handleChatMessage(this, line);
                }
            }
            if (line == null) {
                close();
            }
        } catch (Exception e) {
            close();
        }

    }

}