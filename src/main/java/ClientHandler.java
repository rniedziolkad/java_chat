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
        String [] tokens = line.split("\\s");
        if (tokens.length<1)
            writer.println("Incorrect format: "+ line);
        else{
            String cmd = tokens[0];
            switch (cmd) {
                case "LOGIN" -> {
                    writer.println("LOGGING...");
                    login(tokens);
                }

                case "LOGOUT" -> {
                    writer.println("LOGGING OUT...");
                    logout();
                }
                case "EXIT" -> {
                    writer.println("EXITING...");
                    closeSocket();
                }
                case "BROADCAST" -> writer.println("BROADCASTING...");
                default -> writer.println("Unknown command: " + line);
            }
        }
    }

    private void login(String [] tokens) {
        if (tokens.length != 3) {
            writer.println("Incorrect format. Expected: LOGIN user password");
            return;
        }
        if (tokens[1].equals(tokens[2])) {
            user = tokens[1];
            writer.println("MSG Successful login as " + user);
        } else {
            writer.println("MSG Incorrect username or password");
        }
    }
    private void logout(){
        user = null;
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
