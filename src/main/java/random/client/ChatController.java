package random.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ChatController implements MessageListener{
    private Client client;
    @FXML
    private TextField messageInput;

    private final ObservableList<HBox> messages = FXCollections.observableArrayList(new ArrayList<>());
    @FXML
    private ListView<HBox> chatMessagesList;

    @FXML
    protected void onSend(){
        String message = messageInput.getText();
        messageInput.clear();
        if(client != null) {
            client.send(message);
        }
    }

    public void initialize() {
        chatMessagesList.setItems(messages);
    }

    public void setClient(Client client) {
        this.client = client;
    }
    @Override
    public void notifyAboutNewMessage(String fromUser, String message) {
        System.out.println("IN CONTROLLER");
        Platform.runLater(()->{
            HBox messageHBox = new HBox();
            try {
                messageHBox = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("message.fxml")));
                Label userLabel = (Label) messageHBox.getChildren().get(0);
                Label messageLabel = (Label) messageHBox.getChildren().get(1);
                userLabel.setText(fromUser);
                messageLabel.setText(message);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            messages.add(messageHBox);
        });

    }
}
