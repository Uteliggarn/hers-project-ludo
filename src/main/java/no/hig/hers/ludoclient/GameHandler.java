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

import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import no.hig.hers.ludoshared.Constants;

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
	
	private final String JOIN = "JOIN:";
	
	public GameHandler(int serverPort, int caseNr, String hostName) {
		this.serverPort = serverPort;
		this.hostName = hostName;
		this.caseNr = caseNr;
		
		executorService = Executors.newCachedThreadPool(); // Lager et pool av threads for bruk
		
		connect();
		createNewLobby();
		
		if (caseNr == 1)
			addPlayersToList();
		
		//executorService.shutdown();	// Dreper tråden når klassen dør
	}
	
	
	private void addPlayersToList() {		// TODO: this needs fixing
		executorService.execute(() -> {
			while (true) {
				Platform.runLater(() -> {
					for (int i=0; i<Main.playerList.size(); i++) {
						if (Main.playerList.get(i) != Main.userName)
							createGameLobbyController.addNewPlayerToList(Main.playerList.get(i));
					}
				});
				//The thread goes to sleep to save the CPU energy
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					Main.LOGGER.log(Level.WARNING, "Error sleeping", e);
				}
			}
		});
	}	
	
	public void connect() {
		try {			
			//connection = new Socket("128.39.83.87", serverPort); // 128.39.83.87 // 127.0.0.1
			connection = new Socket("127.0.0.1", serverPort); // 128.39.83.87 // 127.0.0.1
			
			output = new BufferedWriter(new OutputStreamWriter(
                    connection.getOutputStream()));
			input = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
			
			sendText(Main.userName);
			
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
    
	public void processConnection() {
		executorService.execute(() -> {
			while (true) {
				try {
	                String msg = input.readLine();
	                System.out.println("\nHva er msg handler: " + msg);
	                
	                if (msg != null) {
		                if(msg.startsWith(Constants.GAMESTART)) {
		                	Platform.runLater(() -> {
		                		int n = Integer.parseInt(msg.substring(10, 11));
	                			
	                			FXMLLoader loader = new FXMLLoader();
	                			try {
	                				System.out.print("Starter spill for " + n);
		                			for (int i=0; i<Main.gameTabs.getTabs().size(); i++) {
		                				if (Main.gameTabs.getTabs().get(i).getId() == hostName) {
		                					Main.gameTabs.getTabs().get(i).setContent(
		                							loader.load(getClass().getResource("GameClient.fxml").openStream()));
		                					gameClientUIController = loader.getController();
			                				gameClientUIController.setConnetion(output, input);
			                				gameClientUIController.setPlayer(n);
		                				}	
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
		                else if(msg.startsWith(Constants.GAMEOVER)) {
							Platform.runLater(() -> {
								gameClientUIController.gameover();	
		                	});
		                }
		                else if (msg.startsWith(JOIN)) {
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
	

	public void createNewLobby() {
		Platform.runLater(() -> {
		Tab tab = new Tab("Ludo");
		tab.setId(hostName);
		tab.setClosable(true);
		//tab.setOnClosed(EventHandler<Event>() -> {
			
		//});
		
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

	public String read() throws IOException {
		if (input.ready())
			return input.readLine();
		return null;
	}
	
	public void close() {
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioe) {
			Main.LOGGER.log(Level.SEVERE, "Error closing GameHandler", ioe);
		} 
	}
	
}
