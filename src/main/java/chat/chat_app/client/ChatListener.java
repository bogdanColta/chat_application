package chat.chat_app.client;

public interface ChatListener {
    void messageReceived(String from, String message);
}
