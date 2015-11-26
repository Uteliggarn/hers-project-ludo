package no.hig.hers.ludoclient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import no.hig.hers.ludoshared.Constants;

public class CreateGameLobbyController {
	
	@FXML private Label playerOne;
	@FXML private Label playerTwo;
	@FXML private Label playerThree;
	@FXML private Label playerFour;
	
	@FXML private ListView<String> playerList;
	
	@FXML private Button startGameButton;
	@FXML private Button updateButton;

	private static BufferedWriter output;
	
	private String hostName;
	
	public void initialize() {
		startGameButton.setDisable(false);
		updateButton.setText("Update");
		playerOne.setText("");
		playerTwo.setText("");
		playerThree.setText("");
		playerFour.setText("");
		
		for (int i=0; i<Main.playerList.size(); i++) {
			if (!Main.playerList.get(i).equals(Main.userName))
				playerList.getItems().add(Main.playerList.get(i));
		}
	}
	
	@FXML private void updateButtonPressed() {
		playerList.getSelectionModel().clearSelection();
		playerList.getItems().clear();
		for (int i=0; i<Main.playerList.size(); i++) {
			if (!Main.playerList.get(i).equals(Main.userName))
				playerList.getItems().add(Main.playerList.get(i));
		}
	}
	
	public void addPlayerToList(String name) {
		if (!name.equals(Main.userName))
			playerList.getItems().add(name);
	}
	
	public void removePlayerFromList(String name) {
		playerList.getSelectionModel().clearSelection();
		playerList.getItems().remove(name);
	}
	
	public void setHostPlayer(String hostName) {
		String tmp;
		playerOne.setText(hostName.substring(4));
		this.hostName = hostName;
		tmp = ("Gamechat: " + hostName.substring(4, hostName.length()));
		Main.sendText(Constants.NEWCHAT + tmp);
		//Main.sendText(tmp + Constants.JOINCHAT + Main.userName);
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
	
	
	@FXML private void invitePlayer() {
		String item = playerList.getSelectionModel().getSelectedItem();
		Main.sendText(Constants.INVITE + item);
	}
	
	@FXML private void startGameButtonPressed() {
		try {
			sendText(Constants.GAMESTART);
		} catch (Exception ioe) {
			Main.LOGGER.log(Level.SEVERE, "Error trying to send text to server", ioe);
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
        	Main.LOGGER.log(Level.SEVERE, "Can't send message to server", ioe);
        }
    }
}
