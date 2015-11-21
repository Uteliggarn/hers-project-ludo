package no.hig.hers.ludoclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;

public class CreateGameLobbyController {
	
	@FXML private Label playerOne;
	@FXML private Label playerTwo;
	@FXML private Label playerThree;
	@FXML private Label playerFour;
	
	@FXML private ListView<String> playerList;
	
	@FXML private Button startGameButton;

	@FXML private MenuItem invite;
	
	public void addNewPlayerToList(String name) {
		playerList.getItems().add(name);
	}
	
	@FXML private void invitePlayer(ActionEvent e) {
		String item = playerList.getSelectionModel().getSelectedItem();
		Main.sendText("invite:" + item);
	}
	
	@FXML private void startGameButtonPressed(ActionEvent e) {
		
	}
}
