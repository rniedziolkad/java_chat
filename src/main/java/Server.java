import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private final int port;
    private final List<ClientHandler> connectedClients;

    public Server(int port) {
        this.port = port;
        this.connectedClients = new CopyOnWriteArrayList<>();
    }

    public int getPort() {
        return port;
    }

    public void addClient(ClientHandler client){
        connectedClients.add(client);
    }

    public void removeClient(ClientHandler client){
        connectedClients.remove(client);
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
