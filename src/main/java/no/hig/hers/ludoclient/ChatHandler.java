package no.hig.hers.ludoclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.shape.Rectangle;
import no.hig.hers.ludoshared.Constants;
/**
 * Class for handling chats.
 * This handles the chat tabs, and the chat-related messages.
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
			Tab newTab = new Tab(name);
			newTab.setId(name);
			FXMLLoader loader = new FXMLLoader();
			
			try {
				newTab.setContent(loader.load(getClass().getResource("ClientChatOverlay.fxml").openStream()));
				newTab.setOnClosed(new EventHandler<Event>() {
					@Override
					public void handle(Event e) {
						Main.sendText(Constants.LEAVECHAT + newTab.getId());
					}
				});
			} catch (IOException e) {
				Main.LOGGER.log(Level.SEVERE, "Couldn't find FXML file", e);
			}

			Platform.runLater(() -> {
    				ClientChatOverlayController c = (ClientChatOverlayController) loader.getController();
    				c.setID(name);
    				controllers.add(c);
    				chats.add(newTab);
    				System.out.println("\nHåper vi er her inne først altså");
    				chatTabs.getTabs().add(newTab);
    				if ("Global".equals(newTab.getId())) {
    					newTab.setClosable(false);
    				} else Main.sendText(name + Constants.JOINCHAT + Main.userName); // Sender ut at brukern også vil joine chaten. 
			});	
		} else Main.showAlert("Already joined chat", "You are already a member of this chat");
	}
	
	/**
	 * Handles incoming chat-messages.
	 * @param message Message to handle
	 */
	public void handleChatMessage(String message) {
		System.out.println("\nHva er chats size: " + chats.size());
		for (int i = 0; i < chats.size(); i++) { // Looper igjen alle groupChatene som finnes i listen
        	Tab tab = chats.get(i);
        	ClientChatOverlayController c = controllers.get(i);
        	
            if (message.startsWith(chats.get(i).getId() + Constants.JOINCHAT)){	// Sjekker om noen har lyst å joine
            	Platform.runLater(() -> {
            	String username = message.substring(tab.getId().length() + 5);
            	System.out.println("\nHva er tab iden: " + tab.getId());
            	if (!Main.playerList.contains(username) && message.startsWith("Global"))
					Main.playerList.add(username);
            	c.addUserToList(username);
            	});
            }
            else if (message.startsWith(Constants.QUITGAME)) { // Mottar melding om at noen har logget ut
            	String username = message.substring(Constants.QUITGAME.length());
            	c.removeUserFromList(username);
            } 
            else if (message.startsWith(chats.get(i).getId() + ":")) { // Tar alle andre meldinger
            	c.receiveChatMessage(message.substring(tab.getId().length() + 1));
            }
        }
	}
}
