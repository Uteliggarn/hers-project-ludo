package no.hig.hers.ludoclient;

import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;

public class ClientMainUIController {
	@FXML
	private Label labelUserName;

    @FXML
    private Button queueButton;

    @FXML
    private TabPane chatTabPane;
    
    @FXML
    private CheckBox checkBoxHideChat;
    
    private int count = 0;
    
    @FXML
    private ListView<String> chatListView;
    
    @FXML
    void newGameButtonPressed() {
    	for (int i=0; i<Main.gameTabs.getTabs().size(); i++) {
    		if (Main.gameTabs.getTabs().get(i).getId().equals(Main.IDGK + Main.userName))
    			++count;
    		if (i+1 == Main.gameTabs.getTabs().size() && count == 0) {
    			Main.sendText(Main.CREATEGAME);
    			count = 0;
    		}
    		else if (i+1 == Main.gameTabs.getTabs().size() && count > 0)
    			Main.showAlert("Error", "You're allready hosting a game.");
    	}
    }
    /**
     * Creates a TextInputDialog, asking the user to type a name for the new chat room.
     * It then creates the new chat room by sending a message to the server with the name.
     * Lastly, joins the new chat.
     * @param event
     */
    @FXML
    void createChatButtonPressed() {
    	TextInputDialog dialog = new TextInputDialog("");
    	dialog.setTitle("Create a new chatroom");
    	dialog.setHeaderText(null);
    	dialog.setContentText("Please enter the name of the chatroom:");

    	Optional<String> result = dialog.showAndWait();
    	result.ifPresent(name -> Main.sendText(Main.NEWCHAT + name));
    	result.ifPresent(name -> Main.sendText(name + Main.JOINCHAT + Main.userName));
    }
    
    @FXML
    void queueButtonPressed() {
    	Main.sendText(Main.QUEUE);
    	queueButton.setDisable(true);
    }
    
    @FXML
    void joinChat() {
    	String chatName = chatListView.getSelectionModel().getSelectedItem();
    	Main.cHandler.addNewChat(chatName);
    }
    
    @FXML
    void hideChat() {
    	if (checkBoxHideChat.isSelected()) {
    		chatTabPane.setVisible(false);
    	} else chatTabPane.setVisible(true);
    }

    public void addChatToList(String name) {
       	Platform.runLater(() -> {
    			chatListView.getItems().add(name);	
    	});
    }
    
	public void setLabelUserName(String username) {
		labelUserName.setText(username);
	}
    
}
