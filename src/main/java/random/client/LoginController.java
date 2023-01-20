package random.client;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    private Client client;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

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
        } catch (IOException e) {
            System.err.println("Login failed: "+e.getMessage());
        } catch (AuthError e) {
            System.out.println(e.getMessage());
        }
    }

    public void setClient(Client client){
        this.client = client;
    }

}
