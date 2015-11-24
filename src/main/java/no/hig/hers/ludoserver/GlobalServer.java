package no.hig.hers.ludoserver;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.concurrent.*;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import no.hig.hers.ludoserver.Player;

public class GlobalServer extends JFrame{
	
	private ServerSocket server;
	private ExecutorService executorService;
	
	private JTextArea outputArea;
	
	private ArrayList<Player> player = new ArrayList<Player>();
	
	private ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<String>(50);
	
	private ArrayList<String> groupChatList = new ArrayList<String>();
	private ArrayList<String> gameList = new ArrayList<String>();
	
	private ArrayList<Player> que = new ArrayList<Player>();
	
	private boolean shutdown = false;
    
    private final String JOIN = "JOIN:";
    private final String HOST = "HOST";
    private final String INVITE = "INVITE:";	// invite: +7
    private final String LOGOUT = "LOGOUT:";
    private final String CLOGOUT = ">>>LOGOUT<<<";
    private final String QUEUE = "QUEUE";
    
    private final String GLOBALCHAT = "GlobalJOIN:";	// Global chat name
    
    private final String IDGK = "IDGK";
    private final String CREATEGAME = "CREATEGAME";
    private final String ERROR = "ERROR";
    
    private final String GWON = "GAMEWON";
    private final String GLOST = "GAMELOST";
    
    private final String fileNameEnd = "ChatLog.log"; //The end of the filename
    private String fileName; //The whole filename
    
    private int serverPorts = 10000;
    
    private int tmpPort;
    private String tmpName;
	
