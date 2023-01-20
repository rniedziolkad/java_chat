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
    private PasswordField passwordField2;
    @FXML
    private Label errorLabel;
    private Stage stage;

    @FXML
    protected void onLogin(){
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isBlank()){
            errorLabel.setText("Username is required");
            return;
        }
        if(password.isBlank()){
            errorLabel.setText("Password is required");
            return;
        }
        if(client == null) {
            errorLabel.setText("No connection to server!");
            return;
        }
        try {
            if(client.login(username, password)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
                Parent main = loader.load();
                ChatController controller = loader.getController();
                controller.setClient(client);
                controller.setStage(stage);

                Scene scene = new Scene(main);
                stage.setScene(scene);
            }
        } catch (IOException e) {
            System.err.println("Login failed: "+e.getMessage());
        } catch (AuthError e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private void changeScene(String fxml_file, String message) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml_file));
        Parent main = loader.load();
        LoginController controller = loader.getController();
        controller.setClient(client);
        controller.setStage(stage);
        controller.setMessage(message);

        Scene scene = new Scene(main);
        stage.setScene(scene);
    }

    @FXML
    protected void onRegister(){
        String username = usernameField.getText();
        String password = passwordField.getText();
        if(username.isBlank()){
            errorLabel.setText("Username is required");
            return;
        }
        if(password.isBlank()){
            errorLabel.setText("Password is required");
            return;
        }
        if(!passwordField2.getText().equals(password)) {
            errorLabel.setText("Passwords do not match");
            return;
        }
        if(client == null){
            errorLabel.setText("No connection to server!");
            return;
        }
        try {
            if(client.register(username, password)){
                changeScene("login.fxml", "User registered successfully.");
            }
        } catch (AuthError e) {
            errorLabel.setText(e.getMessage());
        } catch (IOException e) {
            System.err.println("Login failed: "+e.getMessage());
        }
    }
    @FXML
    protected void onBackLogin(){
        try {
            changeScene("login.fxml", "");
        } catch (IOException e) {
            System.err.println("Failed to go back...");
        }
    }
    @FXML
    protected void onGoRegister(){
        try {
            changeScene("register.fxml", "");
        } catch (IOException e) {
            System.err.println("Failed to go to register page");
        }
    }

    public void setClient(Client client){
        this.client = client;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setMessage(String message){
        this.errorLabel.setText(message);
    }

}
