package no.hig.hers.ludoclient;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private ListView<String> playerListView;

    @FXML
    private TextArea chatTextArea;
    
    private String ID;

    @FXML
    void sendChat(KeyEvent event) {
    	if (event.getCode() == KeyCode.ENTER) {
    		Main.sendText(this.ID + ":" + chatTextField.getText().toString());
    		System.out.println("Hva har vi i sendChat: " + this.ID + ":" + chatTextField.getText().toString());
    		chatTextField.setText(null);
    	}
    }
    
    public void receiveChatMessage(String msg) {
    	Platform.runLater(new Runnable() {
    		@Override
    		public void run() {
    	    	chatTextArea.setWrapText(true);
    	    	chatTextArea.appendText(msg + "\n");	
    		}});

    }
    
    public void addUserToList(String name) {
    	Platform.runLater(new Runnable() {
    		@Override
    		public void run() {
		    	playerListView.getItems().add(name);
			}
    	});
    	
    }

	public void removeUserFromList(String username) {
    	Platform.runLater(new Runnable() {
    		@Override
    		public void run() {
    			for (int i = 0; i < playerListView.getItems().size(); i++) {
    				if (playerListView.getItems().get(i).equals(username)) {
    					playerListView.getItems().remove(i);
    				}
    			}
    		}
    	});

	}
	
	public void setID(String id) {
		this.ID = id;
	}
}
