package no.hig.hers.ludoclient;

import java.util.Optional;

import javafx.application.Platform;
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

/**
 * Main controller for handling all of the main GUI features.
 * That includes the buttons in the "Main" tab aswell as the list of group chats
 * and the button for creating new group chats
 */
public class ClientMainUIController {
    @FXML
    private TabPane gameTabs;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button queueButton;

    @FXML
    private Button newGameButton;

    @FXML
    private Label labelUserName;

    @FXML
    private Label labelWelcome;

    @FXML
    private Label labelPlayerWon;
    
    @FXML
    private Label labelPlayerWonScore;

    @FXML
    private Label labelPlayerPlayed;
    
    @FXML
    private Label labelPlayerPlayedScore;
    
    @FXML
    private Label labelTopWon;

    @FXML
    private Label labelTopPlayed;

    @FXML
    private TabPane chatTabPane;

    @FXML
    private CheckBox checkBoxHideChat;

    @FXML
    private ListView<String> chatListView;
    
    @FXML
    private Button buttonCreateChat;

    
	private final Label[][] topTenWonLabels = new Label[10][2];
	private final Label[][] topTenPlayedLabels = new Label[10][2];
	
	/**
	 * Creates the labels for where the top ten lists are shown
	 * Internationalization of all the buttons and labels aswell
	 * when the controller is made
	 */
	@FXML
	public void initialize() {
		createLabels();
		
		setLabelText();
	}
	/**
	 * Method for setting the internationalized texts. 
	 */
	private void setLabelText() {
	    queueButton.setText(Main.messages.getString("BUTTONQUEUETEXT"));
	    newGameButton.setText(Main.messages.getString("BUTTONNEWGAMETEXT"));
	    buttonCreateChat.setText(Main.messages.getString("BUTTONCREATECHATTEXT"));
	    labelWelcome.setText(Main.messages.getString("WELCOMETEXT"));
	    labelPlayerWon.setText(Main.messages.getString("PLAYERWONTEXT"));
	    labelPlayerPlayed.setText(Main.messages.getString("PLAYERPLAYEDTEXT"));
	    labelTopWon.setText(Main.messages.getString("TOPWONTEXT"));
	    labelTopPlayed.setText(Main.messages.getString("TOPPLAYEDTEXT"));
	    checkBoxHideChat.setText(Main.messages.getString("HIDECHATTEXT"));
	}
    
	/**
	 * When button is pressed a "creategame" message is sent to
	 * server. And the button is disabled because you can only be the
	 * host of one game
	 */
    @FXML
    void newGameButtonPressed() {
    	int count = 0;
    	for (int i=0; i<Main.gameHandler.size(); i++) {
    		if (Main.gameHandler.get(i).getHostName().equals(Constants.IDGK + Main.userName))
    			++count;
    	}
    	if(count == 0 && Main.gameServer.isEmpty()) {
	    	Main.sendText(Constants.CREATEGAME);
			newGameButton.setDisable(true);
    	}
    	else
    		Main.showAlert(Main.messages.getString("BUTTONDECLINE"), Main.messages.getString("BUTTONDECLINE"));
    }
    
    /**
     * Removes the disable set when making a new game
     */
    public void openNewGameButton() {
    	newGameButton.setDisable(false);
    }
    /**
     * Creates a TextInputDialog, asking the user to type a name for the new chat room.
     * It then creates the new chat room by sending a message to the server with the name.
     * Lastly, joins the new chat.
     */
    @FXML
    void createChatButtonPressed() {
    	TextInputDialog dialog = new TextInputDialog("");
    	dialog.setTitle(Main.messages.getString("CHATROOMCREATETITLE"));
    	dialog.setHeaderText(null);
    	dialog.setContentText(Main.messages.getString("CHATROOMCREATECONTENT"));

    	Optional<String> result = dialog.showAndWait();
    	result.ifPresent(name -> Main.sendText(Constants.CHATMESSAGE + Constants.NEWCHAT + name));
    	result.ifPresent(name -> Main.cHandler.addNewChat(name));
    }
    
    /**
     * Sends a "queue" message to the server and disables the button
     */
    @FXML
    void queueButtonPressed() {
    	Main.sendText(Constants.QUEUE);
    	queueButton.setDisable(true);
    }
    
    /**
     * Joins the selected chat in the groupChatList
     */
    @FXML
    void joinChat() {
    	String chatName = chatListView.getSelectionModel().getSelectedItem();
    	Main.cHandler.addNewChat(chatName);
    }
    
    /**
     * Hides the chatOverlay 
     */
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
    
    /**
     * Method for adding a chat to the list
     * @param name The chatname to add
     */
    public void addChatToList(String name) {
       	Platform.runLater(() -> {
    			chatListView.getItems().add(name);	
    	});
    }
    
    /**
     * Method for setting the labelUserName.
     * This is run on login
     * @param username The username to set
     */
	public void setLabelUserName(String username) {
		labelUserName.setText(username);
	}
	
	/**
	 * Removes the disable on the queue button
	 */
	public void openQueue() {
		queueButton.setDisable(false);
	}
	
	/**
	 * Sets the score of the player in the "Main" tab
	 * @param won games by the user
	 * @param played games by the user
	 */
	public void setScores(String won, String played) {
		Platform.runLater(() -> {
		    labelPlayerWonScore.setText(won);
		    labelPlayerPlayedScore.setText(played);
		});
	}
	
	/**
	 * Method for removing a chat from the list.
 	 * @param name The chatname to remove.
	 */
	public void removeChatFromList(String name) {
		Platform.runLater(() -> {
			chatListView.getItems().remove(name);
		});
	}
	
}

