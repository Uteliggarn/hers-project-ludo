package no.hig.hers.ludoclient;

import java.io.IOException;

import javax.swing.JOptionPane;

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

    @FXML
    void testCode(ActionEvent event) {
    	Tab newTab = new Tab("random");	
    	chatTabPane.getTabs().add(newTab);
    }
    
    @FXML
    void newGameButtonPressed(ActionEvent event) {
    	newGameTab();
    }
    
    @FXML
    void queueButtonPressed(ActionEvent event) {
    	Main.sendText("queue");
    	queueButton.setDisable(true);
    	
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
				
			//	CreateGameLobbyController createLobbyWindowController = (CreateGameLobbyController) loader.getController();
				
				for (int i=0; i<1; i++) {
					String msg = Main.input.readLine();
					System.out.println("\nHva kommer in som msg: " + msg);
					if (!msg.substring(7).equals(Main.userName))    ;
					//	createLobbyWindowController.addNewPlayerToList(msg.substring(7));
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
