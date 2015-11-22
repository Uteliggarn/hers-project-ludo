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
	
	public GameHandler(int serverPort, int type) {
		this.serverPort = serverPort;
		
		switch (type) {
			case 1: Platform.runLater(new Runnable() {
	    				@Override
	    				public void run() {
							Tab tab = new Tab("Ludo");
							tab.setId(Main.IDGK + Main.userName);
							FXMLLoader loader = new FXMLLoader();
							try {
								tab.setContent(loader.load(getClass().getResource("CreateGameLobby.fxml").openStream()));
								createGameLobbyController = (CreateGameLobbyController) loader.getController();
								
								addPlayersToList();
								
								Main.gameTabs.getTabs().add(tab);
								Main.gameTabs.getSelectionModel().select(tab);
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
	    				}
					});
				break;
			case 2: 
				break;
			case 3:
				break;
		}
		
		//connect();
		
		//executorService = Executors.newCachedThreadPool(); // Lager et pool av threads for bruk
		//processConnection(); // Starter en ny evighets tråd som tar seg av meldinger fra server
		//executorService.shutdown();	// Dreper tråden når klassen dør
	}
	
	
	private void addPlayersToList() {
		for (int i=0; i<Main.playerList.size(); i++) {
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
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
