import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread{
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        System.out.println("Obsługuję połączenie z:");
        System.out.println(clientSocket.getInetAddress()+":"+clientSocket.getPort());
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
