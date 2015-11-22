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

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;

public class GameHandler {
	
	private static int serverPort;
	
	static Socket connection;
	public static BufferedWriter output;
	public static BufferedReader input;
	private ExecutorService executorService;
	
	private GameClientUIController gameClientUIController;
	
	public GameHandler(int serverPort) {
		this.serverPort = serverPort;
		
		connect();
		
		executorService = Executors.newCachedThreadPool(); // Lager et pool av threads for bruk
		processConnection(); // Starter en ny evighets tråd som tar seg av meldinger fra server
		executorService.shutdown();	// Dreper tråden når klassen dør
	}
	
	
	public static void connect() {
		try {
			connection = new Socket("127.0.0.1", 13333);
			
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
	                if(msg != null && msg.startsWith("diceValue:")) {
	                	int diceVal = Integer.parseInt(msg.substring(11,11));
	                	int player = Integer.parseInt(msg.substring(12,12));
	                	int pawn = Integer.parseInt(msg.substring(13,13));
	                	gameClientUIController.getDiceValue(diceVal, player, pawn);
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
	
}
