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
	
	private CreateGameLobbyController createGameLobbyController;
	private HostGameLobbyController hostGameLobbyController;
	private PlayerGameLobbyController playerGameLobbyController;
	
	private String tabName;
	
	public GameHandler(int serverPort, int type, String hostName) {
		this.serverPort = serverPort;
		
		switch (type) {
			case 1: Platform.runLater(new Runnable() {	//Create
	    				@Override
	    				public void run() {
							Tab tab = new Tab("Ludo");
							tab.setId(Main.IDGK + Main.userName);
							tabName = Main.IDGK + Main.userName;
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
							Tab tab = new Tab("Ludo");
							tab.setId(Main.IDGK + Main.userName);
							tabName = Main.IDGK + Main.userName;
							FXMLLoader loader = new FXMLLoader();
							try {
								tab.setContent(loader.load(getClass().getResource("HostGameLobby.fxml").openStream()));
								hostGameLobbyController = (HostGameLobbyController) loader.getController();
								
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
							Tab tab = new Tab("Ludo");
							tab.setId(hostName);
							tabName = hostName;
							FXMLLoader loader = new FXMLLoader();
							try {
								tab.setContent(loader.load(getClass().getResource("PlayerGameLobby.fxml").openStream()));
								playerGameLobbyController = (PlayerGameLobbyController) loader.getController();
								
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
		executorService.shutdown();	// Dreper tråden når klassen dør
	}
	
	
	private void addPlayersToList() {
		for (int i=0; i<Main.playerList.size(); i++) {
			if (!Main.playerList.get(i).equals(Main.userName))
				createGameLobbyController.addNewPlayerToList(Main.playerList.get(i));
		}
	}
	
	public static void connect() {
		try {
			System.out.println("\nHva er connect: " + serverPort);
			
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
	                String tmp = input.readLine();
	                        	                
	                
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

}
