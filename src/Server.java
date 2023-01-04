import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 54321;

    public static void main(String[] args){
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            //noinspection InfiniteLoopStatement
            while (true) {
                try (Socket client = serverSocket.accept()) {
                    new ClientHandler(client).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
