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
            stage.setTitle("Random Chat");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
            Parent main = loader.load();
            ChatController controller = loader.getController();
            Scene scene = new Scene(main);
            stage.setScene(scene);
            stage.show();

            client = new Client(HOST, SERVER_PORT);
            controller.setClient(client);
            client.registerMessageListener(controller);
            client.registerUserEvenListener(controller);
            client.login("admin", "admin");
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void stop() throws Exception {
        if(client != null)
            client.logout();
        super.stop();
    }
}
