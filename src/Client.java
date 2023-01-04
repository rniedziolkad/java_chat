import java.io.IOException;
import java.net.Socket;

public class Client {
    private static final int SERVER_PORT = 54321;
    private static final String HOST = "localhost";
    public static void main(String[] args){

        try(Socket socket = new Socket(HOST, SERVER_PORT)){
            System.out.println("Connected!");
            System.out.println(socket.getInetAddress() + ":" + socket.getPort());
            System.out.println("On local port: "+socket.getLocalPort());
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

    }
}
