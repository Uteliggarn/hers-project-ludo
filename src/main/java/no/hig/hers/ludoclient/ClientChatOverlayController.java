package no.hig.hers.ludoclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ClientChatOverlayController {

    @FXML
    private TextField chatTextField;

    @FXML
    private ListView<?> playerListView;

    @FXML
    private TextArea chatTextArea;

    @FXML
    void sendChat(KeyEvent event) {
    	if (event.getCode() == KeyCode.ENTER) {
    		Main.showAlert("TEST", "TEST");
    	}
    }

}
