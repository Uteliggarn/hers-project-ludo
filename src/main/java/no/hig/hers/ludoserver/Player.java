package no.hig.hers.ludoserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.Formatter;
import java.util.Iterator;
import java.util.logging.Level;

import no.hig.hers.ludoshared.Constants;

public class Player {
	
	private Socket connection;
	
	private BufferedReader input;
	private BufferedWriter output;
	
	private String name;
	private int serverPort;
	private int playerID;

	private String IPaddress;


	public Player(Socket connection) throws IOException {
		this.connection = connection;
		this.IPaddress = connection.getRemoteSocketAddress().toString();
		
		int h = IPaddress.indexOf(":");
		IPaddress = IPaddress.substring(1, h);
		
		
		input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));	
	}
	
	/**
	 * Closes the buffered reader, the buffered writer and the socket connection
	 * @throws IOException if one can't be closed
	 */
	public void close() throws IOException {
		input.close();
		output.close();
		connection.close();
	}
	
	/**
	 * Send the given message to the client. Ensures that all messages
	 * have a trailing newline and are flushed.
	 * @param text the message to send
	 * @throws IOException if an error occurs when sending the message
	 */
	public void sendText(String text) throws IOException {
		output.write(text);
		output.newLine();
		output.flush();
	}
	
	public void sendPort(int port) throws IOException {
		output.flush();
		output.write(port);
		output.newLine();
		output.flush();
	}
	
	public String read() throws IOException {
		if (input.ready())
			return input.readLine();
		return null;
	}
	
	/**
	 * Returns the name of the Player
	 * @return player name
	 */
	public String getName() {
		return name;
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public int getPlayerID() {
		return playerID;
	}
	
	public String returnIPaddress() {
		return IPaddress;
	}
	
	/**
	 * The function read from the input two messages. Then it goes threw several if, else if's
	 * to check what the message contains. If the message contains the correct keyword
	 * the message data will then be checked against the database. If the database
	 * accept the info. A message go ahead or you can't message will be sent back to
	 * the one trying to log in. 
	 *
	 * @return true or false
	 */
	public boolean loginChecker(int serverPort) {
		try {
			String code = input.readLine();
			String tempName = input.readLine();
			String tempPass = input.readLine();

			if (code.equals(Constants.SENDLOGIN)) {
				int ID = DatabaseHandler.userLogin(tempName, tempPass);

				if (ID == 0) {
					sendText(Integer.toString(ID));
					return false;
				}
				this.name = tempName;
				this.playerID = ID;
				this.serverPort = serverPort;
				
				sendText(Integer.toString(this.playerID));				// Sends the Player ID
				sendText(Integer.toString(this.serverPort));	// Sends the given serverport
				
				sendChatList();
				
				return true;
			} else if (code.equals(Constants.SENDREGISTER)) {
				boolean register = DatabaseHandler.registerNewUser(tempName, tempPass);
				
				if (register) sendText(Constants.ACCEPTED);
				else sendText(Constants.DECLINED);
			}
			
		} catch (IOException ioe) { // catches any errors when trying to read from input
			GlobalServer.LOGGER.log(Level.SEVERE, "Could not receive input", ioe);
		}
		return false;
	}
	

	private void sendChatList() {
		Iterator<Chat> i = GlobalServerMain.application.groupChatList.iterator();
		i.next(); 		// Skip Global chat
		while (i.hasNext()) {
			String chatName = i.next().getName();
			try {
				sendText(Constants.CHATMESSAGE + Constants.NEWCHAT + chatName);
			} catch (IOException e) {
				GlobalServer.LOGGER.log(Level.INFO, "Couldn't send chatlist", e);
			}
		}
	}
	
	public void sendPlayerList(Chat chat) {
		Iterator<String> y = chat.getPlayerList().iterator();
		while (y.hasNext()) {
			String playerName = y.next();
			if (!playerName.equals(this.name)) {
				try {
					sendText(Constants.CHATMESSAGE + Constants.JOIN + chat.getName() + ":" + playerName);
				} catch (IOException e) {
					GlobalServer.LOGGER.log(Level.INFO, "Couldn't send playerlist", e);
				}
			}
		}	
		
	}
	public void sendTopTenLists() {
		try {
			String toptenPlayedName = null;
			int toptenPlayedCount;
			String toptenWonName = null;
			String toptenWonCount;
			ResultSet resultSetPlayed = DatabaseHandler.retrieveTopTen(DatabaseHandler.MATCHESPLAYED);
			ResultSet resultSetWon = DatabaseHandler.retrieveTopTen(DatabaseHandler.MATCHESWON);
			
			while (resultSetPlayed.next()) {			
				toptenPlayedName =  (String) resultSetPlayed.getObject(1);
				toptenPlayedCount = (int) resultSetPlayed.getObject(2);
				toptenPlayedName = ( toptenPlayedName + "," + Integer.toString(toptenPlayedCount));
				sendText(Constants.TOPPLAYED + toptenPlayedName);	
			}
			while(resultSetWon.next()) {
				String tmp;
				toptenWonName = (String) resultSetWon.getObject(1);
				toptenWonCount = Integer.toString((int)resultSetWon.getObject(2));
				tmp = (toptenWonName + "," + toptenWonCount);
				sendText(Constants.TOPWON + tmp);
			}
		}
		catch (Exception e) {
			GlobalServer.LOGGER.log(Level.SEVERE, "Exception", e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	Iterator<Player> i = GlobalServerMain.application.player.iterator();
	while (i.hasNext()) {
		Player t = i.next();
		try {
			sendText(Constants.GLOBALCHAT + t.returnName());
			t.sendText(Constants.GLOBALCHAT + returnName());
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	
	Iterator<Player> i = player.iterator();
    while (i.hasNext()) {		// Send message to all clients that a new person has joined
    	Player t = i.next();
    	p.sendText(Constants.JOIN + t.returnName());
    	t.sendText(Constants.JOIN + p.returnName());
    } 
	*/
	
	
}
