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
					GlobalServer.LOGGER.log(Level.SEVERE, "Thread interrupted", e);
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
				msg = msg.substring(GlobalServer.groupChatList.get(i).getName().length() + 1);
				
				try {
					for (int j = 0; j < GlobalServer.players.size(); j++) {
					if (GlobalServer.groupChatList.get(i).playerExists(GlobalServer.players.get(j).getName()))
						GlobalServer.players.get(j).sendText(Constants.CHATMESSAGE + GlobalServer.groupChatList.get(i).getName() + ":" + p.getName() + " > " + msg);
					}
				} catch (Exception e) {
					GlobalServer.LOGGER.log(Level.SEVERE, "Thread interrupted", e);
				}
			//Writes to file
			GlobalServer.fileName = GlobalServer.groupChatList.get(i).getName() + "_" + GlobalServer.fileNameEnd;
			GlobalServer.writeToFile(GlobalServer.fileName, GlobalServer.groupChatList.get(i).getName() + ":" + msg);
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
		if(GlobalServer.groupChatList.contains(newChat) 
				&& GlobalServer.groupChatList.contains(new Chat(Constants.IDGK + p.getName())))
			try {
				p.sendText(Constants.ERRORCHAT);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		else {
			GlobalServer.groupChatList.add(newChat);
			try {
				GlobalServer.messages.put(Constants.CHATMESSAGE + msg);
			} catch (InterruptedException e) {
				GlobalServer.LOGGER.log(Level.SEVERE, "Thread interrupted", e);
			}
			GlobalServer.GUI.displayMessage("New chat room: " + msg.substring(13) + " made by: " + p.getName() + "\n");
		}

	}
	static void playerLeaveChat(Player p, String msg) {
		for (int i = 0; i < GlobalServer.groupChatList.size(); i++)
			if (msg.equals(GlobalServer.groupChatList.get(i).getName())) {
				GlobalServer.groupChatList.get(i).removePlayer(p.getName());
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
