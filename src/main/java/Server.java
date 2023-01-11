import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private final int port;
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<ClientHandler>> connectedClients;

    public Server(int port) {
        this.port = port;
        this.connectedClients = new ConcurrentHashMap<>();
    }

    public int getPort() {
        return port;
    }

    public void addClient(ClientHandler client){
        if(!connectedClients.containsKey(client.getUser())) {
            connectedClients.put(client.getUser(), new CopyOnWriteArrayList<>());
            broadcastToConnected("EVENT USER_JOIN "+client.getUser());
        }
        connectedClients.get(client.getUser()).add(client);
        System.out.println("Added client. Logged clients: "+getNumberOfConnected());
    }

    public void removeClient(ClientHandler client){
        CopyOnWriteArrayList<ClientHandler> userConnections = connectedClients.get(client.getUser());
        userConnections.remove(client);
        if(userConnections.isEmpty()) {
            connectedClients.remove(client.getUser());
            broadcastToConnected("EVENT USER_EXIT "+client.getUser());
            System.out.println("Removed client. Logged clients: "+getNumberOfConnected());
        }
    }

    public void broadcastToConnected(String message){
        System.out.println("Broadcasting: "+message);
        for (CopyOnWriteArrayList<ClientHandler> userConnections : connectedClients.values())
            for (ClientHandler connection : userConnections)
                connection.send(message);
    }

    public ConcurrentHashMap<String, CopyOnWriteArrayList<ClientHandler>> getConnectedClients() {
        return connectedClients;
    }

    public int getNumberOfConnected(){
        return connectedClients.size();
    }
}
