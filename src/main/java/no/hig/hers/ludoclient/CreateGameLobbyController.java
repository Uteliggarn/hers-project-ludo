package no.hig.hers.ludoclient;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;

public class CreateGameLobbyController {
	
	@FXML private Label playerOne;
	@FXML private Label playerTwo;
	@FXML private Label playerThree;
	@FXML private Label playerFour;
	
	@FXML private ListView<String> playerList;
	
	@FXML private Button startGameButton;

	@FXML private MenuItem invite;
	
	private String hostName;
	
	public void initialize() {
		startGameButton.setDisable(true);
		playerOne.setText("");
		playerTwo.setText("");
		playerThree.setText("");
		playerFour.setText("");
	}
	
	public void addNewPlayerToList(String name) {
		if (!playerList.getItems().contains(name))
			playerList.getItems().add(name);
	}
	
	public void setHostPlayer(String hostName) {
		playerOne.setText(hostName.substring(4));
		this.hostName = hostName;
	}
	
	public void joinedPlayer(String name) {
		if (playerTwo.getText() == "") {
			playerTwo.setText(name);
			startGameButton.setDisable(false);
		}
		else if (playerThree.getText() == "")
			playerThree.setText(name);
		else if (playerFour.getText() == "")
			playerFour.setText(name);
	}
	
	
	@FXML private void invitePlayer(ActionEvent e) {
		String item = playerList.getSelectionModel().getSelectedItem();
		Main.sendText(Main.INVITE + item);
	}
	
	@FXML private void startGameButtonPressed(ActionEvent e) {
		
		FXMLLoader loader = new FXMLLoader();
		
		try {
			for (int i=0; i<Main.gameTabs.getTabs().size(); i++) {
				if (Main.gameTabs.getTabs().get(i).getId() == hostName) {
					Main.gameTabs.getTabs().get(i).setContent(loader.load(getClass().getResource("GameClient.fxml")));
				}	
			}
			//tab.setContent(loader.load(getClass().getResource("GameClient.fxml").openStream()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
