package no.hig.hers.ludoclient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import no.hig.hers.ludoshared.Constants;

/**
 * Handles the createGameLobby GUI elements 
 * Where the start game button is set to disable until 1 more player
 * has joined the lobby other than the host.
 * updates the playerList in order to invite players
 */
public class CreateGameLobbyController {
	
	@FXML private Label playerOne;
	@FXML private Label playerTwo;
	@FXML private Label playerThree;
	@FXML private Label playerFour;
	@FXML private Label playerLabel;
	@FXML private Label hostLabel;
	
	@FXML private ListView<String> playerList;
	
	@FXML private Button startGameButton;
	
	private ArrayList<String> playerJoinList = new ArrayList<>();
	
	private static BufferedWriter output;
	
	private String hostName;
	
	/**
	 * Disables the button until more players have joined
	 * and adds players online to the playerList
	 * starts the method for Internationalization
	 */
	public void initialize() {
		startGameButton.setDisable(true);
		setLabelText();	
		playerOne.setText("");
		playerTwo.setText("");
		playerThree.setText("");
		playerFour.setText("");
		
		for (int i=0; i<Main.playerList.size(); i++) {
			if (!Main.playerList.get(i).equals(Main.userName))
				playerList.getItems().add(Main.playerList.get(i));
		}
	}
	
	/**
	 * Internationalization on playerLabel, hostLabel and startGameButoon
	 */
	public void setLabelText() {
		playerLabel.setText(Main.messages.getString("INVITEDPLAYERS"));
		hostLabel.setText(Main.messages.getString("HOST"));
		startGameButton.setText(Main.messages.getString("STARTGAME"));
	}
	
	/**
	 * Adds player to the playerList
	 * @param name of the player to be added
	 */
	public void addPlayerToList(String name) {
		if (!name.equals(Main.userName))
			playerList.getItems().add(name);
	}
	
	/**
	 * Removes the player from the playerList
	 * @param name of the player to be removed
	 */
	public void removePlayerFromList(String name) {
		playerList.getSelectionModel().clearSelection();
		playerList.getItems().remove(name);
	}
	
	/**
	 * Sets the name of the player that host the game
	 * @param hostName The one hosting the game
	 */
	public void setHostPlayer(String hostName) {
		String tmp;
		playerOne.setText(hostName.substring(4));
		this.hostName = hostName;
		tmp = (Constants.GAMECHAT + hostName.substring(4));
		Main.cHandler.addNewChat(tmp);
		
	}
	
	private void clearJoinedPlayers() {
		playerTwo.setText("");
		playerThree.setText("");
		playerFour.setText("");
	}
	
	/**
	 * Sets the empty labels of the players that got invited
	 * too the game. And removes the startgame lock when
	 * there are two players or more
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
			if (playerJoinList.size() >= 1)
				startGameButton.setDisable(false);
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
			if (playerJoinList.size() < 1)
				startGameButton.setDisable(true);
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
	 * The host selects an player from the playerList to 
	 * send an invite too.
	 */
	@FXML private void invitePlayer() {
		String item = playerList.getSelectionModel().getSelectedItem();
		Main.sendText(Constants.INVITE + item);
	}
	
	/**
	 * Sends "gamestart" too the gameserver
	 */
	@FXML private void startGameButtonPressed() {
		try {
			sendText(Constants.GAMESTART);
		} catch (Exception ioe) {
			Main.LOGGER.log(Level.SEVERE, "Error trying to send text to server", ioe);
		}
	}
	
	/**
	 * Sets the gamerserver connection too an output
	 * for sending messages to the gameserver
	 * @param write connection from GameHandler
	 */
	public void setConnetion(BufferedWriter write) {
		output = write;
	}
	
	/**
     * Method used to send a message to the server. Handled in a separate method
     * to ensure that all messages are ended with a newline character and are
     * flushed (ensure they are sent.)
     * 
     * @param textToSend
     *            the message to send to the server
     */
    public static void sendText(String textToSend) {
        try {
            output.write(textToSend);
            output.newLine();
            output.flush();
        } catch (IOException ioe) {
        	Main.LOGGER.log(Level.SEVERE, "Can't send message to server", ioe);
        }
    }
}
