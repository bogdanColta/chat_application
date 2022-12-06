package chat.chat_app.client;

public interface ChatClient {
    boolean connect(String address, int port);

    void close();

    boolean sendUsername(String username);

    boolean sendMessage(String message);

    void addChatListener(ChatListener listener);

    void removeChatListener(ChatListener listener);
}
