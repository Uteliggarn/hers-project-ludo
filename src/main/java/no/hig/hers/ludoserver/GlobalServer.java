package no.hig.hers.ludoserver;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.hig.hers.ludoshared.Constants;
import no.hig.hers.ludoshared.MyLogger;

import javax.swing.JFrame;
import no.hig.hers.ludoserver.Player;

/**
 * @author on 27.11.2015 
 * Global server handles all the communication form the clients
 * and messages back to the clients.
 */
public class GlobalServer extends JFrame{
	private static boolean shutdown = false;
	private static int serverPorts = 10000;
	
	static GlobalServerGUIHandler GUI;
	private static ServerSocket server;
	private static ExecutorService executorService;
	
	static List<Player> players = new ArrayList<>();
	static List<Chat> groupChatList = new ArrayList<>();
	private static List<String> gameList = new ArrayList<>();
	private static List<Player> que = new ArrayList<>();
	static ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<String>(50);
	
	private static String tmpName;
	private static int tmpPort;
	private static String tmpIP;
	
    static String fileName; //The whole filename
    static final String fileNameEnd = "ChatLog.log"; //The end of the filename

    final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
    /**
     * Method that is called when main starts. Server socket is set and
     * the thread pool is made aswell as all the infinite threads are launched.
	 * @param args the command line arguments passed to the application.
     */
    public static void main( String[] args ) {
    	GUI = new GlobalServerGUIHandler();
		
		Chat globalChat = new Chat("Global");
		groupChatList.add(globalChat);
		fileName = "Global" + fileNameEnd; //Placeholder so we can write to a file.
		
		try {
			MyLogger.setupLogger();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Couldn't create log files", e);
		}

		try {
			server = new ServerSocket();
			server.setReuseAddress(true);
			server.bind(new InetSocketAddress(12344));
			
			executorService = Executors.newCachedThreadPool();
			
			startLoginMonitor();
			startMessageSender();
			startMessageListener();
		} catch (IOException ioe) {
			GlobalServer.LOGGER.log(Level.SEVERE, "Cannot set the socket", ioe);
			System.exit(1);
		}
	}

	/**
	 * Managing when players are logging on and off.
	 */
	private static void startLoginMonitor() {
		executorService.execute(() -> {
			while (!shutdown) {
				try {
					Socket s = server.accept();
					Player p = new Player(s);
					synchronized (players) {
						if (p.loginChecker(serverPorts + 1)) {
							serverPorts++;
							players.add(p);
							GUI.displayMessage("PLAYER CONNECTED: " + p.getName() + "\n");
						}
					}
				} catch (IOException ioe) {
					GUI.displayMessage("CONNECTION ERROR: " + ioe + "\n");
					GlobalServer.LOGGER.log(Level.SEVERE, "Connection Error", ioe);
				} catch (Exception e) {
					GlobalServer.LOGGER.log(Level.WARNING, "Exception", e);
				}
			}
		});
	}

	/**
	 * Listens to all messages that comes in. Sends the different messages to the correct methods.
	 */
	private static void startMessageListener() {
		executorService.execute(() -> {
			while (!shutdown) {
				try {
					synchronized(players) {
						Iterator<Player> i = players.iterator();
						while (i.hasNext()) {
							Player p = i.next();
							try {
								String msg = p.read();
								if (msg != null) {
									if (msg.equals(Constants.CLOGOUT)) {
										i.remove();
										handleLogout(p);
									}
									else if (msg.startsWith(Constants.CHATMESSAGE)) // chat-related
										handleChatMessages(msg.substring(Constants.CHATMESSAGE.length()), p);
									else if (msg.startsWith(Constants.PLAYERMESSAGE)) // player-related (only to one)
										handlePlayerMessages(msg.substring(Constants.PLAYERMESSAGE.length()), p);
									
									else {
										//Sends the message to game listener.
										handleGameKeywords(p, msg);
									}
								}
							} catch (IOException ioe) {
								i.remove();
								
								messages.put(Constants.CHATMESSAGE + Constants.LOGOUT + p.getName());
								messages.put(p.getName() + " got lost in hyperspace");
								GlobalServer.LOGGER.log(Level.WARNING, "Error with reading message", ioe);
							}
						}
					}
				} catch (InterruptedException ie) {
					GlobalServer.LOGGER.log(Level.SEVERE, "Problem with thread", ie);
				}
			}
		});
	}
	
	/**	
	 * Method for handling chat-related messages.
	 * Creates a new thread that handles the message.
	 * @param msg The message to handle
	 * @param p The player that sent the message.
	 */
	private static void handleChatMessages(String msg, Player p) {
		executorService.execute(() -> {
			if (msg.startsWith(Constants.JOIN))
				ChatHandler.playerJoinChat(p, msg.substring(Constants.JOIN.length()));
			else if (msg.startsWith(Constants.NEWCHAT))
				ChatHandler.createNewChat(p, msg);
			else if (msg.startsWith(Constants.LEAVECHAT))
				ChatHandler.playerLeaveChat(p, msg.substring(Constants.LEAVECHAT.length()));
			else ChatHandler.sendChatMessage(p, msg);
		});
	}

