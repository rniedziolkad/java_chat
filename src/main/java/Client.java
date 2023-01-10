import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final int SERVER_PORT = 54321;
    private static final String HOST = "localhost";
    public static void main(String[] args){

        try(Socket socket = new Socket(HOST, SERVER_PORT)){
            System.out.println("Connected!");
            System.out.println(socket.getInetAddress() + ":" + socket.getPort());
            System.out.println("On local port: "+socket.getLocalPort());

            new ClientReceiver(socket).start();
            Scanner scanner = new Scanner(System.in);
            String message = "Random Chat Protocol";
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            while(!socket.isClosed() && !message.equals("EXIT")) {
                message = scanner.nextLine();
                writer.println(message);
            }
        } catch (IOException e){
            System.out.println("Exception IO: "+e.getMessage());
        }

    }
}
