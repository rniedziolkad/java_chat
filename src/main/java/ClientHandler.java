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
    private String user;

    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.user = null;
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
        handleSocket();
        System.out.println("Ending thread: "+this.getName()+"...");
        server.removeClient(this);
        System.out.println("Thread ended: "+this.getName());
    }

    private void handleSocket() {
        String line;
        while (!clientSocket.isClosed()){
            try {
                line = reader.readLine();
                if(line == null)
                    closeSocket();
                else{
                    interpretCommand(line);
                }
            } catch (IOException e) {
                System.out.println("Error: "+e.getMessage());
                closeSocket();
            }
        }
    }

    private void interpretCommand(String line) {
        String [] parts = line.split("\\s+", 2);
        if (parts.length<1)
            writer.println("ERROR Incorrect format: "+line);
        else{
            handleCommand(parts);
        }
    }

    private void handleCommand(String[] parts) {
        String cmd = parts[0];
        switch (cmd) {
            case "LOGIN" -> {
                if (parts.length == 2)
                    login(parts[1]);
                else
                    writer.println("ERROR No arguments provided");
            }
            case "LOGOUT" -> logout();
            case "EXIT" -> {
                writer.println("EXITING...");
                closeSocket();
            }
            case "BROADCAST" -> {
                if (parts.length == 2)
                    server.broadcastToConnected(parts[1]);
                else
                    writer.println("ERROR No message given");
            }
            default -> writer.println("ERROR Unknown command: " + cmd);
        }
    }

    private void login(String args) {
        String [] tokens = args.split("\\s+");
        if (tokens.length != 2) {
            writer.println("ERROR Incorrect format. Expected: LOGIN <user> <password>");
            return;
        }

        if (tokens[0].equals(tokens[1])) {
            user = tokens[0];
            writer.println("INFO Successful login as " + user);
            server.addClient(this);
        } else {
            writer.println("ERROR Incorrect username or password");
        }
    }
    private void logout(){
        user = null;
        server.removeClient(this);
        writer.println("MSG Successful logout");
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
