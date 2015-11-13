package no.hig.hers.ludoclient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class GameLobbyWindowController {
	
	@FXML private Label playerOne;
	@FXML private Label playerTwo;
	@FXML private Label playerThree;
	@FXML private Label playerFour;
	
	public void initialize() {
		
	}
	
	
	
	public void test(String name) {
		playerOne.setText(name);
	}

}
