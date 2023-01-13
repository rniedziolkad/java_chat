import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler extends Thread{
    private final Socket clientSocket;
    private final Server server;
    private BufferedReader reader;
    private PrintWriter writer;
    private final UserManager userManager;

    public ClientHandler(Socket clientSocket, Server server) {
        UserManager uManager = null;
        try {
            uManager = new UserManager(server.getDbConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.clientSocket = clientSocket;
        this.server = server;
        this.userManager = uManager;
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
        if(userManager.isUserLoggedIn())
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
            case "REGISTER" -> {
                if (parts.length == 2)
                    register(parts[1]);
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
        if(userManager == null){
            writer.println("ERROR Server side error. Unable to authenticate user");
            return;
        }
        if(userManager.isUserLoggedIn()) {
            writer.println("ERROR Already logged in");
            return;
        }

        try{
            userManager.login(tokens[0], tokens[1]);
            server.addClient(this);
            writer.println("INFO LOGIN_SUCCESS " + userManager.getCurrentUser());
            server.getConnectedClients().forEachKey(16, (u)->send("EVENT USER_JOIN "+u));
        } catch (AuthDataException e) {
            writer.println("ERROR "+e.getMessage());
        } catch (SQLException e) {
            System.out.println("Login error: "+e.getMessage());
            writer.println("ERROR Server side error. Unable to authenticate user");
        }
    }

    private void register(String args){
        String [] tokens = args.split("\\s+");
        if (tokens.length != 2) {
            writer.println("ERROR Incorrect format. Expected: REGISTER <user> <password>");
            return;
        }
        if(userManager == null){
            writer.println("ERROR Server side error. Unable to register user");
            return;
        }

        try{
            userManager.register(tokens[0], tokens[1]);
            writer.println("INFO REGISTER_SUCCESS "+tokens[0]);
        } catch (UserExistsException e) {
            writer.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Register error: "+e.getMessage());
            writer.println("ERROR Server side error. Unable to register user");
        }
    }

    private void logout(){
        if(userManager.isUserLoggedIn())
            server.removeClient(this);
        userManager.logout();
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
        return userManager.getCurrentUser();
    }
}
