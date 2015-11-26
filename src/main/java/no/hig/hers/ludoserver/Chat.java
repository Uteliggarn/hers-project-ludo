package no.hig.hers.ludoserver;

import java.util.ArrayList;

public class Chat {
	private String name;
	private ArrayList<String> playerList;
	
	public Chat(String name) {
		this.name = name;
		playerList = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public ArrayList<String> getPlayerList() {
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
	
}