	public GlobalServer() {
		
		super("GlobalServer");
		
		groupChatList.add("Global");
		
		fileName = "Global" + fileNameEnd; //Placeholder so we can write to a file.
		
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
			ioe.printStackTrace();
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
								
								//Sends the message to both listeners. One for game and one for chat.
								handleGroupChatKeywords(p, msg);
								handleGameKeywords(p, msg);
								
								if (msg != null && msg.equals(CLOGOUT)) {
									i.remove();
									que.remove(p.returnName());
									gameList.remove(IDGK + p.returnName());
									messages.put(LOGOUT + p.returnName());
								}
							} catch (IOException ioe) {
								i.remove();
								messages.put(LOGOUT + p.returnName());
								messages.put(p.returnName() + " got lost in hyperspace");
							}
						}
					}
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
				
				//The thread goes to sleep to save the CPU energy
				try {
					Thread.sleep(250);
				} catch (Exception e) {
					// Prints the stackTrace if anything goes wrong.
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * All the chat messages / commands will be handled in this method.
	 * @param p The active player
	 * @param msg The message that was read
	 */
	private void handleGroupChatKeywords(Player p, String msg) {
		try {
			
			if (msg != null && msg.startsWith("NEWGROUPCHAT:")) {
				if(groupChatList.contains(msg.substring(13)) && groupChatList.contains(IDGK + p.returnName()))
					try {
						p.sendText("ERRORCHAT");
						writeToFile(fileName, "ERRORCHAT");
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				else {
					groupChatList.add(msg.substring(13));
					messages.put("NEWGROUPCHAT:" + msg.substring(13));
					displayMessage("New chat room: " + msg.substring(13) + " made by: " + p.returnName() + "\n");
				}
			}
			for (int i=0; i<groupChatList.size(); i++) {
				
				if (msg != null && msg.startsWith(groupChatList.get(i) + JOIN)) {
					messages.put(msg); 
				}
				else if (msg != null && msg.startsWith(groupChatList.get(i) + ":")) {
					displayMessage(groupChatList.get(i) + ":" + msg.substring(groupChatList.get(i).length() + 1) + "\n");
					messages.put(groupChatList.get(i) + ":" + p.returnName() +" > " + msg.substring(groupChatList.get(i).length()+1));
				}	
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	/**
	 * All the commands concerning the game will be handled here.
	 * @param p The active player
	 * @param msg The message that was read
	 */
	private void handleGameKeywords(Player p, String msg) {
		try {
			if (msg != null && msg.equals(QUEUE)){
				Player tmp = p;
				que.add(tmp);
				displayMessage("Player: " + p.returnName() + " joined the queue. Queue size: " + que.size() + "\n");
				if(que.size() == 4) {
					boolean hostFound = false;
					for (int t=0; t<4; t++) {
						
						//que.get(t).sendText("HOST");
						//System.out.println("Hva er que: "  );
						
						if (!gameList.contains(IDGK + que.get(t).returnName()) && hostFound != true) {
							//player.get(player.indexOf(que.get(t))).setHost(true);
							gameList.add(IDGK + que.get(t));
							hostFound = true;
							que.get(t).sendText(HOST);
							tmpPort = que.get(t).returnServerPort();
							tmpName = que.get(t).returnName();
						}
						else {
							que.get(t).sendText(JOIN + tmpName);
							que.get(t).sendText(Integer.toString(tmpPort));
						}	
						
					}
				}
			}
			else if (msg != null && msg.equals(CREATEGAME)) {
				displayMessage(p.returnName() + " created a new game: " + IDGK + p.returnName() + "\n");
				if (!gameList.contains(IDGK + p.returnName())) {
					gameList.add(IDGK + p.returnName());
					p.sendText(CREATEGAME);
				}
				else
					p.sendText(ERROR);
			}
			else if (msg != null && msg.startsWith(INVITE)) {
				displayMessage(p.returnName() + " invited " + msg.substring(7) + " to play a game\n");
			
				for (int y=0; y<player.size(); y++)
					if(msg.substring(7).equals(player.get(y).returnName())) {
						player.get(y).sendText(JOIN + player.get(y).returnName());
						player.get(y).sendText(Integer.toString(player.get(y).returnServerPort()));
					}
			}
			else if (msg != null && msg.equals(GWON))
				DatabaseHandler.updatePlayersMatches(p.returnPlayerID(), true);
			else if (msg != null && msg.equals(GLOST))
				DatabaseHandler.updatePlayersMatches(p.returnPlayerID(), false);
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
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
					displayMessage("Sending \"" + message + "\" to " + player.size() + " players\n");
					synchronized (player) {
						Iterator<Player> i = player.iterator();
						while (i.hasNext()) {
							Player p = i.next();
							try {
								p.sendText(message);
								writeToFile(fileName, message);
							} catch (IOException ioe) {
								i.remove();
								messages.add(LOGOUT + p.returnName());
								messages.add(p.returnName() + " got lost in hyperspace");
							}
						}
					}
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
				
				//The thread goes to sleep to save the CPU energy
				try {
					Thread.sleep(250);
				} catch (Exception e) {
					// Prints the stackTrace if anything goes wrong.
					e.printStackTrace();
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
					
						
						/*
						for (int i=0; i<groupChatList.size(); i++) {
							p.sendText(groupChatList.get(i)+ "JOIN:" + p.returnName());
							writeToFile(fileName, groupChatList.get(i)+ "JOIN:" + p.returnName());
						}*/
						
					//player.add(p);
					//int g = player.indexOf(p);
					/*
					synchronized (player) {
						player.add(p);
						int g = player.indexOf(p);
						
						if (p.loginChecker(++serverPorts)) {
									
							displayMessage("PLAYER CONNECTED: " + p.returnName() + "\n");						
							try {
								messages.put(GLOBALCHAT + p.returnName());
									
								for (int t=0; t<player.size(); t++) {
									if(!player.get(t).equals(p.returnName()))
										p.sendText(GLOBALCHAT + player.get(t).returnName());
								}
							} catch (InterruptedException ie) {
								ie.printStackTrace();
							}	
						}
						else {
							--serverPorts;
							player.remove(g);
						}
							*/
						synchronized (player) {
							player.add(p);
							int g = player.indexOf(p);
							
							if (p.loginChecker(++serverPorts)) {    	
		                    	try {
		                    		messages.put(GLOBALCHAT + p.returnName());
		                    	} catch (InterruptedException ie) {
		                    		ie.printStackTrace();
		                    	}
		                    	
		                    	Iterator<Player> i = player.iterator();
			                    while (i.hasNext()) {		// Send message to all clients that a new person has joined
			                        Player p1 = i.next();
			                        if (p != p1)
			                        	try {
											p.sendText(GLOBALCHAT + p1.returnName());
											//p.sendText(groupChatList.get(i)+ "JOIN:" + p.returnName());
			                        	} catch (IOException ioelocal) {
			                        		// Lost connection, but doesn't bother to handle it here
			                        	}
			                    }
							}
							else {
								--serverPorts;
								player.remove(g);
							}
						}
				} catch (IOException ioe) {
					displayMessage("CONNECTION ERROR: " + ioe + "\n");
				}
				
				//The thread goes to sleep to save the CPU energy
				try {
					Thread.sleep(250);
				} catch (Exception e) {
					// Prints the stackTrace if anything goes wrong.
					e.printStackTrace();
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
	 * Code for the timestamp:
	 * http://stackoverflow.com/questions/5175728/ 
	 * 
	 * This is the logging system for the GlobalServer.
	 * @param fileName The name of the file that will be written to
	 * @param data The data that will be written
	 */
	private void writeToFile(String fileName, String data) {
		PrintWriter writer = null;
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			writer.println(timeStamp + " " + data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}