	/**
	 * Method for handling a player logging out / exiting game.
	 * Removes the player from the game queues and lists,
	 * then removing it from the chats.
	 * Finally, sends a message that the player has logged out to all players.
	 * 
	 * @param p The quitter.
	 */
	private static void handleLogout(Player p) {
		executorService.execute(() -> {
			que.remove(p);
			gameList.remove(Constants.IDGK + p.getName());
			
			for (int i = 0; i < groupChatList.size(); i++)
				groupChatList.get(i).removePlayer(p.getName());

			try {
				messages.put(Constants.CHATMESSAGE + Constants.LOGOUT + p.getName());
			} catch (Exception e) {
				GlobalServer.LOGGER.log(Level.SEVERE, "Thread interrupted", e);
			}
			GUI.displayMessage("\n" + Constants.LOGOUT + p.getName());
		});
	}

	/**
	 * Method for handling all player-related messages.
	 * Creates a new thread for handling the message.
	 * 
	 * @param msg The message to handle
	 * @param p The player that sent the message, and will receive the answer.
	 */
	private static void handlePlayerMessages(String msg, Player p) {
		executorService.execute(() -> {
			if (msg.equals(Constants.TOP))
				p.sendTopTenLists();
			else if (msg.equals(Constants.PLAYERSCORES))
				p.sendPlayerScores();
		});
	}
	
	
	/**
	 * All the commands concerning the game will be handled here.
	 * @param p The active player
	 * @param msg The message that was read
	 */
	private static void handleGameKeywords(Player p, String msg) {
		try {
			if (msg.equals(Constants.QUEUE)){
				Player tmp = p;
				que.add(tmp);
				GUI.displayMessage("Player: " + p.getName() + " joined the queue. Queue size: " + que.size() + "\n");
				if(que.size() == 4) {
					boolean hostFound = false;
					for (int i=0; i<4; i++) {
						if (!gameList.contains(Constants.IDGK + que.get(i).getName()) && hostFound != true) {
							gameList.add(Constants.IDGK + que.get(i));
							hostFound = true;

							que.get(i).sendText(Constants.HOST  + que.get(i).getIPaddress());
							tmpPort = que.get(i).getServerPort();
							tmpName = que.get(i).getName();
							tmpIP = que.get(i).getIPaddress();
							i = 0;

						}
						else if (hostFound && que.get(i).getName() != tmpName){
							que.get(i).sendText(Constants.HOTJOIN + tmpName);
							que.get(i).sendText(Integer.toString(tmpPort) + tmpIP);
						}
					}
					if (hostFound)
						for (int i=0; i<que.size(); i++)
							que.get(i).sendText(Constants.QUEOPEN);
					que.clear();
				}
			}

			else if (msg.equals(Constants.CREATEGAME)) {
				GUI.displayMessage(p.getName() + " created a new game: " + Constants.IDGK + p.getName() + "\n");
				gameList.add(Constants.IDGK + p.getName());
				p.sendText(Constants.CREATEGAME + p.getIPaddress());
			}
			else if (msg.startsWith(Constants.INVITE)) {
				GUI.displayMessage(p.getName() + " invited " + msg.substring(7) + " to play a game\n");
				
				for (int y=0; y<players.size(); y++)
					if(msg.substring(7).equals(players.get(y).getName())) {
						players.get(y).sendText(Constants.JOIN + p.getName());
						players.get(y).sendText(Integer.toString(p.getServerPort()) + p.getIPaddress());
					}
			}

			else if (msg.equals(Constants.GAMEWON))
				DatabaseHandler.updatePlayersMatches(p.getPlayerID(), true);
			else if (msg.equals(Constants.GAMELOST))
				DatabaseHandler.updatePlayersMatches(p.getPlayerID(), false);
			else if (msg.startsWith(Constants.REMOVEHOST))
				gameList.remove(msg.substring(11));

		} catch (IOException ioe) {
			GlobalServer.LOGGER.log(Level.SEVERE, "Cannot send message", ioe);
		}
	}
	
	/**
	 * Sends the message to all players.
	 */
	private static void startMessageSender() {
		executorService.execute(() -> {
			while (!shutdown) {
				try {
					String message = messages.take();
					synchronized (players) {
						Iterator<Player> i = players.iterator();
						while (i.hasNext()) {
							Player p = i.next();
							try {
								p.sendText(message);
							} catch (IOException ioe) {
								i.remove();
								messages.add(Constants.CHATMESSAGE + Constants.LOGOUT + p.getName());
								messages.add(p.getName() + " got lost in hyperspace");
								GlobalServer.LOGGER.log(Level.WARNING, "Cannot send message", ioe);
							}
						}
					}
				} catch (InterruptedException ie) {
					GlobalServer.LOGGER.log(Level.WARNING, "Could not take take message", ie);
				}
			}
		});
	}
	
	/**
	 * Code for writing to file:
	 * http://stackoverflow.com/questions/2885173/
	 * http://stackoverflow.com/questions/1625234/
	 * 
	 * This is the logging system for the GlobalServer.
	 * @param fileName The name of the file that will be written to
	 * @param data The data that will be written
	 */
	static void writeToFile(String fileName, String data) {
		PrintWriter writer = null;
		String timeStamp = timeStamp();
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			writer.println(timeStamp + " " + data);
		} catch (IOException e) {
			GlobalServer.LOGGER.log(Level.SEVERE, "Error writing to file", e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	/**
	 * http://stackoverflow.com/questions/5175728/
	 * Used to create the timestamp when writing to file.
	 * @return The timestamp
	 */
	private static String timeStamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
		 
	}
}