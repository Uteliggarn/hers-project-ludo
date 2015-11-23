package no.hig.hers.ludoclient;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public class ClientMainUIController {
    @FXML
    private Button buttonTest;
    
    @FXML
    private Button newGameButton;
    
    @FXML
    private Button queueButton;
    
    @FXML
    private TabPane chatTabPane;
    
    @FXML
    private TabPane gameTabs; 
    
    private int count = 0;
    
    @FXML
    private ListView<String> chatListView;

    @FXML
	public void initialize() {
		//chatTextArea.setMouseTransparent(false);
		chatTabPane.setMouseTransparent(true);
	}
    @FXML
    void testCode(ActionEvent event) {
    	Tab newTab = new Tab("random");	
    	chatTabPane.getTabs().add(newTab);
    }
    
    @FXML
    void newGameButtonPressed(ActionEvent event) {
    	for (int i=0; i<Main.gameTabs.getTabs().size(); i++) {
    		if (Main.gameTabs.getTabs().get(i).getId() == Main.IDGK + Main.userName) {
    			++count;
    		}
    		if (i+1 == Main.gameTabs.getTabs().size() && count == 0) {
    			Main.sendText(Main.CREATEGAME);
    			count = 0;
    		}
    		else if (i+1 == Main.gameTabs.getTabs().size() && count != 0)
    			Main.showAlert("Error", "You have allready created a game.");
    	}
    }
    
    @FXML
    void queueButtonPressed(ActionEvent event) {
    	Main.sendText("queue");
    	queueButton.setDisable(true);
    	
    }
    
    @FXML
    void joinChat(ActionEvent event) {
    	String chatName = chatListView.getSelectionModel().getSelectedItem();
    	Main.cHandler.addNewChat(chatName);
    }
    
    public void addChatToList(String name) {
       	Platform.runLater(new Runnable() {
    		@Override
    		public void run() {
    			chatListView.getItems().add(name);	
    		}});
    }
    
    public void newGameTab() {
    	try {
    		
    		Main.sendText("createGame");
    		
    		int count = Main.input.read();
    		
    		System.out.println("\nHva er count:" + count);
    		
    		if (count != -1) {
    		
				Tab tmp = new Tab("Ludo");
				
				FXMLLoader loader = new FXMLLoader();
				
				tmp.setContent(loader.load(getClass().getResource("CreateGameLobby.fxml").openStream()));
				
				CreateGameLobbyController createLobbyWindowController = (CreateGameLobbyController) loader.getController();
				
				for (int i=0; i<1; i++) {
					String msg = Main.input.readLine();
					System.out.println("\nHva kommer in som msg: " + msg);
					if (!msg.substring(7).equals(Main.userName))    ;
						createLobbyWindowController.addNewPlayerToList(msg.substring(7));
				}
				
				gameTabs.getTabs().add(tmp);
				gameTabs.getSelectionModel().select(tmp);
    		}
    		else
    			Main.showAlert("Error", "Could not create game. You're allready hosting a game");
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
    	}
    	
    	//gameTabs.getTabs().add(tab);
    	//gameTabs.getSelectionModel().select(tab);
    }
    
}
