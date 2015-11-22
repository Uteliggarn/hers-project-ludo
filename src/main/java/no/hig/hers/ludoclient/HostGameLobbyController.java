package no.hig.hers.ludoclient;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.Parent;
import javafx.scene.control.Button;

public class HostGameLobbyController {
	
	@FXML private Label playerOne;
	@FXML private Label playerTwo;
	@FXML private Label playerThree;
	@FXML private Label playerFour;
	
	@FXML private Button startGameButton;
	
	private int serverPort;
	
	public void initialize() {
		startGameButton.setDisable(false);
	}
	
	public void test(String name) {
		playerOne.setText(name);
	}
	
	
	@FXML private void startGameButtonPressed(ActionEvent e) throws IOException {
		Tab tab = Main.gameTabs.getTabs().get(1);
		//FXMLLoader loader = new FXMLLoader();
		Parent root = (Parent)FXMLLoader.load(getClass().getResource("GameClient.fxml"));
		
		try {
			tab.setContent(root);
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void getServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
}