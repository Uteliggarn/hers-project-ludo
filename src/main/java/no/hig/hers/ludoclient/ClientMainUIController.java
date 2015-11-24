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
    private Button buttonTest;
    
    @FXML
    private Button newGameButton;
    
    @FXML
    private Button queueButton;
    
    @FXML
    private Button buttonAddChat;
    
    @FXML
    private Button buttonCreateChat;
    
    @FXML
    private TabPane chatTabPane;
    
    @FXML
    private CheckBox checkBoxHideChat;
    
    @FXML
    private TabPane gameTabs; 
    
    private int count = 0;
    
    @FXML
    private ListView<String> chatListView;
    

    @FXML
	public void initialize() {
    	
	}

    @FXML
    void newGameButtonPressed(ActionEvent event) {
    	for (int i=0; i<Main.gameTabs.getTabs().size(); i++) {
    		if (Main.gameTabs.getTabs().get(i).getId() == Main.IDGK + Main.userName) {
    			System.out.println("\nKom vi in i ++count?");
    			++count;
    		}
    		if (i+1 == Main.gameTabs.getTabs().size() && count == 0) {
    			Main.sendText(Main.CREATEGAME);
    			count = 0;
    		}
    		else if (i+1 == Main.gameTabs.getTabs().size() && count != 0)
    			Main.showAlert("Error", "You have already created a game.");
    	}
    }
    /**
     * Creates a TextInputDialog, asking the user to type a name for the new chat room.
     * It then creates the new chat room by sending a message to the server with the name.
     * Lastly, joins the new chat.
     * @param event
     */
    @FXML
    void createChatButtonPressed(ActionEvent event) {
    	TextInputDialog dialog = new TextInputDialog("");
    	dialog.setTitle("Create a new chatroom");
    	dialog.setHeaderText(null);
    	dialog.setContentText("Please enter the name of the chatroom:");

    	Optional<String> result = dialog.showAndWait();
    	result.ifPresent(name -> Main.sendText(Main.NEWCHAT + name));
    	result.ifPresent(name -> Main.sendText(name + Main.JOINCHAT + Main.userName));
    }
    
    @FXML
    void queueButtonPressed(ActionEvent event) {
    	Main.sendText(Main.QUEUE);
    	queueButton.setDisable(true);
    }
    
    @FXML
    void joinChat(ActionEvent event) {
    	String chatName = chatListView.getSelectionModel().getSelectedItem();
    	Main.cHandler.addNewChat(chatName);
    }
    
    @FXML
    void hideChat(ActionEvent event) {
    	if (checkBoxHideChat.isSelected()) {
    		chatTabPane.setVisible(false);
    	} else chatTabPane.setVisible(true);
    }

    public void addChatToList(String name) {
       	Platform.runLater(new Runnable() {
    		@Override
    		public void run() {
    			chatListView.getItems().add(name);	
    		}});
    }
    
	public void setLabelUserName(String username) {
		labelUserName.setText(username);
	}
    
}
