package random.client;

public interface UserEventListener {
    void userJoin(String user);
    void userExit(String user);
}
