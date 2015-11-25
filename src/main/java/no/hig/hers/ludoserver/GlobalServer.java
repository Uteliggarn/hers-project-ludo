package no.hig.hers.ludoserver;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.hig.hers.ludoshared.Constants;
import no.hig.hers.ludoshared.MyLogger;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import no.hig.hers.ludoserver.Player;

public class GlobalServer extends JFrame{
	
	private ServerSocket server;
	private ExecutorService executorService;
	
	private JTextArea outputArea;
	
	ArrayList<Player> players = new ArrayList<Player>();
	
	private ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<String>(50);
	
	ArrayList<Chat> groupChatList = new ArrayList<Chat>();
	private ArrayList<String> gameList = new ArrayList<String>();
	
	private ArrayList<Player> que = new ArrayList<Player>();
	
	private boolean shutdown = false;
    private final String fileNameEnd = "ChatLog.log"; //The end of the filename
    private String fileName; //The whole filename
    
    private int serverPorts = 10000;
    
    private int tmpPort;
    private String tmpName;
    
    static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public GlobalServer() {
		super("GlobalServer");
		
		Chat globalChat = new Chat("Global");
		groupChatList.add(globalChat);
		fileName = "Global" + fileNameEnd; //Placeholder so we can write to a file.
		
		try {
			MyLogger.setupLogger();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Couldn't create log files", e);
		}
		
		setUpGUI();

		try {
			server = new ServerSocket();
			server.setReuseAddress(true);
			server.bind(new InetSocketAddress(12344));
			
			executorService = Executors.newCachedThreadPool();
			
			startLoginMonitor();
			startMessageSender();
			startMessageListener();
			
			//executorService.shutdown();
			
		} catch (IOException ioe) {
			GlobalServer.LOGGER.log(Level.SEVERE, "Cannot set the socket", ioe);
			System.exit(1);
		}
		
		setSize(600, 400);
		setVisible(true);	
	}
	/**
	 * Method for setting up the server GUI.
	 */
	private void setUpGUI() {
		outputArea = new JTextArea();
		outputArea.setFont(new Font("Ariel", Font.PLAIN, 14));
		outputArea.setEditable(false);
		add(new JScrollPane(outputArea), BorderLayout.CENTER);
		outputArea.setText("Server awaiting connections\n");
				
	}

