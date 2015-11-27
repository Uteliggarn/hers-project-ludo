package no.hig.hers.ludoserver;

import java.io.IOException;
import java.util.logging.Level;

import no.hig.hers.ludoshared.Constants;

/**
 * Class for handling chat messages, serverside.
 * @author daniel on 26.11.2015
 */

public class ChatHandler {
	
	/**
	 * Private constructor to hide the implicit public one
	 */
	private ChatHandler() {
	}

	/**
	 * Method handles a player joining a chat,
	 * first finding the Chat, then sending a message to all players in that chat
	 * then adding the player to the playerlist of that chat,
	 * finally sending the player the list of players in that chat.
	 * 
	 * @param p The player joining the chat
	 * @param msg The chatname
	 */
	static void playerJoinChat(Player p, String msg) {
		for (int i = 0; i < GlobalServer.groupChatList.size(); i++) {
			if (msg.equals(GlobalServer.groupChatList.get(i).getName())) {
				GlobalServer.groupChatList.get(i).addPlayer(p.getName());
				p.sendPlayerList(GlobalServer.groupChatList.get(i));
				
				try {
					for (int j = 0; j < GlobalServer.players.size(); j++) {
						if (GlobalServer.groupChatList.get(i).playerExists(GlobalServer.players.get(j).getName()))
							GlobalServer.players.get(j).sendText(
									Constants.CHATMESSAGE + Constants.JOIN + msg + ":" + p.getName());
					}
				} catch (Exception e) {
					GlobalServer.LOGGER.log(Level.INFO, "Could not send chat message to players", e);
				}
				
				//Writes to file
				GlobalServer.fileName = msg + "_" + GlobalServer.fileNameEnd;
				GlobalServer.writeToFile(GlobalServer.fileName, msg);

			}
		}
	}
	/**
	 * Method handling chat messages.
	 * Sends the chat message to all players that's in the Chat.
	 * 
	 * @param p The player that sent the message.
	 * @param msg The message to send.
	 */
	static void sendChatMessage(Player p, String msg) {
		for (int i = 0; i < GlobalServer.groupChatList.size(); i++) {
			if (msg.startsWith(GlobalServer.groupChatList.get(i).getName() + ":")) {
				GlobalServer.GUI.displayMessage(msg + "\n");
				String message = msg.substring(GlobalServer.groupChatList.get(i).getName().length() + 1);
				
				try {
					for (int j = 0; j < GlobalServer.players.size(); j++) {
						if (GlobalServer.groupChatList.get(i).playerExists(GlobalServer.players.get(j).getName()))
							GlobalServer.players.get(j).sendText(Constants.CHATMESSAGE + 
									GlobalServer.groupChatList.get(i).getName() + ":" + p.getName() + " > " + message);
					}
				} catch (Exception e) {
					GlobalServer.LOGGER.log(Level.SEVERE, "Thread interrupted", e);
				}
			//Writes to file
			GlobalServer.fileName = GlobalServer.groupChatList.get(i).getName() + "_" + GlobalServer.fileNameEnd;
			GlobalServer.writeToFile(GlobalServer.fileName, GlobalServer.groupChatList.get(i).getName() + ":" + p.getName() + " > " + message);
			}	
		}
	}
	
	/**
	 * Method for handling the creation of a new chat.
	 * If the chat already exists, send ERRORCHAT to the 
	 * user that created the chat.
	 * If not, add the chat to the list, and send the new chat to all players.
	 * 
	 * @param p The Player that created the chat.
	 * @param msg Constants.NEWCHAT followed by the new chatname.
	 */
	static void createNewChat(Player p, String msg) {
		boolean exists = false;
		int i = 0;
		
		while (!exists && i < GlobalServer.groupChatList.size()) {
			if(GlobalServer.groupChatList.get(i).getName().equals(msg.substring(Constants.NEWCHAT.length())))
				exists = true;
			i++;
		}
		
		if (exists)
			try {
				p.sendText(Constants.ERRORCHAT);
			} catch (IOException ioe) {
				GlobalServer.LOGGER.log(Level.INFO, "Couldn't send message to player", ioe);
			}
		else {
			Chat newChat = new Chat(msg.substring(Constants.NEWCHAT.length()));
			GlobalServer.groupChatList.add(newChat);
			try {
				GlobalServer.messages.put(Constants.CHATMESSAGE + msg);
			} catch (InterruptedException e) {
				GlobalServer.LOGGER.log(Level.SEVERE, "Thread interrupted", e);
			}
			GlobalServer.GUI.displayMessage("New chat room: " + msg.substring(13) + " made by: " + p.getName() + "\n");
		}
	}
	
	/**
	 * Method for handling a player leaving a chat.
	 * 
	 * First finds the chat the player is trying to leave,
	 * then removing the player from it.
	 * 
	 * If the chat then is empty, sends a message to all players
	 * to remove the chat from their chat list, then removing it from the servers list.
	 * 
	 * Lastly, sends a message to all remaining players that the player has left.
	 * @param p The leaving player
	 * @param msg The chat the player is leaving
	 */
	static void playerLeaveChat(Player p, String msg) {
		for (int i = 0; i < GlobalServer.groupChatList.size(); i++)
			if (msg.equals(GlobalServer.groupChatList.get(i).getName())) {
				GlobalServer.groupChatList.get(i).removePlayer(p.getName());
				if (GlobalServer.groupChatList.get(i).noPlayers()) {
					try {
						GlobalServer.messages.put(Constants.CHATMESSAGE + Constants.REMOVECHAT + 
								GlobalServer.groupChatList.get(i).getName());
					} catch (InterruptedException e) {
						GlobalServer.LOGGER.log(Level.INFO, "Could not send message to players", e);
					}
					GlobalServer.groupChatList.remove(i);
				} else {
					try {
						for (int j = 0; j < GlobalServer.players.size(); j++) {
						if (GlobalServer.groupChatList.get(i).playerExists(GlobalServer.players.get(j).getName()))
							GlobalServer.players.get(j).sendText(
									Constants.CHATMESSAGE + Constants.LEAVECHAT + 
									GlobalServer.groupChatList.get(i).getName() + ":" + p.getName());
						}
					} catch (Exception e) {
						GlobalServer.LOGGER.log(Level.SEVERE, "Thread interrupted", e);
					}
				}
			}
	}


}
