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
	
	ArrayList<Player> player = new ArrayList<Player>();
	
	private ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<String>(50);
	
	ArrayList<String> groupChatList = new ArrayList<String>();
	private ArrayList<String> gameList = new ArrayList<String>();
	
	private ArrayList<Player> que = new ArrayList<Player>();
	
	private boolean shutdown = false;
    
    private final String JOIN = "JOIN:";
    private final String HOTJOIN = "HOTJOIN:";
    private final String HOST = "HOST";
    private final String INVITE = "INVITE:";	// invite: +7
    private final String LOGOUT = "LOGOUT:";
    private final String CLOGOUT = ">>>LOGOUT<<<";
    private final String QUEUE = "QUEUE";
    
    private final String GLOBALCHAT = "GlobalJOIN:";	// Global chat name
    
    private final String IDGK = "IDGK";
    private final String CREATEGAME = "CREATEGAME";
    private final String ERROR = "ERROR";
    private final String TOP = "TOP";
    
    private final String GWON = "GAMEWON";
    private final String GLOST = "GAMELOST";
    
    private final String fileNameEnd = "ChatLog.log"; //The end of the filename
    private String fileName; //The whole filename
    
    private int serverPorts = 10000;
    
    private int tmpPort;
    private String tmpName;
    
    static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public GlobalServer() {
		super("GlobalServer");
		
		groupChatList.add("Global");
		
		fileName = "Global" + fileNameEnd; //Placeholder so we can write to a file.
		
		try {
			MyLogger.setupLogger();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Couldn't create log files", e);
		}
		
		outputArea = new JTextArea();
		outputArea.setFont(new Font("Ariel", Font.PLAIN, 14));
		outputArea.setEditable(false);
		add(new JScrollPane(outputArea), BorderLayout.CENTER);
		outputArea.setText("Server awaiting connections\n");
				
		try {
			server = new ServerSocket();
			server.setReuseAddress(true);
			server.bind(new InetSocketAddress(12344));
			
			executorService = Executors.newCachedThreadPool();
			
			startLoginMonitor();
			startMessageSender();
			startMessageListener();
			
			executorService.shutdown();
			
		} catch (IOException ioe) {
			GlobalServer.LOGGER.log(Level.SEVERE, "Cannot set the socket", ioe);
			System.exit(1);
		}
		
		setSize(600, 400);
		setVisible(true);	
	}

	/**
	 * Listens to all messages that comes in. Sends the different messages to the correct methods.
	 */
	private void startMessageListener() {
		executorService.execute(() -> {
			while (!shutdown) {
				try {
					synchronized(player) {
						Iterator<Player> i = player.iterator();
						while (i.hasNext()) {
							Player p = i.next();
							try {
								String msg = p.read();
								
								if (msg != null) {
									if (msg.equals(Constants.CLOGOUT)) {
										i.remove();
										que.remove(p.returnName());

										gameList.remove(IDGK + p.returnName());

										messages.put(LOGOUT + p.returnName());
										displayMessage("\n" + LOGOUT + p.returnName());
									} else if (msg.equals(p.returnName() + Constants.TOP)) {
										System.out.println(msg + "  TEST!");
										handleTopTenLists(p);
									}
									else if (msg.equals("GETPLAYERLIST")){
										p.sendPlayerList();
									}
									else {
										//Sends the message to both listeners. One for game and one for chat.
										handleGroupChatKeywords(p, msg);
										handleGameKeywords(p, msg);
									}
								}
							} catch (IOException ioe) {
								i.remove();
								messages.put(Constants.LOGOUT + p.returnName());
								messages.put(p.returnName() + " got lost in hyperspace");
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
	
	private void handleTopTenLists(Player p) {
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

				p.sendText(Constants.TOPPLAYED + toptenPlayedName);	
			}
			while(resultSetWon.next()) {
				String tmp;
				toptenWonName = (String) resultSetWon.getObject(1);
				toptenWonCount = Integer.toString((int)resultSetWon.getObject(2));
				tmp = (toptenWonName + "," + toptenWonCount);

				p.sendText(Constants.TOPWON + tmp);
			}
		}
		catch (Exception e) {
			GlobalServer.LOGGER.log(Level.SEVERE, "Exception", e);
		}
	}
	
	/**
	 * All the chat messages / commands will be handled in this method.
	 * @param p The active player
	 * @param msg The message that was read
	 */
	private void handleGroupChatKeywords(Player p, String msg) {
		try {
			if (msg.startsWith(Constants.NEWCHAT)) {
				if(groupChatList.contains(msg.substring(13)) && groupChatList.contains(IDGK + p.returnName()))
					try {
						p.sendText(Constants.ERRORCHAT);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				else {
					groupChatList.add(msg.substring(13));
					messages.put(Constants.NEWCHAT + msg.substring(13));
					displayMessage("New chat room: " + msg.substring(13) + " made by: " + p.returnName() + "\n");
				}
			}
			for (int i=0; i<groupChatList.size(); i++) {
				
				if (msg.startsWith(groupChatList.get(i) + JOIN)) {
					messages.put(msg);
					
					//Writes to file
					fileName = groupChatList.get(i) + "_" + fileNameEnd;
					writeToFile(fileName, msg);
				}
				else if (msg.startsWith(groupChatList.get(i) + ":")) {
					displayMessage(groupChatList.get(i) + ":" + msg.substring(groupChatList.get(i).length() + 1) + "\n");
					messages.put(groupChatList.get(i) + ":" + p.returnName() +" > " + msg.substring(groupChatList.get(i).length()+1));
					
					//Writes to file
					fileName = groupChatList.get(i) + "_" + fileNameEnd;
					writeToFile(fileName, groupChatList.get(i) + ":" + msg.substring(groupChatList.get(i).length() + 1));
				}	
			}
		} catch (InterruptedException ie) {
			GlobalServer.LOGGER.log(Level.SEVERE, "Thread interrupted", ie);
		}
	}
	
	/**
	 * All the commands concerning the game will be handled here.
	 * @param p The active player
	 * @param msg The message that was read
	 */
	private void handleGameKeywords(Player p, String msg) {
		try {
			if (msg.equals(QUEUE)){
				Player tmp = p;
				que.add(tmp);
				displayMessage("Player: " + p.returnName() + " joined the queue. Queue size: " + que.size() + "\n");
				if(que.size() == 4) {
					boolean hostFound = false;
					for (int t=0; t<4; t++) {
						
						if (!gameList.contains(Constants.IDGK + que.get(t).returnName()) && hostFound != true) {
							gameList.add(Constants.IDGK + que.get(t));
							hostFound = true;
							que.get(t).sendText(Constants.HOST);
							tmpPort = que.get(t).returnServerPort();
							tmpName = que.get(t).returnName();
							t = 0;
						}
						else if (hostFound == true && que.get(t).returnName() != tmpName){
							que.get(t).sendText(Constants.HOTJOIN + tmpName);
							que.get(t).sendText(Integer.toString(tmpPort));
						}
					}
					for (int i=0; i<que.size(); i++) {
						que.get(i).sendText(Constants.QUEOPEN);
						que.remove(i);
					}
				}
			}
			else if (msg.equals(Constants.CREATEGAME)) {
				displayMessage(p.returnName() + " created a new game: " + Constants.IDGK + p.returnName() + "\n");
				if (!gameList.contains(Constants.IDGK + p.returnName())) {
					gameList.add(Constants.IDGK + p.returnName());
					p.sendText(Constants.CREATEGAME);
				}
				else
					p.sendText(Constants.ERROR);
			}
			else if (msg.startsWith(Constants.INVITE)) {
				displayMessage(p.returnName() + " invited " + msg.substring(7) + " to play a game\n");
			
				for (int y=0; y<player.size(); y++)
					if(msg.substring(7).equals(player.get(y).returnName())) {
						player.get(y).sendText(Constants.JOIN + player.get(y).returnName());
						player.get(y).sendText(Integer.toString(player.get(y).returnServerPort()));
					}
			}
			else if (msg.equals(GWON))
				DatabaseHandler.updatePlayersMatches(p.returnPlayerID(), true);
			else if (msg.equals(GLOST))
				DatabaseHandler.updatePlayersMatches(p.returnPlayerID(), false);
			
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
					synchronized (player) {
						Iterator<Player> i = player.iterator();
						while (i.hasNext()) {
							Player p = i.next();
							try {
								p.sendText(message);
							} catch (IOException ioe) {
								i.remove();
								messages.add(LOGOUT + p.returnName());
								messages.add(p.returnName() + " got lost in hyperspace");
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
	
	/**
	 * Managing when players are logging on and off.
	 */
	private void startLoginMonitor() {
		executorService.execute(() -> {
			while (!shutdown) {
				try {
					Socket s = server.accept();
					Player p = new Player(s);
					synchronized (player) {
						player.add(p);
						int g = player.indexOf(p);
						
						if (p.loginChecker(++serverPorts)) {
							displayMessage("PLAYER CONNECTED: " + p.returnName() + "\n");
							messages.put(Constants.GLOBALCHAT + p.returnName());
						}
						else {
							--serverPorts;
							player.remove(g);
						}
					}
					//Thread.sleep(250);
				} catch (IOException ioe) {
					displayMessage("CONNECTION ERROR: " + ioe + "\n");
					GlobalServer.LOGGER.log(Level.SEVERE, "Connection Error", ioe);
				} catch (Exception e) {
					e.printStackTrace();
					GlobalServer.LOGGER.log(Level.WARNING, "Exception", e);
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