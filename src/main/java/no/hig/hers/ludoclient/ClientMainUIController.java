package no.hig.hers.ludoclient;

import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import no.hig.hers.ludoshared.Constants;

public class ClientMainUIController {
	@FXML
	private Label labelUserName;

    @FXML
    private Button queueButton;

    @FXML
    private TabPane chatTabPane;
    
    @FXML
    private CheckBox checkBoxHideChat;

    @FXML
    private GridPane gridPane;
    
    private int count = 0;
    
    @FXML
    private ListView<String> chatListView;
    
	private final Label[][] topTenWonLabels = new Label[10][2];
	private final Label[][] topTenPlayedLabels = new Label[10][2];
	
	@FXML
	public void initialize() {
		createLabels();
	}
    
    @FXML
    void newGameButtonPressed() {
    	for (int i=0; i<Main.gameTabs.getTabs().size(); i++) {
    		if (Main.gameTabs.getTabs().get(i).getId().equals(Constants.IDGK + Main.userName)) 
    			++count;
    		if (i+1 == Main.gameTabs.getTabs().size() && count == 0) {
    			Main.sendText(Constants.CREATEGAME);
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
    	result.ifPresent(name -> Main.sendText(Constants.CHATMESSAGE + Constants.NEWCHAT + name));
    	result.ifPresent(name -> Main.cHandler.addNewChat(name));
    }
    
    @FXML
    void queueButtonPressed() {
    	Main.sendText(Constants.QUEUE);
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
    /**
     * Setting up the Labels,
     * first by creating new Labels, 
     * then setting the constraints,
     * finally adding them to the GridPane.  
     */
    public void createLabels() {
    	for (int i = 0; i < 10; i++) {
    		topTenWonLabels[i][0] = new Label();
    		topTenWonLabels[i][1] = new Label();
    		topTenPlayedLabels[i][0] = new Label();
    		topTenPlayedLabels[i][1] = new Label();
    	}
    	
    	for (int i = 0; i < 10; i++) {
    		GridPane.setConstraints(topTenWonLabels[i][0], 1, i+3, 1, 1, HPos.LEFT, VPos.CENTER);
    		GridPane.setConstraints(topTenWonLabels[i][1], 1, i+3, 1, 1, HPos.RIGHT, VPos.CENTER);
    		GridPane.setConstraints(topTenPlayedLabels[i][0], 3, i+3, 1, 1, HPos.LEFT, VPos.CENTER);
    		GridPane.setConstraints(topTenPlayedLabels[i][1], 3, i+3, 1, 1, HPos.RIGHT, VPos.CENTER);
    		
    		gridPane.getChildren().addAll(topTenWonLabels[i][0], topTenWonLabels[i][1],
    				topTenPlayedLabels[i][0], topTenPlayedLabels[i][1]);
    	}
    }
    /**
     * Sets the Top ten played list text
     * @param played The String 2D array with the new played list
     */
    public void setTopTenPlayed(String[][] played) {
    	Platform.runLater(() -> {
        	for (int i = 0; i < 10; i++) {
        		topTenPlayedLabels[i][0].setText(played[i][0]);
        		topTenPlayedLabels[i][1].setText(played[i][1]);
        	}
    	});
    }
    /**
     * Sets the Top ten played list text
     * @param played The String 2D array with the new played list
     */
    public void setTopTenWon(String[][] won) {
    	Platform.runLater(() -> {
	    	for (int i = 0; i < 10; i++) {
	    		topTenWonLabels[i][0].setText(won[i][0]);
	    		topTenWonLabels[i][1].setText(won[i][1]);
	    	}
    	});
    }

    public void addChatToList(String name) {
       	Platform.runLater(() -> {
    			chatListView.getItems().add(name);	
    	});
    }
    
	public void setLabelUserName(String username) {
		labelUserName.setText(username);
	}
	
	public void openQueue() {
		queueButton.setDisable(false);
	}

	@FXML
	void getPlayerList() {
		Main.getPlayers();
	}
	
}

