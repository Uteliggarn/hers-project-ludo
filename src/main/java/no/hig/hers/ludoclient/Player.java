package no.hig.hers.ludoclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * @author on 27.11.2015
 * Player object is created when a new connection to server is accepted.
 * player Object handles all the personal data of the player and
 * the connection too the player on the client side.
 */
public class Player {
	
	private Socket connection;
	
	private BufferedReader input;
	private BufferedWriter output;
	
	private String name;
	private boolean host;
	private int playerNr;

	/**
	 * Constructor that sets the socket connection and input and output
	 * for the player. And sets the player to be host or not
	 * @param connection of the socket
	 * @param nr of the player in the game
	 * @throws IOException if constructor can't be ran.
	 */
	public Player(Socket connection, int nr) throws IOException {
		this.connection = connection;
		playerNr = nr;
		input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));	
		
		String msg = input.readLine();
		
		if (msg.startsWith("1"))
			host = true;
		else if (msg.startsWith("2"))
			host = false;
		name = msg.substring(1);
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
	 * Sets the name of the player
	 * @param name of the player 
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the playerNr
	 * @return playerNr
	 */
	public int getPlayerNr() {
		return playerNr;
	}
	
	/**
	 * Checks if the player is hosting
	 * @return true if player is host, false otherwise
	 */
	public boolean getHost() {
		return  host;
	}
	
}