package no.hig.hers.ludoclient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import no.hig.hers.ludoshared.Constants;

/**
 * Handles messages sent and received from a user
 * and the controls that only players in the chat
 * are shown in the playerListView
 *
 */
public class ClientChatOverlayController {

    @FXML
    private TextField chatTextField;

    @FXML
    private ListView<String> playerListView;

    @FXML
    private TextArea chatTextArea;
    
    private String ID;

    /**
     * Sends the message written in the TextField
     * @param event for when "Enter" is pressed
     */
    @FXML
    void sendChat(KeyEvent event) {
    	if (event.getCode() == KeyCode.ENTER && chatTextField.getText() != null) {
    		Main.sendText(Constants.CHATMESSAGE + this.ID + ":" + chatTextField.getText().toString());
    		chatTextField.setText(null);
    	}
    }
    
    /**
     * Appends too the TextArea
     * @param msg contains the new message to be appended
     */
    public void receiveChatMessage(String msg) {
    	Platform.runLater(() -> {
    		chatTextArea.setWrapText(true);
	    	chatTextArea.appendText(msg + "\n");	
    	});
    }
    
    /**
     * Adds a new user too playerListView
     * @param name of the user to be added
     */
    public void addUserToList(String name) {
    	Platform.runLater(() -> {
    		if (!playerListView.getItems().contains(name))
    			playerListView.getItems().add(name);	
    	});
    }

    /**
     * Removes the given name from playerListView
     * @param username too be removed
     */
	public void removeUserFromList(String username) {
		Platform.runLater(() -> {
			for (int i = 0; i < playerListView.getItems().size(); i++) {
				if (playerListView.getItems().get(i).equals(username))
					playerListView.getItems().remove(i);
			}
    	});
	}
	
	/**
	 * Sets the id
	 * @param id sets the id to object ID
	 */
	public void setID(String id) {
		this.ID = id;
	}
}
