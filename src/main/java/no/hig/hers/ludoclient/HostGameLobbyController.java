package no.hig.hers.ludoclient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import no.hig.hers.ludoshared.Constants;
import javafx.scene.control.Button;

public class HostGameLobbyController {
	
	@FXML private Label playerOne;
	@FXML private Label playerTwo;
	@FXML private Label playerThree;
	@FXML private Label playerFour;
	@FXML private Label playerLabel;
	@FXML private Label hostLabel;	
	@FXML private Button startGameButton;

	private static BufferedWriter output;

	private ArrayList<String> playerJoinList = new ArrayList<>();
	
	private String hostName;
	
	public void initialize() {
		startGameButton.setDisable(false);
		setLabelText();
		playerOne.setText("");
		playerTwo.setText("");
		playerThree.setText("");
		playerFour.setText("");
	}
	
	public void setHostPlayer(String hostName) {
		playerOne.setText(hostName.substring(4));
		
		String tmp = Constants.GAMECHAT + hostName.substring(4, hostName.length());
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
	
	public void setLabelText() {
		playerLabel.setText(Main.messages.getString("INVITEDPLAYERS"));
		hostLabel.setText(Main.messages.getString("HOST"));
		startGameButton.setText(Main.messages.getString("STARTGAME"));
	}
	
	@FXML private void startGameButtonPressed() throws IOException {	
		try {
			sendText(Constants.GAMESTART);
		} catch (Exception ioe) {
			Main.LOGGER.log(Level.WARNING, "Unable to send message to server", ioe);
		}
	}

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
        	Main.LOGGER.log(Level.WARNING, "Unable to send message to server", ioe);
        }
    }

}