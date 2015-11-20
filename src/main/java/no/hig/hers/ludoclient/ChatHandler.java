package no.hig.hers.ludoclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
/**
 * Class for handling chats.
 * This does everything from adding new chat tabs, to handling messages
 * @author Daniel Rosland on 13.11.2015
 */

public class ChatHandler {
	private List<Tab> chats;
	private TabPane chatTabs;
	private TabPane gameTabs;
	private List<ClientChatOverlayController> controllers;
	
	public ChatHandler(TabPane chatTabs, TabPane gameTabs) {
		this.chatTabs = chatTabs;
		this.gameTabs = gameTabs;
		
		chats = new ArrayList<>();
		controllers = new ArrayList<ClientChatOverlayController>();

		addNewChat("Global");
	}
	
	/**
	 * Adds a new chat.
	 * @param name The name/id of the tab/chat
	 */
	public void addNewChat(String name) {
		Platform.runLater(new Runnable() {
    		@Override
    		public void run() {
    			Tab newTab = new Tab(name);
    			newTab.setId(name);
    			FXMLLoader loader = new FXMLLoader();
    			try {
    				newTab.setContent(loader.load(getClass().getResource("ClientChatOverlay.fxml").openStream()));
    				ClientChatOverlayController c = (ClientChatOverlayController) loader.getController();
    				c.setID(name);
    				controllers.add(c);
    				chats.add(newTab);
    				chatTabs.getTabs().add(newTab);
    			} catch (IOException e) {
    				Main.showAlert("Error", "Couldn't find FXML file");
    				e.printStackTrace();
    			}	
    		}
    	});
	}
	
	private void newHostGameLobby() {
		try {
			Tab tab = new Tab("Ludo");
			
			FXMLLoader loader = new FXMLLoader();
			
			tab.setContent(loader.load(getClass().getResource("HostGameLobby.fxml").openStream()));
			
			//HostGameLobbyController hostGameLobbyController = (HostGameLobbyController) loader.getController();
			
			//hostGameLobbyController.getServerPort(Main.serverPort);
			
			tab.setId("tab1");
			
			gameTabs.getTabs().add(tab);
			gameTabs.getSelectionModel().select(tab);
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	/**
	 * Handles incoming chat-messages.
	 * @param message Message to handle
	 */
	public void handleChatMessage(String message) {
		for (int i = 0; i < chats.size(); i++) { // Looper igjen alle groupChatene som finnes i listen
        	Tab tab = chats.get(i);
        	ClientChatOverlayController c = controllers.get(i);
        	
            if (message.startsWith(chats.get(i).getId() + Main.JOINCHAT)){	// Sjekker om noen har lyst å joine		                	
            	String username = message.substring(tab.getId().length() + 5);
            	c.addUserToList(username);
            //	Main.sendText(tab.getId() + Main.JOINCHAT + username); // Sender klient som lyst å joine til chaten
            }
            else if (message.startsWith(chats.get(i).getId()+ Main.LEAVECHAT)) { // Mottar melding om at noen har logget ut
            	String username = message.substring(tab.getId().length() + 4);
            	c.removeUserFromList(username);
            	
            } 
            else if (message.startsWith(chats.get(i).getId() + ":")) { // Tar alle andre meldinger
            	c.receiveChatMessage(message.substring(tab.getId().length() + 1));
            }
        }
	}
}
