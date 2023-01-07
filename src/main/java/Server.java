import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private final int port;
    private final ArrayList<ClientHandler> connectedClients;

    public Server(int port) {
        this.port = port;
        this.connectedClients = new ArrayList<>();
    }

    public int getPort() {
        return port;
    }

    public ArrayList<ClientHandler> getConnectedClients() {
        return connectedClients;
    }

    public void addClient(ClientHandler client){
        connectedClients.add(client);
    }

    public void broadcastToConnected(String message){
        System.out.println("Broadcasting: "+message);
        for(ClientHandler client:connectedClients){
            client.send(message);
        }
    }

    public static void main(String[] args){
        Server server = new Server(54321);
        DBConnection.connect();

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
