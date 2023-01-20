package random.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    private Client client;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML
    protected void onLogin(){
        String username = usernameField.getText();
        String password = passwordField.getText();

        if(client == null) {
            System.err.println("No connection to server!");
            return;
        }
        try {
            client.login(username, password);
            Stage stage = (Stage) errorLabel.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
            Parent main = loader.load();
            ChatController controller = loader.getController();
            controller.setClient(client);

            Scene scene = new Scene(main);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Login failed: "+e.getMessage());
        } catch (AuthError e) {
            errorLabel.setText(e.getMessage());
        }
    }

    public void setClient(Client client){
        this.client = client;
    }

}
