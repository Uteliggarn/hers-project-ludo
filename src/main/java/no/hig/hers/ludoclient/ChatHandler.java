package no.hig.hers.ludoclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class ChatHandler {
	private BufferedWriter output;
	private BufferedReader input;
	
	private ExecutorService executorServer;
	
	private String chatGroupName;
	private String clientUsername;
	private Tab chatTab;
	private TextArea chatText;
	
	public ChatHandler(Tab tab, BufferedWriter output, BufferedReader input, String username) {
		this.output = output;
		this.input = input;
		this.chatTab = tab;
		
		chatText = ((TextArea) ((BorderPane)tab.getContent().getParent()).getChildren().get(0));
		
		/**
		Tab globalTab = new Tab("Global");
		globalTab.setContent((Node)FXMLLoader.load(getClass().getResource("ClientChatOverlay.fxml")) );
		chatTabs.getTabs().add(globalTab);
		*/
	}
	
	

}
