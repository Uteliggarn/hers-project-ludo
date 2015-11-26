package no.hig.hers.ludoclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import no.hig.hers.ludoshared.Constants;

public class GameServer {
	
	private ServerSocket server;
	private ExecutorService executorService;
	
	ArrayList<Player> player = new ArrayList<Player>();
	
	private ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<String>(50);
	
	private boolean shutdown = false;
	
	private final String LOGOUT = "LOGOUT:";
	private final String JOIN = "JOIN:";
	private int playerNr = 1; 
	
	public GameServer(int socket) {
		
		try {
			server = new ServerSocket();
			server.setReuseAddress(true);
			server.bind(new InetSocketAddress(socket));
			
			//executorService = Executors.newCachedThreadPool();
			executorService = Executors.newFixedThreadPool(3);
			startLoginMonitor();
			startMessageSender();
			startMessageListener();
			
			executorService.shutdown();
			
		} catch (IOException ioe) {
			Main.LOGGER.log(Level.SEVERE, "Unable to create gameserver", ioe);
		}
	}
	
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
			                        
			                        //System.out.println("hva er msg " + msg);
			                        if (msg != null) {
				                        if(msg.startsWith(Constants.GAMESTART)) {
				                        	//String tmp;
				                        	//tmp = Integer.toString(p.returnPlayerNr());
				                        	//System.out.println("Hvilken player:" + tmp);
				                        	//messages.put(msg + tmp);
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
				                        }
				                        if(msg.startsWith(Constants.DISCONNECT)) {
				                        	messages.put(msg);
				                        }
			                        }
		                        } catch (IOException ioe) {	// Unable to communicate with the client, remove it
		                        	i.remove();
		                            messages.put(LOGOUT+p.getName());
		                            messages.put(p.getName()+" got lost in hyperspace");
		                            Main.LOGGER.log(Level.WARNING, "Disconnected from server", ioe);
		                        }
		                    }
	                	}
	                } catch (InterruptedException ie) {
	                	Main.LOGGER.log(Level.SEVERE, "Synchronized function for player failed", ie);
	                } 
	              //The thread goes to sleep to save the CPU energy
					try {
						//Thread.sleep(250);
					} catch (Exception e) {
						Main.LOGGER.log(Level.WARNING, "Unable to sleep", e);
					}
	           }
	        });
	    }

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
		                        	messages.add(LOGOUT+p.getName());
		                        	messages.add(p.getName()+" got lost in hyperspace");
		                        	Main.LOGGER.log(Level.WARNING, p.getName() + " disconnected from server", ioe);
		                        }
		                    }
	                    }
	                } catch (InterruptedException ie) {
	                	Main.LOGGER.log(Level.SEVERE, "Synchronized function for player failed", ie);
	                }
	              //The thread goes to sleep to save the CPU energy
					try {
						//Thread.sleep(250);
					} catch (Exception e) {
						Main.LOGGER.log(Level.WARNING, "Unable to sleep", e);
					}
	            }
	        });
	    }

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
		                    	
		                    	Iterator<Player> i = player.iterator();
			                    while (i.hasNext()) {		// Send message to all clients that a new person has joined
			                    	Player t = i.next();
			                    	try {
			                    		System.out.println("\nGameServer->startLoginMonitor->Hva er name: " + t.getName());
				                    	p.sendText(Constants.JOIN + t.getName());
				                    	t.sendText(Constants.JOIN + p.getName());
			                    	} catch (IOException ioe) {
			                    		ioe.printStackTrace();
			                    	}
			                    } 
		                    }
	                    }
	                } catch (IOException ioe) {
	                	Main.LOGGER.log(Level.SEVERE, "Unable to setup socket", ioe);
	                }
	                
	              //The thread goes to sleep to save the CPU energy
					try {
						//Thread.sleep(250);
					} catch (Exception e) {
						Main.LOGGER.log(Level.WARNING, "Unable to sleep", e);
					}
	            }
	        });
	    }
	    
	    public void close() {
	    	executorService.shutdown();
	    }
}
