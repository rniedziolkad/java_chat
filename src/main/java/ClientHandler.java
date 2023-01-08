import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread{
    private final Socket clientSocket;
    private final Server server;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println("Obsługuję połączenie z:");
        System.out.println(clientSocket.getInetAddress()+":"+clientSocket.getPort());
        try {
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            closeSocket();
        }
        String line;
        while (!clientSocket.isClosed()){
            try {
                line = reader.readLine();
                if(line == null)
                    closeSocket();
                else
                    server.broadcastToConnected(line);

            } catch (IOException e) {
                System.out.println("Error: "+e.getMessage());
                closeSocket();
            }
        }
        System.out.println("Ending thread: "+this.getName()+"...");
        server.removeClient(this);
        System.out.println("Thread ended: "+this.getName());
    }

    private void closeSocket(){
        try {
            System.out.println("Closing socket "+clientSocket.getInetAddress()+":"+clientSocket.getPort()+"...");
            clientSocket.close();
            System.out.println("Socket closed: "+clientSocket.getInetAddress()+":"+clientSocket.getPort());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void send(String message){
        System.out.println("sending: "+message+"...");
        writer.println(message);
        System.out.println("send: "+message);
    }
}
