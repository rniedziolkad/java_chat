import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Server {
    private final int port;
    private final Set<ClientHandler> connectedClients;

    public Server(int port) {
        this.port = port;
        this.connectedClients = new CopyOnWriteArraySet<>();
    }

    public int getPort() {
        return port;
    }

    public void addClient(ClientHandler client){
        connectedClients.add(client);
        System.out.println("Added client. Logged clients: "+getNumberOfConnected());
    }

    public void removeClient(ClientHandler client){
        connectedClients.remove(client);
        System.out.println("Removed client. Logged clients: "+getNumberOfConnected());
    }

    public void broadcastToConnected(String message){
        System.out.println("Broadcasting: "+message);
        for(ClientHandler client:connectedClients){
            client.send(message);
        }
    }

    public int getNumberOfConnected(){
        return connectedClients.size();
    }
}
