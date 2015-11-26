package no.hig.hers.ludoclient;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import no.hig.hers.ludoshared.Constants;

public class PlayerGameLobbyController {

	@FXML private Label playerOne;
	@FXML private Label playerTwo;
	@FXML private Label playerThree;
	@FXML private Label playerFour;
	@FXML private Label playerLabel;
	@FXML private Label hostLabel;
	
	
	String host;
	
	public void initialize() {
		setLabelText();
		playerOne.setText("");
		playerTwo.setText("");
		playerThree.setText("");
		playerFour.setText("");
	}
	
	public void setHostPlayer(String hostName) {
		playerOne.setText(hostName.substring(4));
		host = hostName;
		joinGameChat();
	}
	
	public void setLabelText() {
		playerLabel.setText(Main.messages.getString("INVITEDPLAYERS"));
		hostLabel.setText(Main.messages.getString("HOST"));
	}
	
	public void joinedPlayer(String name) {
		if (playerTwo.getText() == "") {
			playerTwo.setText(name);
		}
		else if (playerThree.getText() == "")
			playerThree.setText(name);
		else if (playerFour.getText() == "")
			playerFour.setText(name);
	}
	
	public void joinGameChat() {
		String tmp;
		tmp = ("Gamechat-" + host.substring(4, host.length()));
		Main.sendText(tmp + Constants.JOIN + Main.userName);
		Main.cHandler.addNewChat(tmp);
	}
	
}


