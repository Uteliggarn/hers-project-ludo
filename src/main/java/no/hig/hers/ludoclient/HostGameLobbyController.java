package no.hig.hers.ludoclient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import no.hig.hers.ludoshared.Constants;
import javafx.scene.control.Button;
/**
 * Class that is made by the host player when the queue game option is chosen.
 * The other players than the host makes a PlayerGameLobbyController instead.
 * The player of that is host can start the game. 
 * The player also joins the game chat.
 */
public class HostGameLobbyController {
	
	@FXML private Label playerOne;
	@FXML private Label playerTwo;
	@FXML private Label playerThree;
	@FXML private Label playerFour;
	@FXML private Label playerLabel;
	@FXML private Label hostLabel;	
	@FXML private Button startGameButton;

	private static BufferedWriter output;
	
	private String hostName;
	/**
	 * Initialize label text and sets the button to start game as not disabled. 
	 */
	public void initialize() {
		startGameButton.setDisable(false);
		setLabelText();
		playerOne.setText("");
		playerTwo.setText("");
		playerThree.setText("");
		playerFour.setText("");
	}
	/**
	 * Sets the host player on playerOne label and join game chat.
	 * @param hostName the name of the game host
	 */
	public void setHostPlayer(String hostName) {
		playerOne.setText(hostName.substring(4));
		
		String tmp = Constants.GAMECHAT + hostName.substring(4, hostName.length());
		Main.cHandler.addNewChat(tmp);
	}
	/**
	 * Sets names to the correct player label.
	 * @param name the name of a player that is joining
	 */
	public void joinedPlayer(String name) {
		if (playerTwo.getText() == "")
			playerTwo.setText(name);
		else if (playerThree.getText() == "")
			playerThree.setText(name);
		else if (playerFour.getText() == "")
			playerFour.setText(name);
	}
	/**
	 * Internationalization of the playerLabel, hostLabel and starGameButton.
	 */
	public void setLabelText() {
		playerLabel.setText(Main.messages.getString("INVITEDPLAYERS"));
		hostLabel.setText(Main.messages.getString("HOST"));
		startGameButton.setText(Main.messages.getString("STARTGAME"));
	}
	/**
	 * Starts the game for all players when the start game button is pressed. 
	 * @throws IOException Throws any I/O exceptions.
	 */
	@FXML private void startGameButtonPressed() throws IOException {	
		try {
			sendText(Constants.GAMESTART);
		} catch (Exception ioe) {
			Main.LOGGER.log(Level.WARNING, "Unable to send message to server", ioe);
		}
	}
	/**
	 * Sets the correct output so that the host can communicate with the game server.
	 * @param write the bufferedWriter of the gameserver. Connection to the server.
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
        	Main.LOGGER.log(Level.WARNING, "Unable to send message to server", ioe);
        }
    }

}