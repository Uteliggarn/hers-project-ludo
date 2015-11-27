package no.hig.hers.ludoclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import no.hig.hers.ludoshared.Constants;

/**
 * Each players gameserver that handles the connection between players
 * when a Ludo game is in a session
 */
public class GameServer {
	
	private ServerSocket server;
	private ExecutorService executorService;
	
	List<Player> player = new ArrayList<Player>();
	
	private ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<String>(50);
	
	private boolean shutdown = false;
	
	private int playerNr = 1;
	
	/**
	 * Constructor for GameServer where the the server socket is set
	 * and all the threads are started.
	 * @param socket
	 */
	public GameServer(int socket) {
		
		try {
			server = new ServerSocket();
			server.setReuseAddress(true);
			server.bind(new InetSocketAddress(socket));

			executorService = Executors.newFixedThreadPool(3);
			startLoginMonitor();
			startMessageSender();
			startMessageListener();
			
			executorService.shutdown();
			
		} catch (IOException ioe) {
			Main.LOGGER.log(Level.SEVERE, "Unable to create gameserver", ioe);
		}
		
		String chatName = Constants.GAMECHAT + Main.userName;
		Main.sendText(Constants.CHATMESSAGE + Constants.NEWCHAT + chatName);
	}
	
	/**
	 * Checks if the player list in the server is empty for players
	 * @return true if the player list in empty otherwise false
	 */
	public boolean isEmpty() {
		return player.isEmpty();
	}
	
	/**
	 * Thread taht handles all the input communication to the gameserver
	 * and synchronized the use of the thread on switch player object that is in use
	 */
	 private void startMessageListener() {
	        executorService.execute(() -> {
	            while (!shutdown) {
	                try {
	                	synchronized (player) {	// Only one thread at a time might use the clients object 
		                    Iterator<Player> i = player.iterator();
		                    while (i.hasNext()) {
		                        Player p = i.next();
		                        try {
			                        String msg = p.read();

			                        if (msg != null) {
				                        if(msg.startsWith(Constants.GAMESTART)) {
				                        	for (int t=0; t<player.size(); t++) {
				                    			System.out.println("\n" + t);
				                    			player.get(t).sendText(msg + (t+1));
											}
				                        	for(int t=0; t < player.size(); t++) {
				                        		String tmp;
				                        		tmp = Constants.GAMENAME + (t+1) + player.get(t).getName(); 
				                        		messages.put(tmp);
				                        	}
				                        }
				                        if(msg.startsWith(Constants.DICEVALUE)) {
				                        	messages.put(msg);
				                        }
				                        if(msg.startsWith(Constants.GAMEOVER)) {
				                        	messages.put(msg);
				                        	i.remove();
				                        }
				                        if(msg.equals(Constants.DISCONNECT)) {
				                        	messages.put(msg + Integer.toString(p.getPlayerNr()));
				                        	if (p.getHost()) {
				                        		messages.put(Constants.QUITGAME);
				                        	}
				                        	else
				                        		messages.put(Constants.LEAVEGAME + p.getName());
				                        	i.remove();
				                        	
				                        }
			                        }
		                        } catch (IOException ioe) {	// Unable to communicate with the client, remove it
		                        	i.remove();
		                            messages.put(Constants.CHATMESSAGE + Constants.LOGOUT+p.getName());
		                            messages.put(p.getName()+" got lost in hyperspace");
		                            Main.LOGGER.log(Level.WARNING, "Disconnected from server", ioe);
		                        }
		                    }
	                	}
	                } catch (InterruptedException ie) {
	                	Main.LOGGER.log(Level.SEVERE, "Synchronized function for player failed", ie);
	                }
	           }
	        });
	    }

	 /**
	  * Sends all the messages from the ArrayBlockingQue to the players
	  * synchronized on player object aswell
	  */
	 private void startMessageSender() {
	        executorService.execute(() -> {
	            while (!shutdown) {
	                try {
	                    String message = messages.take();
	                    
	                    synchronized (player) {		// Only one thread at a time might use the clients object
		                    Iterator<Player> i = player.iterator();
		                    while (i.hasNext()) {
		                        Player p = i.next();
		                        try {
		                        	p.sendText(message);
		                        } catch (IOException ioe) {	// Unable to communicate with the client, remove it
		                        	i.remove();
		                        	messages.add(Constants.CHATMESSAGE + Constants.LOGOUT+p.getName());
		                        	messages.add(p.getName()+" got lost in hyperspace");
		                        	Main.LOGGER.log(Level.WARNING, p.getName() + " disconnected from server", ioe);
		                        }
		                    }
	                    }
	                } catch (InterruptedException ie) {
	                	Main.LOGGER.log(Level.SEVERE, "Synchronized function for player failed", ie);
	                }
	            }
	        });
	 }
	 
	 /**
	  * Thread for handling every connection to the gameserver
	  * and sets the connection to a player object
	  * Only for four connection can be done before new ones get blocked
	  */
	 private void startLoginMonitor() {
	        executorService.execute(() -> {
	            while (!shutdown) {
	                try {
	                    Socket s = server.accept();
	                    
	                    if (player.size() != 4) {
	                    	
		                    Player p = new Player(s, playerNr);
		                    playerNr++;
		                   
		                    synchronized (player) {
		                    	player.add(p);
		                    	
		                    	int count = 0;
		                    	Iterator<Player> i = player.iterator();
			                    while (i.hasNext()) {		// Send message to all clients that a new person has joined
			                    	++count;
			                    	Player t = i.next();
			                    	try {
			                    		//if(!t.getHost()) {
			                    			p.sendText(Constants.JOIN + t.getName());
			                    			//if (!t.getName().equals(p.getName()) && !p.getHost()) {
			                    			if (!t.getName().equals(p.getName()) && !p.getHost()) {
			                    				t.sendText(Constants.JOIN + p.getName());
			                    			}
			                    		//}
			                    		if (count == 4) {
			                    			for (int y=0; y<player.size(); y++) {
			                    				if (player.get(y).getHost())
			                    					t.sendText(Constants.JOIN + p.getName());
			                    			}
			                    		}
			                    	} catch (IOException ioe) {
			                    		Main.LOGGER.log(Level.INFO, "Couldn't send gameserver messages", ioe);
			                    	}
			                    } 
		                    }
	                    }
	                } catch (IOException ioe) {
	                	Main.LOGGER.log(Level.SEVERE, "Unable to setup socket", ioe);
	                }
	            }
	        });
	    }
	 
	 /**
	  * shutdowns the active threads in use. And stop new thread
	  * task from creating.
	  */
	 public void close() {
	    	executorService.shutdown();
	    }
}
