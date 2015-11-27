package no.hig.hers.ludoserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.logging.Level;

import no.hig.hers.ludoshared.Constants;

/**
 * Player objects that are created when a new server connection
 * is accpeted. Also handles the check if the one trying to connect
 * with a username and passord exits in the database or wants too 
 * create a new user in the database
 * 
 * Author Petter on 03.11.2015 
 */
public class Player {
	
	private Socket connection;
	
	private BufferedReader input;
	private BufferedWriter output;
	
	private String name;
	private int serverPort;
	private int playerID;

	private String IPaddress;


	/**
	 * Sets the connection and input and output from the server socket
	 * @param connection socket
	 * @throws IOException if connection didn't work
	 */
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
	
	/**
	 * Sends the port number of the given player
	 * @param port server port
	 * @throws IOException if output failed to send
	 */
	public void sendPort(int port) throws IOException {
		output.flush();
		output.write(port);
		output.newLine();
		output.flush();
	}
	
	/**
     * Non blocking read from the client, if no data is available then null 
     * will be returned. Checks to see if a line can be read from the client
     * and if so reads and returns that line (message). If no message is 
     * available null is returned.
     * 
     * @return a String with message if available, otherwise null
     * @throws IOException if an error occurs during reading
     */
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
	
	/**
	 * returns the server port of the player
	 * @return serverPort of the player
	 */
	public int getServerPort() {
		return serverPort;
	}
	
	/**
	 * returns the player id
	 * @return playerID of the player
	 */
	public int getPlayerID() {
		return playerID;
	}
	
	/**
	 * return the IP address of the player
	 * @return IPaddress of the player
	 */
	public String getIPaddress() {
		return IPaddress;
	}
	
	/**
	 * The function read from the input two messages. Then it goes threw several if, else if's
	 * to check what the message contains. If the message contains the correct keyword
	 * the message data will then be checked against the database. If the database
	 * accept the info. A message go ahead or you can't message will be sent back to
	 * the one trying to log in. 
	 *
	 * @param serverPort The port that the new player uses
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
	
	/**
	 * Method for sending a player the list of chats.
	 * This is run when the player first logs in.
	 */
	private void sendChatList() {
		Iterator<Chat> i = GlobalServer.groupChatList.iterator();
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
	/**
	 * Method for sending a player the playerlist
	 * for a specific chat.
	 * @param chat The chat to get the playerlist from.
	 */
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
	
	/**
	 * Method for sending a player the top ten
	 * played and won lists.
	 * First retrieves it from the server,
	 * then sends the lists one by one line.
	 */
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
			GlobalServer.LOGGER.log(Level.INFO, "Could not send top ten list to player", e);
		}
	}

	/**
	 * Method for sending a player his win and played scores.
	 * First retrieves it from the DatabaseHandler,
	 * then sends it.
	 */
	public void sendPlayerScores() {
		int won = DatabaseHandler.retrievePlayersMatches(playerID, DatabaseHandler.MATCHESWON);
		int played = DatabaseHandler.retrievePlayersMatches(playerID, DatabaseHandler.MATCHESPLAYED);
		
		try {
			sendText(Constants.PLAYERSCORES + Integer.toString(won));
			sendText(Constants.PLAYERSCORES + Integer.toString(played));
		} catch (IOException e) {
			GlobalServer.LOGGER.log(Level.INFO, "Could not send playerscores to player", e);
		}
		
	}
}