	/**
	 * Managing when players are logging on and off.
	 */
	private void startLoginMonitor() {
		executorService.execute(() -> {
			while (!shutdown) {
				try {
					Socket s = server.accept();
					Player p = new Player(s);
					synchronized (players) {
						if (p.loginChecker(serverPorts + 1)) {
							serverPorts++;
							players.add(p);
							displayMessage("PLAYER CONNECTED: " + p.getName() + "\n");
						}
					}
					//Thread.sleep(250);
				} catch (IOException ioe) {
					displayMessage("CONNECTION ERROR: " + ioe + "\n");
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
	private void startMessageListener() {
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
									else if (msg.startsWith(Constants.GAMEMESSAGE)) // game-related
										handleGameMessages(msg.substring(Constants.GAMEMESSAGE.length()), p);
									else if (msg.startsWith(Constants.CHATMESSAGE)) // chat-related
										handleChatMessages(msg.substring(Constants.CHATMESSAGE.length()), p);
									else if (msg.startsWith(Constants.PLAYERMESSAGE)) // player-related (only to one)
										handlePlayerMessages(msg.substring(Constants.PLAYERMESSAGE.length()), p);
									else if (msg.startsWith(Constants.GENERALMESSAGE))// player-related (all)	
										handleGeneralMessages(msg.substring(Constants.GENERALMESSAGE.length()), p);
										
									
									else {
										//Sends the message to both listeners. One for game and one for chat.
										handleGroupChatKeywords(p, msg);
										handleGameKeywords(p, msg);
									}
								}
							} catch (IOException ioe) {
								i.remove();
								
								messages.put(Constants.LOGOUT + p.getName());
								messages.put(p.getName() + " got lost in hyperspace");
								GlobalServer.LOGGER.log(Level.WARNING, "Error with reading message", ioe);
							}
						}
					}
				} catch (InterruptedException ie) {
					GlobalServer.LOGGER.log(Level.SEVERE, "Problem with thread", ie);
				}
				
				//The thread goes to sleep to save the CPU energy
				try {
					//Thread.sleep(250);
				} catch (Exception e) {
					GlobalServer.LOGGER.log(Level.WARNING, "Sleep interrupted", e);
				}
			}
		});
	}
	

	private void handleGameMessages(String msg, Player p) {
		executorService.execute(() -> {
			
		});
	}
	private void handleChatMessages(String msg, Player p) {
		executorService.execute(() -> {
			displayMessage("handleChatMessages: " + msg + " \n");
			if (msg.startsWith(Constants.JOIN))
				playerJoinChat(p, msg.substring(Constants.JOIN.length()));
			else if (msg.startsWith(Constants.NEWCHAT))
				createNewChat(p, msg);

			else sendChatMessage(p, msg);
			//handleGroupChatKeywords(p, msg);
		});
	}
	private void handleGeneralMessages(String msg, Player p) {
		executorService.execute(() -> {
			
		});
	}
	private void handleLogout(Player p) {
		executorService.execute(() -> {
			que.remove(p.getName());
			gameList.remove(Constants.IDGK + p.getName());

			try {
				messages.put(Constants.LOGOUT + p.getName());
			} catch (Exception e) {
				GlobalServer.LOGGER.log(Level.SEVERE, "Thread interrupted", e);
			}
			displayMessage("\n" + Constants.LOGOUT + p.getName());
		});
	}

	private void handlePlayerMessages(String msg, Player p) {
		executorService.execute(() -> {
			if (msg.equals(Constants.TOP))
				p.sendTopTenLists();
		});
	}
	
	private void playerJoinChat(Player p, String msg) {
		for (int i = 0; i < groupChatList.size(); i++) {
			if (msg.equals(groupChatList.get(i).getName())) {
				try {
					messages.put(Constants.CHATMESSAGE + Constants.JOIN + msg + p.getName());
					groupChatList.get(i).addPlayer(p.getName());
					p.sendPlayerList(groupChatList.get(i));
					
					//Writes to file
					fileName = msg + "_" + fileNameEnd;
					writeToFile(fileName, msg);
				} catch (InterruptedException e) {
					GlobalServer.LOGGER.log(Level.SEVERE, "Thread interrupted", e);
				}
			}
		}
	}
	
	private void sendChatMessage(Player p, String msg) {
		for (int i = 0; i < groupChatList.size(); i++) {
			if (msg.startsWith(groupChatList.get(i).getName())) {
				displayMessage(msg + "\n");
				msg = msg.substring(groupChatList.get(i).getName().length() + 1);
				
				try {
					messages.put(Constants.CHATMESSAGE + groupChatList.get(i).getName() + ":" + p.getName() + " > " + msg);
					
				//	messages.put(groupChatList.get(i) + ":" + p.getName() +" > " + msg.substring(groupChatList.get(i).length()+1));
					
					//Writes to file
					fileName = groupChatList.get(i).getName() + "_" + fileNameEnd;
					writeToFile(fileName, groupChatList.get(i).getName() + ":" + msg);
				} catch (InterruptedException e) {
					GlobalServer.LOGGER.log(Level.SEVERE, "Thread interrupted", e);
				}
			}	
		}
	}
	
	private void createNewChat(Player p, String msg) {
		Chat newChat = new Chat(msg.substring(Constants.NEWCHAT.length()));
		if(groupChatList.contains(newChat) 
				&& groupChatList.contains(new Chat(Constants.IDGK + p.getName())))
			try {
				p.sendText(Constants.ERRORCHAT);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		else {
			newChat = new Chat(msg.substring(Constants.NEWCHAT.length()));
			groupChatList.add(newChat);
			try {
				messages.put(Constants.CHATMESSAGE + msg);
			} catch (InterruptedException e) {
				GlobalServer.LOGGER.log(Level.SEVERE, "Thread interrupted", e);
			}
			displayMessage("New chat room: " + msg.substring(13) + " made by: " + p.getName() + "\n");
		}

	}

	/**
	 * All the chat messages / commands will be handled in this method.
	 * @param p The active player
	 * @param msg The message that was read
	 */
	private void handleGroupChatKeywords(Player p, String msg) {
	}
	
	/**
	 * All the commands concerning the game will be handled here.
	 * @param p The active player
	 * @param msg The message that was read
	 */
	private void handleGameKeywords(Player p, String msg) {
		try {
			if (msg.equals(Constants.QUEUE)){
				Player tmp = p;
				que.add(tmp);
				displayMessage("Player: " + p.getName() + " joined the queue. Queue size: " + que.size() + "\n");
				if(que.size() == 4) {
					boolean hostFound = false;
					for (int t=0; t<4; t++) {
						
						if (!gameList.contains(Constants.IDGK + que.get(t).getName()) && hostFound != true) {
							gameList.add(Constants.IDGK + que.get(t));
							hostFound = true;

							que.get(t).sendText(Constants.HOST);
							tmpPort = que.get(t).getServerPort();
							tmpName = que.get(t).getName();
							t = 0;
						}
						else if (hostFound == true && que.get(t).getName() != tmpName){

							que.get(t).sendText(Constants.HOST + que.get(t).returnIPaddress());
							tmpPort = que.get(t).getServerPort();
							tmpName = que.get(t).getName();
							t = 0;
						}
						/** SJEKK DENNE!!!!!!!!!!! */
						else if (hostFound == true && que.get(t).getName() != tmpName){

							que.get(t).sendText(Constants.HOTJOIN + tmpName);
							que.get(t).sendText(Integer.toString(tmpPort));
						}
					}
					for (int i=0; i<que.size(); i++) {
						que.remove(i);
					}
				}

				else if (msg.equals(Constants.CREATEGAME)) {
					displayMessage(p.getName() + " created a new game: " + Constants.IDGK + p.getName() + "\n");
					if (!gameList.contains(Constants.IDGK + p.getName())) {
						gameList.add(Constants.IDGK + p.getName());
						p.sendText(Constants.CREATEGAME + p.returnIPaddress());
					}
					else
						p.sendText(Constants.ERROR);
				}
				else if (msg.startsWith(Constants.INVITE)) {
					displayMessage(p.getName() + " invited " + msg.substring(7) + " to play a game\n");
				
					for (int y=0; y<players.size(); y++)
						if(msg.substring(7).equals(players.get(y).getName())) {
							players.get(y).sendText(Constants.JOIN + p.getName());
							players.get(y).sendText(Integer.toString(p.getServerPort()) + p.returnIPaddress());
						}
				}
				else if (msg.equals(Constants.GWON))
					DatabaseHandler.updatePlayersMatches(p.getPlayerID(), true);
				else if (msg.equals(Constants.GLOST))
					DatabaseHandler.updatePlayersMatches(p.getPlayerID(), false);

			}
			else if (msg.equals(Constants.GWON))
				DatabaseHandler.updatePlayersMatches(p.getPlayerID(), true);
			else if (msg.equals(Constants.GLOST))
				DatabaseHandler.updatePlayersMatches(p.getPlayerID(), false);
		} catch (IOException ioe) {
			GlobalServer.LOGGER.log(Level.SEVERE, "Cannot send message", ioe);
		}
	}
	
	/**
	 * Sends the message to all players.
	 */
	private void startMessageSender() {
		executorService.execute(() -> {
			while (!shutdown) {
				try {
					String message = messages.take();
					//displayMessage("Sending \"" + message + "\" to " + player.size() + " players\n");
					synchronized (players) {
						Iterator<Player> i = players.iterator();
						while (i.hasNext()) {
							Player p = i.next();
							try {
								p.sendText(message);
							} catch (IOException ioe) {
								i.remove();
								messages.add(Constants.LOGOUT + p.getName());
								messages.add(p.getName() + " got lost in hyperspace");
								GlobalServer.LOGGER.log(Level.WARNING, "Cannot send message", ioe);
							}
						}
					}
				//	Thread.sleep(250);
					//Thread.sleep(250);
				} catch (InterruptedException ie) {
					GlobalServer.LOGGER.log(Level.WARNING, "Sleep interrupted", ie);
				} catch (Exception e) {
					GlobalServer.LOGGER.log(Level.WARNING, "Sleep interrupted", e);
				}

			}
		});
	}
	
	private void displayMessage(String text) {
		SwingUtilities.invokeLater(() -> outputArea.append(text));
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
	private void writeToFile(String fileName, String data) {
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
	private String timeStamp() {
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
		return timeStamp;
	}
}