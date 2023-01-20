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
    }

    @Override
    public void start(Stage stage) {
        try{
            client = new Client(HOST, SERVER_PORT);
        } catch (IOException e) {
            client = null;
            System.err.println("Unable to connect to server: "+e.getMessage());
        }

        try{
            stage.setTitle("Random Chat");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent loginPage = loader.load();
            LoginController controller = loader.getController();
            controller.setClient(client);
            controller.setStage(stage);

            Scene scene = new Scene(loginPage);
            stage.setScene(scene);
            stage.show();

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
