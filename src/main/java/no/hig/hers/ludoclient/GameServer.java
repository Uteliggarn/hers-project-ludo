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

public class GameServer {
	
	private ServerSocket server;
	private ExecutorService executorService;
	
	private ArrayList<Player> player = new ArrayList<Player>();
	
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
			ioe.printStackTrace();
			System.exit(1);
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
			                        
			                        if(msg != null && msg.startsWith("gamestart:")) {
			                        	String tmp;
			                        	tmp = Integer.toString(p.returnPlayerNr());
			                        	messages.put(msg + tmp);
			                        }
			                        if(msg != null && msg.startsWith("dicevalue:")) {
			                        	messages.put(msg);
			                        }
			                   
		                        } catch (IOException ioe) {	// Unable to communicate with the client, remove it
		                        	i.remove();
		                            messages.put(LOGOUT+p.returnName());
		                            messages.put(p.returnName()+" got lost in hyperspace");
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
		                        	messages.add(LOGOUT+p.returnName());
		                        	messages.add(p.returnName()+" got lost in hyperspace");
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

	    private void startLoginMonitor() {
	        executorService.execute(() -> {
	            while (!shutdown) {
	                try {
	                    Socket s = server.accept();
	                    
	                    if (player.size() != 4) {
	                    	
		                    Player p = new Player(s, playerNr++);
		                    
		                    /*
		                    try {
								//displayMessage("GlobalJOIN:" + p.returnName() + "\n");
	                    		for (int t=0; t<player.size(); t++) {
									p.sendText(JOIN + player.get(t).returnName());
								}
	                    		
								messages.put(JOIN + p.returnName());
								
							} catch (InterruptedException ie) {
								ie.printStackTrace();
							}
		                    */
		                    synchronized (player) {
		                    	player.add(p);     	
		                    	
		                    	try {
		                    		messages.put(JOIN + p.returnName());
		                    	} catch (InterruptedException ie) {
		                    		ie.printStackTrace();
		                    	}
		                    	
		                    	Iterator<Player> i = player.iterator();
			                    while (i.hasNext()) {		// Send message to all clients that a new person has joined
			                        Player p1 = i.next();
			                        if (p != p1)
			                        	try {
			                        		System.out.println("\n Synchronized i loginMonitor: " + p1.returnName());
			                        		p.sendText(JOIN + p1.returnName());
			                        	} catch (IOException ioelocal) {
			                        		// Lost connection, but doesn't bother to handle it here
			                        	}
			                    }
			                    
		                    }
	                    }
	                } catch (IOException ioe) {
	                    ioe.printStackTrace();
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
	    
	    public void close() {
	    	executorService.shutdown();
	    }
}
