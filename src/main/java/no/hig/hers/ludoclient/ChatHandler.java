package no.hig.hers.ludoclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import no.hig.hers.ludoshared.Constants;

/**
 * Class for handling chats, clientside
 * This handles the chat tabs, and the chat-related messages.
 * @author Daniel Rosland on 13.11.2015
 */

public class ChatHandler {
	private TabPane chatTabs;
	private List<Tab> chats;
	private List<ClientChatOverlayController> controllers;
	
	/**
	 * Default constructor.
	 * Sets up chats, and adding the global chat.
	 * @param chatTabs The chattabs that the ChatHandler will handle.
	 */
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
			if (chats.get(i).getId().equals(name)) 
				exists = true; 
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
						leaveChat(name);
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
				chatTabs.getTabs().add(newTab);
				
				if ("Global".equals(newTab.getId())) 
					newTab.setClosable(false);
				Main.sendText(Constants.CHATMESSAGE + Constants.JOIN + name); // Sender ut at brukern ogs� vil joine chaten.

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

            if (message.startsWith(Constants.JOIN + tab.getId() + ":")){	// Sjekker om noen har lyst � joine
            	Platform.runLater(() -> {
            	String username = message.substring(Constants.JOIN.length() + tab.getId().length() + 1);
            	Main.playerList.add(username);
            	if (tab.getId().equals("Global"))
            		for (int y=0; y<Main.gameHandler.size(); y++) {		//Finds the gameHandler object that
                		if (Main.gameHandler.get(y).getCaseNr())		//is a createGameLobby
                			Main.gameHandler.get(y).addPlayer(username);	//and removes the the player from the invite list
                	}
            	c.addUserToList(username);
            	});
            }
            else if (message.startsWith(Constants.LEAVECHAT + tab.getId() + ":")) {
            	Platform.runLater(() -> {
                	String username = message.substring(Constants.LEAVECHAT.length() + tab.getId().length() + 1);
                	c.removeUserFromList(username);
                });
            }
            else if (message.startsWith(Constants.LOGOUT)) { // Mottar melding om at noen har logget ut
            	Platform.runLater(() -> {
	            	String username = message.substring(Constants.LOGOUT.length());
	            	for (int y=0; y<Main.gameHandler.size(); y++) { //Finds the gameHandler object that
	            		if (Main.gameHandler.get(y).getCaseNr())	//is a createGameLobby
	            			Main.gameHandler.get(y).removePlayer(username);	//and removes the the player from the invite list
	            	}
	            	Main.playerList.remove(username);
	            	c.removeUserFromList(username);
            	});
            } 
            else if (message.startsWith(chats.get(i).getId() + ":")) { // Tar alle andre meldinger
            	c.receiveChatMessage(message.substring(tab.getId().length() + 1));
            }
            
        }
	}

	/**
	 * Method for leaving the gamechat,
	 * closing the tab and removing it from the list
	 * @param hostName The gamechat to leave.
	 */
	public void leaveGameChat(String hostName) {
		String chatName = Constants.GAMECHAT + hostName.substring(Constants.IDGK.length());
		
		for (int i = 0; i < chats.size(); i++) {
			if (chats.get(i).getId().equals(chatName))
				chats.remove(i);
			if (chatTabs.getTabs().get(i).getId().equals(chatName)) {
				chatTabs.getTabs().remove(i);
				Main.sendText(Constants.CHATMESSAGE + Constants.LEAVECHAT + chatName);
			}
		}
	}
	
	/**
	 * Method for leaving chats, removing it from the list,
	 * and sending the server a message that the player has left the chat
	 * @param name The chat to leave.
	 */
	public void leaveChat(String name) {
		for (int i = 0; i < chats.size(); i++) {
			if (chats.get(i).getId().equals(name)) {
				Main.sendText(Constants.CHATMESSAGE + Constants.LEAVECHAT + name);
				chats.remove(i);
			}
		}
	}
}
