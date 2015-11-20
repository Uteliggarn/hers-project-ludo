package no.hig.hers.ludoclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class ChatHandler {
	private ExecutorService executorService;
	private List<Tab> chats;
	private TabPane chatTabs;
	private TabPane gameTabs;
	String message;
	
	//private ClientMainUIController clientMainUIController;
	
	public ChatHandler(TabPane chatTabs, TabPane gameTabs) {
		chats = new ArrayList<>();
		this.chatTabs = chatTabs;
		this.gameTabs = gameTabs;
		//this.clientMainUIController = clientMainUIController;
		
		addNewChat("Global");
		addNewChat("Glotest");
				
		executorService = Executors.newCachedThreadPool(); // Lager et pool av threads for bruk
		processConnection(); // Starter en ny evighets tr�d som tar seg av meldinger fra server
		executorService.shutdown();	// Dreper tr�den n�r klassen d�r
	}
	
	/**
	 * Adds a new chat.
	 * @param name The name/id of the tab/chat
	 */
	public void addNewChat(String name) {
		Tab newTab = new Tab(name);
		newTab.setId(name);
		FXMLLoader loader = new FXMLLoader();
		try {
			newTab.setContent(loader.load(getClass().getResource("ClientChatOverlay.fxml").openStream()));
			ClientChatOverlayController c = (ClientChatOverlayController) loader.getController();
			c.setID(name);
			chats.add(newTab);
			chatTabs.getTabs().add(newTab);
		} catch (IOException e) {
			Main.showAlert("Error", "Couldn't find FXML file");
			e.printStackTrace();
		}	
	}
	
	private void newHostGameLobby() {
		try {
			Tab tab = new Tab("Ludo");
			
			FXMLLoader loader = new FXMLLoader();
			
			tab.setContent(loader.load(getClass().getResource("HostGameLobby.fxml").openStream()));
			
			HostGameLobbyController hostGameLobbyController = (HostGameLobbyController) loader.getController();
			
			hostGameLobbyController.getServerPort(Main.serverPort);
			
			tab.setId("tab1");
			
			chatTabs.getTabs().add(tab);
			chatTabs.getSelectionModel().select(tab);
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Kopiert mer eller mindre fra den vi hadde p� forrige prosjekt.
	 * Ser ikke ut til � fungere.
	 */
	private void processConnection() {
		executorService.execute(() -> {
			while (true) {
				try {
	                message = Main.input.readLine();
	                              
	                if (message.startsWith("NEWGROUPCHAT:")) { //Legger til ny chatTab
	                	addNewChat(message.substring(13));
	                	
	                //	sendText(message.substring(13) + "JOIN:" + clientUserName); // Sender ut at brukern ogs� vil joine chaten.
	                }
	                else if (message.equals("ERRORCHAT")) {	// Forteller at chaten finnes allerede
	                	Main.showAlert("Chat-room already exists", "Chat-room already exits");
	                }
	                else if (message.equals("HOST")) {
	                	newHostGameLobby();
	                }
	                else if (message.equals("JOIN")) {
	                	int port = Main.input.read();
	                	GameLobby gameLobby = new GameLobby(port);
	                	
	                }
	                      /*     
	                for (int i = 1; i < chats.size(); i++) { // Looper igjen alle groupChatene som finnes i listen
	                	FXMLLoader loader = new FXMLLoader();
	                	Tab tab = chats.get(i);
	                			// Denne f�r en error
	                	tab.setContent(loader.load(getClass().getResource("ClientChatOverlay.fxml").openStream()));
	                	ClientChatOverlayController c = (ClientChatOverlayController) loader.getController();
	                	
		                if (message.startsWith(chats.get(i).getId() + "JOIN:")){	// Sjekker om noen har lyst � joine		                	
		                	String username = message.substring(tab.getId().length() + 5);
		                	c.addUserToList(username);
		                	//Main.sendText(tab.getId() + "JOIN:" + username); // Sender klient som lyst � joine til chaten
		                }
		                else if (message.startsWith(chats.get(i).tab.getId()+ "OUT:")) { // Mottar melding om at noen har logget ut
		                	String username = message.substring(tab.getId().length() + 4);
		                	c.removeUserFromList(username);
		                } 
		                else if (message.startsWith(chats.get(i).tab.getId() + ":")) { // Tar alle andre meldinger
		                	c.receiveChatMessage(message.substring(tab.getId().length() + 1));
		                }
		                }*/
	                
	            } catch (Exception e) {
	             //   Main.showAlert("Error", "Error receiving message from server");
	            }
				
				try {
					Thread.sleep(250);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	 

}
