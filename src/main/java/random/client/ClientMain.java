package random.client;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class ClientMain {
    private static final int SERVER_PORT = 54321;
    private static final String HOST = "localhost";

    public static void main(String[] args) {
        try (Client client = new Client(HOST, SERVER_PORT)){
            client.login("admin", "admin");
            sleep(1000*10);
            client.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ignored) {
        }
        System.out.println("EXITING APP...");
    }
}
