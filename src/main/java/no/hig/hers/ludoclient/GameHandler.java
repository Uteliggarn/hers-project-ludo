package no.hig.hers.ludoclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import no.hig.hers.ludoshared.Constants;

/**
 * GameHandler creates all the different lobbyController objects and game object and controller
 * createGameLobby, hostGameLobby and playerGameLobby
 * gameClientUI
 * The connection too gameserver is also set aswell as the connection loop
 * for proccessing messages from the gameserver
 */
public class GameHandler {
	
	private int serverPort;
	
	private Socket connection;
	private BufferedWriter output;
	private BufferedReader input;
	private ExecutorService executorService;
	
	private GameClientUIController gameClientUIController;

	private CreateGameLobbyController createGameLobbyController;
	private HostGameLobbyController hostGameLobbyController;
	private PlayerGameLobbyController playerGameLobbyController;
	
	private String hostName;
	private int caseNr = 0;
	private String ip;
	
	/**
	 * Makes the thread for proccessConnection
	 * And sets the connection information and who is hosting
	 * and the type iof lobby to be created
	 * @param serverPort Of the game host.
	 * @param ip Of the game host.
	 * @param caseNr Witch lobby type to be created.
	 * @param hostName Name of the hosting player.
	 */
	public GameHandler(int serverPort, String ip, int caseNr, String hostName) {
		this.serverPort = serverPort;
		this.hostName = hostName;
		this.caseNr = caseNr;
		this.ip = ip;
		
		executorService = Executors.newFixedThreadPool(1); // Lager et pool av threads for bruk
		
		connect();
		createNewLobby();
	}

	/**
	 * Gives the hostName of the user that is hosting the game
	 * @return hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Connects to the gameserver with the given ip and serverPort set in the
	 * constructor and sends if the user is the host and the userName from when
	 * you login
	 */
	public void connect() {
		try {
			connection = new Socket(ip, serverPort); // 128.39.83.87 // 127.0.0.1
			
			output = new BufferedWriter(new OutputStreamWriter(
                    connection.getOutputStream()));
			input = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
			
			if (Main.userName.equals(hostName))
				sendText("1" + Main.userName);
			else
				sendText("2" + Main.userName);
			
		} catch (UnknownHostException e) {
			Main.LOGGER.log(Level.SEVERE, "Error connecting server", e);
		} catch (IOException e) {
			Main.LOGGER.log(Level.WARNING, "Error making output/input", e);
		}
	}
	
	/**
     * Method used to send a message to the server. Handled in a separate method
     * to ensure that all messages are ended with a newline character and are
     * flushed (ensure they are sent.)
     * 
     * @param textToSend
     *            the message to send to the server
     */
    public void sendText(String textToSend) {
        try {
            output.write(textToSend);
            output.newLine();
            output.flush();
        } catch (IOException ioe) {
        	Main.LOGGER.log(Level.WARNING, "Unable to send message to server", ioe);
        }
    }
    
    /**
     * Starts an infinite loop to proccess all messages from the gameserver
     */
	public void processConnection() {
		executorService.execute(() -> {
			while (true) {
				try {
	                String msg = input.readLine();
	                System.out.println("\nHva er msg handler: " + msg);
	                
	                if (msg != null) {
		                if(msg.startsWith(Constants.GAMESTART)) {					// Makes the gameClientUIController if the message
		                	Platform.runLater(() -> {								// "gamestart" is recived from the gameserver.
		                		int n = Integer.parseInt(msg.substring(10, 11));	// and sets the connection to the game controller
	                															
	                			FXMLLoader loader = new FXMLLoader();
	                			try {
	                				System.out.print("Starter spill for " + n);
		                			for (int i=0; i<Main.gameTabs.getTabs().size(); i++) {
		                				if (Main.gameTabs.getTabs().get(i).getId().equals(hostName)) {
		                					Main.gameTabs.getTabs().get(i).setContent(
		                							loader.load(getClass().getResource("GameClient.fxml").openStream()));
		                					gameClientUIController = loader.getController();
			                				gameClientUIController.setConnetion(output, input);
			                				gameClientUIController.setPlayer(n);
			                				System.out.println("inne");
		                				}
		                				System.out.println("ute");
		                			}
	                			} catch (IOException e) {
	                				Main.LOGGER.log(Level.WARNING, "Unable to receive message from server", e);
	                			}	
		                	});
		                }
		                else if(msg.startsWith(Constants.GAMENAME)) {
							Platform.runLater(() -> {
								int n = Integer.parseInt(msg.substring(9, 10));
	                			System.out.println("playernamenr " + n);
	                			String tmpNavn = msg.substring(10, msg.length());
	                			gameClientUIController.setPlayerName(n, tmpNavn);    		
							});
		                }
		                else if(msg.startsWith(Constants.DICEVALUE)) {
							Platform.runLater(() -> {
								int diceVal = Integer.parseInt(msg.substring(10,11));
	                			int player = Integer.parseInt(msg.substring(11,12));
			                	int pawn = Integer.parseInt(msg.substring(12,13));
			                	System.out.println("diceval: " + diceVal + " player " + player + " pawn " + pawn);
			                	gameClientUIController.getDiceValue(diceVal, player, pawn);
		                	});
		                }
		                else if(msg.startsWith(Constants.DISCONNECT)) {
		                	Platform.runLater(() -> {
		                		int player = Integer.parseInt(msg.substring(11,12));
		                		gameClientUIController.setPlayerDisconnect(player);
		                	});
		                }
		                else if(msg.startsWith(Constants.GAMEOVER)) {
							Platform.runLater(() -> {
								gameClientUIController.gameover();	
		                	});
		                }
		                else if (msg.startsWith(Constants.JOIN)) {
		                	if (!msg.substring(5).equals(hostName.substring(4))) {
		                		Platform.runLater(() -> {
				                	switch (caseNr) {
				                	case 1:      					
				                		createGameLobbyController.joinedPlayer(msg.substring(5));
				                		break;
				                	case 2: 
										hostGameLobbyController.joinedPlayer(msg.substring(5));
				                		break;
				                	case 3: 
										playerGameLobbyController.joinedPlayer(msg.substring(5));	
				                		break;
				                	default: break;
				                	}
		                		});
		                	}
		                }
	                }   
	            } catch (IOException ioe) {
	            	Main.LOGGER.log(Level.WARNING, "Unable to receive message from server", ioe);
	            }
				//The thread goes to sleep to save the CPU energy
				try {
					Thread.sleep(250);
				} catch (Exception e) {
					Main.LOGGER.log(Level.WARNING, "Unable to sleep", e);
				}
	            
			}
		});
	}
	
