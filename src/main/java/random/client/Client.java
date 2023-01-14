package random.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements AutoCloseable{
    private final Socket socket;
    private final PrintWriter writer;
    private final BufferedReader reader;
    private final ClientReceiver receiver;
    private String user;
    public Client(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.receiver = new ClientReceiver(this);
        this.user = null;
    }
    public void login(String username, String password) throws IOException {
        writer.println("LOGIN "+username+" "+password);
        String response = reader.readLine();
        String errMsg;
        if(response.equals("LOGIN_SUCCESS "+username)){
            this.user = username;
            System.out.println("Logged in as "+username);
            receiver.start();
            return;
        }
        else if(response.startsWith("ERROR"))
            errMsg = response.split("\\s+", 2)[1];
        else
            errMsg = "Incorrect message received: "+response;

        System.err.println(errMsg);
    }

    public void logout() throws IOException {
        writer.println("LOGOUT");
    }

    public void readCommands(){
        String msg;
        try {
            while (!socket.isClosed() && (msg = reader.readLine()) != null && !msg.equals("LOGOUT_SUCCESS")) {
                System.out.println("Received message:");
                System.out.println(msg);
            }
        }catch (IOException e){
            System.out.println("error reading: "+e.getMessage());
        }

        try {
            close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        user = null;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
