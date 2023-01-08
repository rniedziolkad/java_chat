import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args){
        Server server = new Server(54321);

        try(ServerSocket serverSocket = new ServerSocket(server.getPort())){
            Socket clientSocket;
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    ClientHandler newClient = new ClientHandler(clientSocket, server);
                    newClient.start();
                    server.addClient(newClient);
                    System.out.println(server.getConnectedClients().size());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
