package no.hig.hers.ludoclient;

import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import no.hig.hers.ludoserver.Player;

public class MainWindowController {

	@FXML private Tab mainTab;
	@FXML private Button newGameButton;
	@FXML private TabPane tabPane;
	
	public static int sock = 10010;
	
	private ArrayList<GameServer> gameServerList = new ArrayList<GameServer>();
	
	public void initialize() {
		
		//Tab t = tabPane.getTabs().get(1);
	}

	@FXML private void newGameButtonPressed(ActionEvent e) {
		try {
			Tab tmp = new Tab("newTab");
			
			
			
			FXMLLoader loader = new FXMLLoader();
			
			tmp.setContent(loader.load(getClass().getResource("GameLobbyWindow.fxml").openStream()));
			
			GameLobbyWindowController gameLobbyWindowController = (GameLobbyWindowController) loader.getController();
			
			//gameLobbyWindowController.test("Hello");
			
			//tmp.setContent(FXMLLoader.load(getClass().getResource("GameLobbyWindow.fxml")));
			
			tabPane.getTabs().add(tmp);
			tabPane.getSelectionModel().select(tmp);;
			
			//String title = tabPane.getTabs().get(1).getText();
			
			
			
			sock += 1;
			
			GameServer gameServer = new GameServer("test", sock);
			
			System.out.println("Hva er socket verdien: " + sock);
			
			gameServerList.add(gameServer);
			
			tabPane.getTabs().get(1).setText(gameServer.returnName());
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
}
