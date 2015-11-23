package no.hig.hers.ludoclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.Parent;
import javafx.scene.control.Button;

public class HostGameLobbyController {
	
	@FXML private Label playerOne;
	@FXML private Label playerTwo;
	@FXML private Label playerThree;
	@FXML private Label playerFour;
	
	@FXML private Button startGameButton;
	
	private int serverPort;
	private static BufferedReader input;
	private static BufferedWriter output;
	
	public void initialize() {
		startGameButton.setDisable(false);
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
	
	
	@FXML private void startGameButtonPressed(ActionEvent e) throws IOException {
		Tab tab = Main.gameTabs.getTabs().get(1);
		
		//FXMLLoader loader = new FXMLLoader();
		
		try {
			String gamestart;
			gamestart = "gamestart:";
			sendText(gamestart);
			//tab.setContent(loader.load(getClass().getResource("GameClient.fxml").openStream()));
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void getServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public void setConnetion(BufferedWriter write, BufferedReader read) {
		output = write;
		input = read;
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
        	Main.showAlert("Error", "Unable to send message to server");
        }
    }

}