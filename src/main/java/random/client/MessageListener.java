package random.client;

public interface MessageListener {
    void notifyAboutNewMessage(String fromUser, String message);
}
