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

import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;

public class GameHandler {
	
	private static int serverPort;
	
	static Socket connection;
	public static BufferedWriter output;
	public static BufferedReader input;
	private ExecutorService executorService;
	
	private GameClientUIController gameClientUIController;

	private CreateGameLobbyController createGameLobbyController;
	private HostGameLobbyController hostGameLobbyController;
	private PlayerGameLobbyController playerGameLobbyController;
	
	private String tabName;
	private int caseNr = 0;
	
	private final String JOIN = "JOIN:";
	
	public GameHandler(int serverPort, int type, String hostName) {
		this.serverPort = serverPort;
		this.tabName = hostName;
		
		switch (type) {
			case 1: Platform.runLater(new Runnable() {	//Create
	    				@Override
	    				public void run() {
	    					caseNr = 1;
							Tab tab = new Tab("Ludo");
							tab.setId(Main.IDGK + Main.userName);
						
							FXMLLoader loader = new FXMLLoader();
							try {
								tab.setContent(loader.load(getClass().getResource("CreateGameLobby.fxml").openStream()));
								createGameLobbyController = (CreateGameLobbyController) loader.getController();
								
								addPlayersToList();
								
								Main.gameTabs.getTabs().add(tab);
								Main.gameTabs.getSelectionModel().select(tab);
								
								connect();
								
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
	    				}
					});
				break;
			case 2: Platform.runLater(new Runnable() {	// Host
						@Override
						public void run() {
							caseNr = 2;
							Tab tab = new Tab("Ludo");
							tab.setId(Main.IDGK + Main.userName);
							
							FXMLLoader loader = new FXMLLoader();
							try {
								tab.setContent(loader.load(getClass().getResource("HostGameLobby.fxml").openStream()));
								hostGameLobbyController = (HostGameLobbyController) loader.getController();
								connect();
								hostGameLobbyController.setConnetion(output, input);
								Main.gameTabs.getTabs().add(tab);
								Main.gameTabs.getSelectionModel().select(tab);
								
								connect();
								
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
						}
			});
				break;
			case 3: Platform.runLater(new Runnable() {	//Player
						@Override
						public void run() {
							caseNr = 3;
							Tab tab = new Tab("Ludo");
							tab.setId(hostName);
							
							FXMLLoader loader = new FXMLLoader();
							try {
								tab.setContent(loader.load(getClass().getResource("PlayerGameLobby.fxml").openStream()));
								playerGameLobbyController = (PlayerGameLobbyController) loader.getController();
								connect();
								//playerGameLobbyController.setConnetion(output, input);
								Main.gameTabs.getTabs().add(tab);
								Main.gameTabs.getSelectionModel().select(tab);
								
								connect();
								
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
						}
			});
				break;
		}
		
		//connect();
		
		executorService = Executors.newCachedThreadPool(); // Lager et pool av threads for bruk
		processConnection(); // Starter en ny evighets tråd som tar seg av meldinger fra server
		System.out.println("Hei=?");
		executorService.shutdown();	// Dreper tråden når klassen dør
		close();
	}
	
	
	private void addPlayersToList() {
		for (int i=0; i<Main.playerList.size(); i++) {
			if (!Main.playerList.get(i).equals(Main.userName))
				createGameLobbyController.addNewPlayerToList(Main.playerList.get(i));
		}
	}
	
	public static void connect() {
		try {			
			connection = new Socket("127.0.0.1", serverPort);
			
			output = new BufferedWriter(new OutputStreamWriter(
                    connection.getOutputStream()));
			input = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
			
			sendText(Main.userName);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    public static void sendText(String textToSend) {
        try {
            output.write(textToSend);
            output.newLine();
            output.flush();
        } catch (IOException ioe) {
        	Main.showAlert("Error", "Unable to send message to server");
        }
    }
	
	private void processConnection() {
		executorService.execute(() -> {
			while (true) {
				try {
					
	                String msg = input.readLine();
	                
	                if(msg != null && msg.startsWith("gamestart:")) {
	                	int n = Integer.parseInt(msg.substring(11, 11));
	                	
	                	Tab tab = Main.gameTabs.getTabs().get(1);
	            		FXMLLoader loader = new FXMLLoader();
	            		try {
	            			tab.setContent(loader.load(getClass().getResource("GameClient.fxml").openStream()));
	            			gameClientUIController.setConnetion(output, input);
	            			gameClientUIController.setPlayer(n);
	            		} catch (IOException e1) {
	            			// TODO Auto-generated catch block
	            			e1.printStackTrace();
	            		}	
	                }
	                else if(msg != null && msg.startsWith("diceValue:")) {
	                	int diceVal = Integer.parseInt(msg.substring(11,11));
	                	int player = Integer.parseInt(msg.substring(12,12));
	                	int pawn = Integer.parseInt(msg.substring(13,13));
	                	gameClientUIController.getDiceValue(diceVal, player, pawn);
	                }
	                else if (msg != null && msg.startsWith(JOIN)) {
	                	switch (caseNr) {
	                	case 1: createGameLobbyController.joinedPlayer(msg.substring(5));
	                		break;
	                	case 2: hostGameLobbyController.joinedPlayer(msg.substring(5));
	                		break;
	                	case 3: playerGameLobbyController.joinedPlayer(msg.substring(5));
	                		break;
	                	}
	                }
                    	        	                
	                
	            } catch (IOException ioe) {
	                ioe.printStackTrace();
	            }
	            
			}
		});
	}
	
	public void newHostGameLobby() {
		try {
			Tab tab = new Tab("Ludo");
			
			FXMLLoader loader = new FXMLLoader();
			
			tab.setContent(loader.load(getClass().getResource("HostGameLobby.fxml").openStream()));
			
			//HostGameLobbyController hostGameLobbyController = (HostGameLobbyController) loader.getController();
			
			//hostGameLobbyController.getServerPort(Main.serverPort);
			
			tab.setId("tab1");
			
			Main.gameTabs.getTabs().add(tab);
			Main.gameTabs.getSelectionModel().select(tab);
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
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
			ioe.printStackTrace();
		} 
	}
	
}
