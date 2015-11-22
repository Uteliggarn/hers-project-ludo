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

	private static BufferedReader input;
	private static BufferedWriter output;
	
	public void setConnetion(BufferedWriter write, BufferedReader read) {
		output = write;
		input = read;
	}

}


