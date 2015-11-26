package no.hig.hers.ludoclient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;

import javafx.event.ActionEvent;
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
	
	private int serverPort;
	private static BufferedWriter output;
	
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
		this.hostName = hostName;
		
		String tmp = (Constants.GAMECHAT + hostName.substring(4, hostName.length()));
		Main.cHandler.addNewChat(tmp);
	}
	
	public void joinedPlayer(String name) {
		if (playerTwo.getText() == "")
			playerTwo.setText(name);
		else if (playerThree.getText() == "")
			playerThree.setText(name);
		else if (playerFour.getText() == "")
			playerFour.setText(name);
	}
	
	public void setLabelText() {
		playerLabel.setText(Main.messages.getString("INVITEDPLAYERS"));
		hostLabel.setText(Main.messages.getString("HOST"));
		startGameButton.setText(Main.messages.getString("STARTGAME"));
	}
	@FXML private void startGameButtonPressed(ActionEvent e) throws IOException {	
		try {
			sendText(Constants.GAMESTART);
		} catch (Exception ioe) {
			Main.LOGGER.log(Level.WARNING, "Unable to send message to server", ioe);
		}
	}
	
	public void getServerPort(int serverPort) {
		this.serverPort = serverPort;
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