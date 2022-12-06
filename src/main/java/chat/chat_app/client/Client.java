package chat.chat_app.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client implements ChatClient, Runnable {
    static Socket socket;
    static PrintWriter writer;
    static BufferedReader in;
    private static List<ChatListener> chatListeners;
    private String username;

    @Override
    public void addChatListener(ChatListener listener) {
        if (chatListeners == null) {
            chatListeners = new ArrayList<>();
        }
        chatListeners.add(listener);
    }

    @Override
    public void removeChatListener(ChatListener listener) {
        chatListeners.remove(listener);
    }

    @Override
    public boolean sendUsername(String username) {
        if (username == null) {
            return false;
        }
        this.username = username;
        try {
            writer.println(username);
        } catch (Exception e) {
            close();
            return false;
        }
        return true;
    }

    @Override
    public boolean sendMessage(String message) {
        if (message == null) {
            return false;
        }
        try {
            writer.println("SAY~" + message);
        } catch (Exception e) {
            close();
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean connect(String address, int port) {
        try {
            socket = new Socket(address, port);
        } catch (Exception e) {
            System.out.println("Connection refused");
            return false;
        }

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            close();
            return false;
        }

        new Thread(this).start();

        return true;
    }

    @Override
    public void run() {
        String line;

        try {
            while ((line = in.readLine()) != null) {
                for (int i = 0; i < chatListeners.size(); i++) {
                    String[] s = line.split("~");
                    chatListeners.get(i).messageReceived(s[1], s[2]);
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
