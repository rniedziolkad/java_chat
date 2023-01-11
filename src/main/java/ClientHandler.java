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
        System.out.println("Connection from: "+clientSocket.getInetAddress()+":"+clientSocket.getPort());
        try {
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            closeSocket();
        }
        handleSocket();
        if(user != null)
            server.removeClient(this);
        System.out.println("Closed connection from: "+clientSocket.getInetAddress()+":"+clientSocket.getPort());
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
                    server.broadcastToConnected("MSG "+parts[1]);
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
            if(user != null)
                logout();
            user = tokens[0];
            server.addClient(this);
            writer.println("INFO LOGIN_SUCCESS " + user);
            server.getConnectedClients().forEachKey(16, (u)->send("EVENT USER_JOIN "+u));
        } else {
            writer.println("ERROR Incorrect username or password");
        }
    }
    private void logout(){
        if(user != null)
            server.removeClient(this);
        user = null;
        writer.println("INFO LOGOUT_SUCCESS");
    }

    private void closeSocket(){
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void send(String message){
        writer.println(message);
    }

    public String getUser() {
        return user;
    }
}
