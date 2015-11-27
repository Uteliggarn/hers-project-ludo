package no.hig.hers.ludoserver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author on 27.11.2015 
 * Chat class for handling adding and removing players 
 * in the playerList for one chat
 */
public class Chat {
	private String name;
	private List<String> playerList;
	
	/**
	 * Sets the name of the chat and makes a new playerList
	 * for this chat
	 * @param name of the chat tab
	 */
	public Chat(String name) {
		this.name = name;
		playerList = new ArrayList<>();
	}

	/**
	 * Returns the name of the chat
	 * @return the name of the chat 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the chats playerList
	 * @return playerList for the chat
	 */
	public List<String> getPlayerList() {
		return playerList;
	}
	
	/**
	 * Removes a player from the playerList
	 * @param name of the player to be removed
	 */
	public void removePlayer(String name) {
		if (playerList.contains(name))
			playerList.remove(name);
	}
	
	/**
	 * Adds a new player to the playerList
	 * @param name of the player to be added
	 */
	public void addPlayer(String name) {
		if (!playerList.contains(name)) 
			playerList.add(name);
	}
	
	/**
	 * checks if the player exist in the playerList
	 * @param name checked if it exist
	 * @return true if the name exists otherwise false
	 */
	public boolean playerExists(String name) {
		if (playerList.contains(name))
			return true;
		return false;
	}
	
	/**
	 * Checks in there are any players in the playerList
	 * @return true if playerList is empty
	 */
	public boolean noPlayers() {
		return playerList.isEmpty();
	}
	
}
