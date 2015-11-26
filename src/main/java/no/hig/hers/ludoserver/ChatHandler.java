package no.hig.hers.ludoserver;

import java.io.IOException;
import java.util.logging.Level;

import no.hig.hers.ludoshared.Constants;

/**
 * Class for handling chat messages,
 * serverside.
 * @author daniel on 26.11.2015
 *
 */
public class ChatHandler {

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
		for (int i = 0; i < groupChatList.size(); i++) {
			if (msg.equals(groupChatList.get(i).getName())) {
				try {
					for (int j = 0; j < players.size(); j++) {
						if (groupChatList.get(i).playerExists(players.get(j).getName()))
								players.get(j).sendText(Constants.CHATMESSAGE + Constants.JOIN + msg + ":" + p.getName());
					}
				} catch (Exception e) {
					GlobalServer.LOGGER.log(Level.SEVERE, "Thread interrupted", e);
				}
				groupChatList.get(i).addPlayer(p.getName());
				p.sendPlayerList(groupChatList.get(i));
				
				//Writes to file
				fileName = msg + "_" + fileNameEnd;
				writeToFile(fileName, msg);

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
		for (int i = 0; i < groupChatList.size(); i++) {
			if (msg.startsWith(groupChatList.get(i).getName() + ":")) {
				displayMessage(msg + "\n");
				msg = msg.substring(groupChatList.get(i).getName().length() + 1);
				
				try {
					for (int j = 0; j < players.size(); j++) {
					if (groupChatList.get(i).playerExists(players.get(j).getName()))
						players.get(j).sendText(Constants.CHATMESSAGE + groupChatList.get(i).getName() + ":" + p.getName() + " > " + msg);
					}
				} catch (Exception e) {
					GlobalServer.LOGGER.log(Level.SEVERE, "Thread interrupted", e);
				}
			//Writes to file
			fileName = groupChatList.get(i).getName() + "_" + fileNameEnd;
			writeToFile(fileName, groupChatList.get(i).getName() + ":" + msg);
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
		Chat newChat = new Chat(msg.substring(Constants.NEWCHAT.length()));
		if(GlobalServerMain.application.groupChatList.contains(newChat) 
				&& GlobalServerMain.application.groupChatList.contains(new Chat(Constants.IDGK + p.getName())))
			try {
				p.sendText(Constants.ERRORCHAT);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		else {
			GlobalServerMain.application.groupChatList.add(newChat);
			try {
				GlobalServerMain.application.messages.put(Constants.CHATMESSAGE + msg);
			} catch (InterruptedException e) {
				GlobalServer.LOGGER.log(Level.SEVERE, "Thread interrupted", e);
			}
			GlobalServerMain.application.displayMessage("New chat room: " + msg.substring(13) + " made by: " + p.getName() + "\n");
		}

	}


}
