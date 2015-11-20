package no.hig.hers.ludoclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class HostGameLobbyController {
	
	@FXML private Label playerOne;
	@FXML private Label playerTwo;
	@FXML private Label playerThree;
	@FXML private Label playerFour;
	
	@FXML private Button startGameButton;
	
	private int serverPort;
	/*
	public void initialize() {
		
	}
	*/
	public void test(String name) {
		playerOne.setText(name);
	}
	
	
	@FXML private void startGameButtonPressed(ActionEvent e) {
		
	}
	
	public void getServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
}