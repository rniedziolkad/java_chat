package random.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class ClientMain extends Application {
    private static final int SERVER_PORT = 54321;
    private static final String HOST = "localhost";

    public static void main(String[] args) {
        try (Client client = new Client(HOST, SERVER_PORT)){
            client.login("admin", "admin");
            launch();
            client.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("EXITING APP...");
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Random Chat");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
        Parent main = loader.load();
        Scene scene = new Scene(main);

        stage.setScene(scene);
        stage.show();
    }
}
