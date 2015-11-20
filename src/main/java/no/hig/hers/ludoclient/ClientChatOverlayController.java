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
    
    private ObservableList<String> players = FXCollections.observableArrayList();
    
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
				players.add(name);
		    	removeUserFromList(name);
		    	playerListView.setItems(players);
			}
    	});
    	
    }

	public void removeUserFromList(String username) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).toString().equals(username)) {
				players.remove(i);
			}
		}
	}
	
	public void setID(String id) {
		this.ID = id;
	}
}
