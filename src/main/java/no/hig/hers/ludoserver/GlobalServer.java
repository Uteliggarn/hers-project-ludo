package no.hig.hers.ludoserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;
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
	
	private boolean shutdown = false;
	
	private final String throwDiceText;
    private final String receiveDiceText;
    private final String turnOwnerText;
    private final String makeMoveText;
	
	public GlobalServer() {
		
		super("GlobalServer");
		
		groupChatList.add("GlobalChatRoom");
		
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
			server = new ServerSocket(12347); // Set up serverSocket
			executorService = Executors.newCachedThreadPool();
			
			startLoginMonitor();
			//groupChatMonitor();
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
								
								handleGroupChatKeywords(p, msg);
								handleGameActivity(p, msg);
								
								if (msg != null && msg.equals(">>>LOGOUT<<<")) {
									i.remove();
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
			}
		});
	}
	
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
			}
		});
	}
	
	private void startLoginMonitor() {
		executorService.execute(() -> {
			while (!shutdown) {
				try {
					Socket s = server.accept();
					Player p = new Player(s);
					if (p.loginChecker()) {
						displayMessage("PLAYER CONNECTED: " + p.returnName() + "\n");
						
						for (int i=0; i<groupChatList.size(); i++) {
							p.sendText(groupChatList.get(i)+ "JOIN:" + p.returnName());
						}
						synchronized (player) {
							player.add(p);
							Iterator<Player> i = player.iterator();
							while (i.hasNext()) {
								Player p1 = i.next();
								if (p != p1)
									for (int y=0; y<groupChatList.size(); y++) {
										p.sendText("NEWGROUPCHAT:" + groupChatList.get(y));
									}
									/*try {
									p.sendText("GlobalChatRoomJOIN:" + p1.returnName());
									} catch (IOException ioe) {
										ioe.printStackTrace();
									}*/
							}
						}
						/*try {
							messages.put("GlobalChatRoomJOIN:" + p.returnName());
						} catch (InterruptedException ie) {
							ie.printStackTrace();
						}*/
					}
						
				} catch (IOException ioe) {
					displayMessage("CONNECTION ERROR: " + ioe + "\n");
				}
			}
		});
	}
	
	private void handleGroupChatKeywords(Player p, String msg) {
		try {
		if (msg != null && msg.startsWith("NEWGROUPCHAT:")) {
			displayMessage("New message: " + msg + "\n");
			if(groupChatList.contains(msg.substring(13)))
				try {
					p.sendText("ERRORCHAT");
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
	
}