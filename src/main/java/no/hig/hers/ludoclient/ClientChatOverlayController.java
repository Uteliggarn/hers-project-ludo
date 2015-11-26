package no.hig.hers.ludoclient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import no.hig.hers.ludoshared.Constants;

public class ClientChatOverlayController {

    @FXML
    private TextField chatTextField;

    @FXML
    private ListView<String> playerListView;

    @FXML
    private TextArea chatTextArea;
    
    private String ID;

    /**
     * 
     * @param event
     */
    @FXML
    void sendChat(KeyEvent event) {
    	if (event.getCode() == KeyCode.ENTER && chatTextField.getText() != null) {
    		Main.sendText(Constants.CHATMESSAGE + this.ID + ":" + chatTextField.getText().toString());
    		chatTextField.setText(null);
    	}
    }
    
    public void receiveChatMessage(String msg) {
    	Platform.runLater(() -> {
    		chatTextArea.setWrapText(true);
	    	chatTextArea.appendText(msg + "\n");	
    	});
    }
    
    public void addUserToList(String name) {
    	Platform.runLater(() -> {
    		if (!playerListView.getItems().contains(name))
    			playerListView.getItems().add(name);	
    	});
    }

	public void removeUserFromList(String username) {
		Platform.runLater(() -> {
			for (int i = 0; i < playerListView.getItems().size(); i++) {
				if (playerListView.getItems().get(i).equals(username))
					playerListView.getItems().remove(i);
			}
    	});
	}
	
	public void setID(String id) {
		this.ID = id;
	}
}
