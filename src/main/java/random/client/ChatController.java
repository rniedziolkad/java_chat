package random.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class ChatController{
    @FXML
    private TextField messageInput;

    private final ObservableList<AnchorPane> messages = FXCollections.observableArrayList(new ArrayList<>());
    @FXML
    private ListView<AnchorPane> chatMessagesList;

    @FXML
    protected void onSend(){
        String message = messageInput.getText();
        System.out.println(message);
        messageInput.clear();
        Label text = new Label("Username: "+message);
        text.setWrapText(true);
        text.setTextFill(new Color(1,1,1,1));
        AnchorPane messageBox = new AnchorPane(text);
        AnchorPane.setLeftAnchor(text, 0.0);
        AnchorPane.setRightAnchor(text, 0.0);
        AnchorPane.setTopAnchor(text, 0.0);
        AnchorPane.setBottomAnchor(text, 0.0);
        messageBox.setMinWidth(0);
        messageBox.setPrefWidth(1);
        messages.add(messageBox);
        chatMessagesList.scrollTo(messages.size());
    }


    public void setItems() {
        chatMessagesList.setItems(messages);
    }
}
