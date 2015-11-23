package no.hig.hers.ludoclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PlayerGameLobbyController {

	@FXML private Label playerOne;
	@FXML private Label playerTwo;
	@FXML private Label playerThree;
	@FXML private Label playerFour;
	
	public void initialize() {
		playerOne.setText("");
		playerTwo.setText("");
		playerThree.setText("");
		playerFour.setText("");
	}
	
	public void joinedPlayer(String name) {
		if (playerOne.getText() == "")
			playerOne.setText(name);
		else if (playerTwo.getText() == "")
			playerTwo.setText(name);
		else if (playerThree.getText() == "")
			playerThree.setText(name);
		else if (playerFour.getText() == "")
			playerFour.setText(name);
	}
}


