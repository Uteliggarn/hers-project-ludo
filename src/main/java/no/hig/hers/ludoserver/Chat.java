package no.hig.hers.ludoserver;

import java.util.ArrayList;
import java.util.List;

public class Chat {
	private String name;
	private List<String> playerList;
	
	public Chat(String name) {
		this.name = name;
		playerList = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public List<> getPlayerList() {
		return playerList;
	}
	
	public void removePlayer(String name) {
		if (playerList.contains(name))
			playerList.remove(name);
	}
	
	public void addPlayer(String name) {
		if (!playerList.contains(name)) 
			playerList.add(name);
	}
	
	public boolean playerExists(String name) {
		if (playerList.contains(name))
			return true;
		return false;
	}
	
	public boolean noPlayers() {
		return playerList.isEmpty();
	}
	
}
