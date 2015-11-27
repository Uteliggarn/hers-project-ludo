package no.hig.hers.ludoclient;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import no.hig.hers.ludoshared.Constants;

/**
 * 
 * Handles alle GUI for thge playerGameLobby
 * Setting the invited player labels and the host label
 * and removing those who leave or adding those who join th lobby
 * 
 * @author Petter on 27.11.2015 
 */
public class PlayerGameLobbyController {

	@FXML private Label playerOne;
	@FXML private Label playerTwo;
	@FXML private Label playerThree;
	@FXML private Label playerFour;
	@FXML private Label playerLabel;
	@FXML private Label hostLabel;
	
	String host;
	
	private ArrayList<String> playerJoinList = new ArrayList<>();
	
	/**
	 * Sets the internationilization of labels
	 * and sets all the labels to emtpy
	 */
	public void initialize() {
		setLabelText();
		playerOne.setText("");
		playerTwo.setText("");
		playerThree.setText("");
		playerFour.setText("");
	}
	
	/**
	 * Set the label for the host to the name of the game host
	 * @param hostName is the name of the one hosting the game
	 */
	public void setHostPlayer(String hostName) {
		playerOne.setText(hostName.substring(4));
		host = hostName;
		joinGameChat();
	}
	
	/**
	 * sets the internationilazation of the labels
	 */
	public void setLabelText() {
		playerLabel.setText(Main.messages.getString("INVITEDPLAYERS"));
		hostLabel.setText(Main.messages.getString("HOST"));
	}
	
	/**
	 * sets the invited player labels to empty
	 */
	private void clearJoinedPlayers() {
		playerTwo.setText("");
		playerThree.setText("");
		playerFour.setText("");
	}
	
	/**
	 * Sets the empty labels of the players that got invited
	 * too the game. And removes the startgame lock when
	 * there are two players or more
	 * 
	 * @param name The name of the player that got invited
	 */
	public void setPlayer(String name) {
		if (playerTwo.getText() == "") 
			playerTwo.setText(name);
		else if (playerThree.getText() == "")
			playerThree.setText(name);
		else if (playerFour.getText() == "") 
			playerFour.setText(name);
	}
	
	/**
	 * Sets all labels to empty "", and checks if the playerJoinList containts the name
	 * if so the name is added and the startGameButton is opened
	 * @param name of the player who joined the lobby
	 */
	public void cleanUp(String name) {
		clearJoinedPlayers();
		if (!playerJoinList.contains(name) && playerJoinList.size() < 3) {
			playerJoinList.add(name);
			for (int i=0; i<playerJoinList.size(); i++) {
				setPlayer(name);
			}
		}
	}
	
	/**
	 * Checks if the list contains the name and then removes it
	 * Also set the startGameButton too closed if less than one player
	 * is in the lobby
	 * @param name of the player to be removed
	 */
	public void cleanRemove(String name) {
		if (playerJoinList.contains(name)) {
			playerJoinList.remove(name);
			removePlayer(name);
		}
	}
	
	/**
	 * Removes the player from the lobby label
	 * @param name to be removed from label
	 */
	public void removePlayer(String name) {
		if (playerTwo.getText().equals(name))
			playerTwo.setText("");
		else if (playerThree.getText().equals(name))
			playerTwo.setText("");
		else if (playerFour.getText().equals(name))
			playerTwo.setText("");
	}
	
	/**
	 * joines the gameChat of the given host
	 */
	public void joinGameChat() {
		String tmp;
		tmp = (Constants.GAMECHAT + host.substring(4, host.length()));
		
		Main.cHandler.addNewChat(tmp);
	}
	
}


