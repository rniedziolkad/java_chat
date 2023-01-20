package random.client;

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

public class ChatController{
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
        HBox messageHBox = new HBox();
        try {
            messageHBox = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("message.fxml")));
            Label messageLabel = (Label) messageHBox.getChildren().get(1);
            messageLabel.setText(message);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        messages.add(messageHBox);
        chatMessagesList.scrollTo(messages.size());
    }


    public void initialize() {
        chatMessagesList.setItems(messages);
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
