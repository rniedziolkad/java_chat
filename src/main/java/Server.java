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
}