	/**
	 * Makes an new tab to be added to the gameTabs in Main
	 * and setOnClosed on the tab to remove every object that is 
	 * in the tab, and sends gamelost to globalserver
	 * and disconnet to gameserver
	 */
	public void createNewLobby() {
		Platform.runLater(() -> {
		Tab tab = new Tab("Ludo");
		tab.setId(hostName);
		tab.setClosable(true);
		tab.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				String dcPlayer;
				int p, turnowner;
				if (gameClientUIController != null) {
					p = gameClientUIController.getPlayer();
					turnowner = gameClientUIController.getTurnOwner();
					dcPlayer = Integer.toString(gameClientUIController.getPlayer());
					if(p == turnowner) { //Check if player disconnected on his/her own turn
						sendText(Constants.DICEVALUE + 0 + p + 0);
					}
					sendText(Constants.DISCONNECT + dcPlayer);	//Disconnect the player and remove the player from the server
					Main.sendText(Constants.GAMELOST); 
						
				}
				
				Main.cHandler.leaveGameChat(hostName);
				String tmp = Constants.IDGK + Main.userName;
				if (tmp.equals(hostName)) {
					Main.sendText(Constants.REMOVEHOST + hostName);
					Main.mainController.openNewGameButton();
				}
				
				for(int i = 0; i < Main.gameTabs.getTabs().size(); i++) {
					if(Main.gameTabs.getTabs().get(i).getId().equals(hostName)) {
						Main.gameTabs.getTabs().remove(i);
					}
				}
				
				for (int i=0; i<Main.gameHandler.size(); i++) {
					if(hostName.equals(Main.gameHandler.get(i).getHostName())) {
						Main.gameHandler.remove(i);
					}
				}
			}
		});
		
		FXMLLoader loader = new FXMLLoader();
	
			try {
				switch (caseNr) {
				case 1:
					tab.setContent(loader.load(getClass().getResource("CreateGameLobby.fxml").openStream()));
					createGameLobbyController = (CreateGameLobbyController) loader.getController();
					createGameLobbyController.setHostPlayer(hostName);
					createGameLobbyController.setConnetion(output);
					break;
	
				case 2:
					tab.setContent(loader.load(getClass().getResource("HostGameLobby.fxml").openStream()));
					hostGameLobbyController = (HostGameLobbyController) loader.getController();
					hostGameLobbyController.setHostPlayer(hostName);
					hostGameLobbyController.setConnetion(output);
					break;
					
				case 3: 
					tab.setContent(loader.load(getClass().getResource("PlayerGameLobby.fxml").openStream()));
					playerGameLobbyController = (PlayerGameLobbyController) loader.getController();
					playerGameLobbyController.setHostPlayer(hostName);
					break;
					
				default: 
					break;
				}
		
				Main.gameTabs.getTabs().add(tab);
				Main.gameTabs.getSelectionModel().select(tab);
				
				processConnection();
			} catch (IOException ioe) {
				Main.LOGGER.log(Level.SEVERE, "Unable to find fxml file", ioe);
			}
		});
	}
	
	public void close() {
		try {
			executorService.shutdownNow();
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioe) {
			Main.LOGGER.log(Level.SEVERE, "Error closing GameHandler", ioe);
		} 
	}
	
	public boolean getCaseNr() {
		return caseNr == 1? true : false;
	}
	
	public void removePlayer(String name) {
		createGameLobbyController.removePlayerFromList(name);
	}
	
	public void addPlayer(String name) {
		createGameLobbyController.addPlayerToList(name);
	}
}
