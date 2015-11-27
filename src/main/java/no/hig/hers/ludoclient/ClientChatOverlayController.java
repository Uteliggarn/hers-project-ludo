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
 * Class for handling the Chat Overlays.
 * @author daniel on 07.11.2015
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
     * Method for sending a text message.
     * Takes the text from the TextField when 'Enter' is pressed.
     * @param event The Keyboard event.
     */
    @FXML
    void sendChat(KeyEvent event) {
    	if (event.getCode() == KeyCode.ENTER && chatTextField.getText() != null) {
    		Main.sendText(Constants.CHATMESSAGE + this.ID + ":" + chatTextField.getText().toString());
    		chatTextField.setText(null);
    	}
    }
    
    /**
     * Method for adding a message to the TextArea in the chat.
     * @param msg The message to add
     */
    public void receiveChatMessage(String msg) {
    	Platform.runLater(() -> {
    		chatTextArea.setWrapText(true);
	    	chatTextArea.appendText(msg + "\n");	
    	});
    }
    
    /**
     * Method for adding a user to the playerlist in a chat.
     * @param name The username to add.
     */
    public void addUserToList(String name) {
    	Platform.runLater(() -> {
    		if (!playerListView.getItems().contains(name))
    			playerListView.getItems().add(name);	
    	});
    }

    /**
     * Method for removing a user from the playerlist in a chat.
     * @param username the username to remove.
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
	 * Method for setting ID
	 * @param id the ID to set.
	 */
	public void setID(String id) {
		this.ID = id;
	}
}
