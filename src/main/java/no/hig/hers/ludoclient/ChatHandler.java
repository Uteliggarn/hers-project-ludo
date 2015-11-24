package no.hig.hers.ludoclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.shape.Rectangle;
/**
 * Class for handling chats.
 * This does everything from adding new chat tabs, to handling messages
 * @author Daniel Rosland on 13.11.2015
 */

public class ChatHandler {
	private List<Tab> chats;
	private TabPane chatTabs;
	private List<ClientChatOverlayController> controllers;
	
	public ChatHandler(TabPane chatTabs) {
		this.chatTabs = chatTabs;
	
		chats = new ArrayList<>();
		controllers = new ArrayList<ClientChatOverlayController>();

		addNewChat("Global");
	}
	
	/**
	 * Adds a new chat, if it not already exists.
	 * @param name The name/id of the tab/chat
	 */
	public void addNewChat(String name) {
		boolean exists = false;
		for (int i = 0; i < chats.size(); i++) {
			if (chats.get(i).getId().equals(name)) exists = true; 
		}
		
		if (!exists) {
			Platform.runLater(() -> {
				Tab newTab = new Tab(name);
				newTab.setId(name);
				FXMLLoader loader = new FXMLLoader();
				
				try {
    				newTab.setContent(loader.load(getClass().getResource("ClientChatOverlay.fxml").openStream()));
    				newTab.setOnClosed(new EventHandler<Event>() {
						@Override
						public void handle(Event e) {
							Main.sendText(Main.LEAVECHAT + newTab.getId());
						}
    				});
    				
    				ClientChatOverlayController c = (ClientChatOverlayController) loader.getController();
    				c.setID(name);
    				controllers.add(c);
    				chats.add(newTab);
    				chatTabs.getTabs().add(newTab);
    				if (newTab.getId().equals("Global")) {
    					newTab.setClosable(false);
    				} else Main.sendText(name + Main.JOINCHAT + Main.userName); // Sender ut at brukern også vil joine chaten. 
    				
    			} catch (IOException e) {
    				Main.showAlert("Error", "Couldn't find FXML file");
    				e.printStackTrace();
    			}
			});	
		} else Main.showAlert("Already joined chat", "You are already a member of this chat");
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
            	if (!Main.playerList.contains(username) && message.startsWith("Global"))
					Main.playerList.add(username);
            	c.addUserToList(username);
            }
            else if (message.startsWith(Main.QUITGAME)) { // Mottar melding om at noen har logget ut
            	String username = message.substring(Main.QUITGAME.length());
            	c.removeUserFromList(username);
            } 
            else if (message.startsWith(chats.get(i).getId() + ":")) { // Tar alle andre meldinger
            	c.receiveChatMessage(message.substring(tab.getId().length() + 1));
            }
        }
	}
}
