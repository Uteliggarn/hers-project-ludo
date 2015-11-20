package no.hig.hers.ludoserver;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class GlobalServer extends JFrame{
	
	private ServerSocket server;
	private ExecutorService executorService;
	
	private JTextArea outputArea;
	
	private ArrayList<Player> player = new ArrayList<Player>();
	
	private ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<String>(50);
	
	private ArrayList<String> groupChatList = new ArrayList<String>();
	
	private ArrayList<Player> que = new ArrayList<Player>();
	
	private boolean shutdown = false;
	
	private final String throwDiceText;
    private final String receiveDiceText;
    private final String turnOwnerText;
    private final String makeMoveText;
    
    private final String fileNameEnd = "ChatLog.log"; //The end of the filename
    private String fileName; //The whole filename
    
    private int serverPorts = 0;
    
    private int tmpPort;
	
	public GlobalServer() {
		
		super("GlobalServer");
		
		groupChatList.add("Global");
		
		fileName = "Global" + fileNameEnd; //Placeholder so we can write to a file.
		
		outputArea = new JTextArea();
		outputArea.setFont(new Font("Ariel", Font.PLAIN, 14));
		outputArea.setEditable(false);
		add(new JScrollPane(outputArea), BorderLayout.CENTER);
		outputArea.setText("Server awaiting connections\n");
		
		//The commands that will be received from the gameClient
		throwDiceText = "THROWDICE:"; //Request for a dice value
		makeMoveText = "MOVE:"; //Announce which piece moved
		
		//The commands that will be sent to the gameClient
		receiveDiceText = "RECEIVEDICE:"; //Return the dice value
		turnOwnerText = "TURNOWNER:"; //Announce who has the turn
				
		try {
			server = new ServerSocket();
			server.setReuseAddress(true);
			server.bind(new InetSocketAddress(12344));
			
			//server = new ServerSocket(1234); // Set up serverSocket
			executorService = Executors.newCachedThreadPool();
			//executorService = Executors.newFixedThreadPool(3);
			
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
								
								//System.out.println("\nHva er msg: " + msg);
								//Sends the message to both listeners. One for game and one for chat.
								handleGroupChatKeywords(p, msg);
								//handleGameActivity(p, msg);
								
								if (msg != null && msg.equals("queue")){
									Player tmp = p;
									que.add(tmp);
									displayMessage("Player: " + p.returnName() + " joined the queue. Queue size: " + que.size() + "\n");
									if(que.size() == 1) {
										boolean hostFound = false;
										for (int t=0; t<1; t++) {
											//System.out.println("Hva er que: "  );
											
											if (!que.get(t).returnHost() && hostFound != true) {
												player.get(player.indexOf(que.get(t))).setHost(true);
												hostFound = true;
												que.get(t).sendText("HOST");
												tmpPort = que.get(t).returnServerPort();
											}
											else {
												que.get(t).sendText("JOIN");
												que.get(t).sendPort(tmpPort);
											}	
										}
										
									}
								}
								else if (msg != null && msg.equals("createGame")) {
									if (p.returnHost() != true) {
										p.sendPort(player.size());
										for (int y=0; y<player.size(); y++) 
											p.sendText("player:" + player.get(y).returnName());
									}
									else
										p.sendPort(-1);
								}
								else if (msg != null && msg.startsWith("invite:")) {
									for (int y=0; y<player.size(); y++)
										if(msg.substring(7).equals(player.get(y).returnName())) {
											player.get(y).sendText("JOIN");
											player.get(y).sendPort(p.returnServerPort());
										}
								}
								else if (msg != null && msg.equals(">>>LOGOUT<<<")) {
									i.remove();
									que.remove(p.returnName());
									messages.put("LOGOUT:" + p.returnName());
								}
							} catch (IOException ioe) {
								i.remove();
								messages.put("LOGOUT:" + p.returnName());
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
								messages.add("LOGOUT:" + p.returnName());
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
					if (p.loginChecker(++serverPorts)) {
						displayMessage("PLAYER CONNECTED: " + p.returnName() + "\n");						
						try {
							//displayMessage("GlobalJOIN:" + p.returnName() + "\n");
							messages.put("GlobalJOIN:" + p.returnName());
							for (int t=0; t<player.size(); t++) {
								p.sendText("GlobalChatRoomJOIN:" + player.get(t).returnName());
							}
						} catch (InterruptedException ie) {
							ie.printStackTrace();
						}
						
						for (int i=0; i<groupChatList.size(); i++) {
							p.sendText(groupChatList.get(i)+ "JOIN:" + p.returnName());
							writeToFile(fileName, groupChatList.get(i)+ "JOIN:" + p.returnName());
						}
						
						synchronized (player) {
							player.add(p);/*
							Iterator<Player> i = player.iterator();
							while (i.hasNext()) {
								Player p1 = i.next();
								if (p != p1)
									for (int y=0; y<groupChatList.size(); y++) {
										p.sendText("NEWGROUPCHAT:" + groupChatList.get(y));
										writeToFile(fileName, "NEWGROUPCHAT:" + groupChatList.get(y));
									}
									try {
									p.sendText("GlobalChatRoomJOIN:" + p1.returnName());
									} catch (IOException ioe) {
										ioe.printStackTrace();
									}
							}*/
						}
						/*try {
							messages.put("GlobalChatRoomJOIN:" + p.returnName());
						} catch (InterruptedException ie) {
							ie.printStackTrace();
						}*/
					}
					else
						--serverPorts;
						
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
	
	/**
	 * All the chat messages / commands will be handled in this method.
	 * @param p The active player
	 * @param msg The message that was read
	 */
	private void handleGroupChatKeywords(Player p, String msg) {
		try {
			if (msg != null && msg.startsWith("NEWGROUPCHAT:")) {
				displayMessage("New message: " + msg + "\n");
				if(groupChatList.contains(msg.substring(13)))
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
				
				if (msg != null && msg.startsWith(groupChatList.get(i) + "JOIN:")) {
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
	 * Handles all the game messages / commands.
	 * @param p The active player
	 * @param msg The message that was read
	 */
	private void handleGameActivity(Player p, String msg) {
		try {
			if (msg != null && msg.startsWith(throwDiceText)) {
				//TODO:Check received id with correct id (not really needed, but why not)
				//TODO:Send the dice value to the clients
				//messages.put(receiveDiceText + diceValue);
				
			} else if (msg != null && msg.startsWith(makeMoveText)) {
				//Send a broadcast to every player about the move
				messages.put(msg);
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	/*
	 
	private void groupChatMonitor() {
		executorService.execute(() -> {
			while (!shutdown) {
					synchronized(player) {
						Iterator<Player> i = player.iterator();
						while (i.hasNext()) {
							Player p = i.next();
							try {
								String msg = p.read();
								if (msg != null && msg.startsWith("NEWGROUPCHAT:") && !groupChatList.contains(msg.substring(13))) {
									groupChatList.add(msg.substring(13));
									displayMessage("New chat room: " + msg.substring(13) + " made by: " + p.returnName() + "\n");
								} else if (groupChatList.contains(msg.substring(13))) {
									p.sendText("ERRORCHAT");
								}
								
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
						}
					}
				
			}
		});
	}
	*/
	
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