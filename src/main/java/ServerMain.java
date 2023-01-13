import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ServerMain {
    private static final String DB_URL = "jdbc:sqlite:random.db";
    public static void main(String[] args){
        Connection con;
        try {
            con = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return;
        }
        Server server = new Server(54321, con);

        try(ServerSocket serverSocket = new ServerSocket(server.getPort())){
            Socket clientSocket;
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    ClientHandler newClient = new ClientHandler(clientSocket, server);
                    newClient.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            con.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
