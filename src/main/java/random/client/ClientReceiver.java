package random.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReceiver extends Thread{
    Socket socket;
    BufferedReader reader;

    public ClientReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        String line;
        while(!socket.isClosed()){
            try {
                line = reader.readLine();
                System.out.println(line);
                if(line==null) {
                    System.out.println("random.server.Server closed connection");
                    closeSocket();
                }
            } catch (IOException e) {
                System.out.println("error: "+e.getMessage());
                closeSocket();
            }
        }
        System.out.println("Closing receiver");
    }

    private void closeSocket(){
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("exception: "+e.getMessage());
        }
    }
}
