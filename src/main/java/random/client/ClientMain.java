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

    private Client client;

    public static void main(String[] args) {
        launch();
        System.out.println("EXITING APP...");
    }

    @Override
    public void start(Stage stage) {
        try{
            client = new Client(HOST, SERVER_PORT);
            client.login("admin", "admin");

            stage.setTitle("Random Chat");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
            Parent main = loader.load();
            ChatController controller = loader.getController();
            controller.setClient(client);
            Scene scene = new Scene(main);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        client.logout();
        super.stop();
    }
}
