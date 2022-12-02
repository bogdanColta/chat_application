package chat.chat_app.client;

import java.net.InetAddress;

public interface ChatClient {
    boolean connect (String address, int port);
    void close();
    boolean sendUsername(String username);
    boolean sendMessage(String message);
    void addChatListener(ChatListener listener);
    void removeChatListener(ChatListener listener);
}
