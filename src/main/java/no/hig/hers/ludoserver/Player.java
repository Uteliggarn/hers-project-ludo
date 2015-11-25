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
import java.util.Formatter;
import java.util.Iterator;

import no.hig.hers.ludoshared.Constants;

public class Player {
	
	private Socket connection;
	
	private BufferedReader input;
	private BufferedWriter output;
	
	private String name;
	private int serverPort;
	private int playerID;
	
	private final String ACCEPTED = "ACCEPTED";
    private final String DECLINED = "DECLINED";
    private final String SENDLOGIN = "SENDLOGIN:";
    private final String SENDREGISTER = "SENDREGISTER:";

	public Player(Socket connection) throws IOException {
		this.connection = connection;
		
		System.out.println("Hva er Inet adress: " + connection.getRemoteSocketAddress().toString());
		
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
	public String returnName() {
		return name;
	}
	
	public int returnServerPort() {
		return serverPort;
	}
	
	public int returnPlayerID() {
		return playerID;
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
			System.out.println("\nHello everybody!");
			
			String tempName = input.readLine();	//reades the input

			String tempPass = input.readLine();
			
			
			if (!tempName.startsWith(SENDLOGIN) && !tempName.startsWith(SENDREGISTER))
				return false;
			
			if (tempName.startsWith(SENDLOGIN) && tempPass.startsWith(SENDLOGIN)) {
				int login = 0;
				
				name = tempName.substring(10);	//Saves the name in the player class
							
				login = DatabaseHandler.userLogin(name, tempPass.substring(10));
				if(login > 0) {		// checks the value given by the database
					
					this.playerID = login;
					this.serverPort = serverPort;
					
					String tmp = Integer.toString(login);
					
					sendText(tmp);
					
					tmp = Integer.toString(this.serverPort);
								
					sendText(tmp);	//Sends the given serverport
					
					Iterator<String> i = GlobalServerMain.application.groupChatList.iterator();
					i.next(); 		// Skip Global chat
					while (i.hasNext()) {
						String chatName = i.next();
						sendText(Constants.NEWCHAT + chatName);
					}			
					
					Iterator<Player> y = GlobalServerMain.application.player.iterator();
					while (y.hasNext()) {
						String playerName = y.next().returnName();
						System.out.println("\nHva er playerName: " + Constants.GLOBALCHAT + playerName);
						sendText(Constants.GLOBALCHAT + playerName);
					}	
					
					return true;
				}
				else if (login == 0) {

					String tmp = Integer.toString(login);
					
					sendText(tmp);
					
					return false;
				}
			}
			else if (tempName.startsWith(SENDREGISTER) && tempPass.startsWith(SENDREGISTER)){
				boolean register = false; 
				
				register = DatabaseHandler.registerNewUser(tempName.substring(13), tempPass.substring(13));
			
				if (register) {		// Checks the value given be the database
					sendText(ACCEPTED); // Sends an accepted message back to client
					return false;
				}
				else {
					sendText(DECLINED);	// sends an declined message back to client
					return false;
				}
			}
		} catch (IOException ioe) { // catches any errors when trying to read from input
			ioe.printStackTrace();
		}
		return false;
	}
}
