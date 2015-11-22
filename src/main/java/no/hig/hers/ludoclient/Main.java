package no.hig.hers.ludoclient;
	
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	public static Stage currentStage;
	public static Scene loginScene;
	public static Scene registerScene;
	public static Scene tempScene;
	public static Scene mainScene;
	
	static boolean connected = false;
	
	static ChatHandler cHandler; 
	private static GameServer gameServer;
	private static ArrayList<GameHandler> gameHandler = new ArrayList<>();
	public static ArrayList<String> playerList = new ArrayList<>();
	
	static String LudoClientHost;
	static Socket connection;
	public static BufferedWriter output;
	public static BufferedReader input;

	public static int playerID;
	public static String userName;
	public static int serverPort = 10000;
	
	private static TabPane chatTabs;
	public static TabPane gameTabs;
	private static ClientMainUIController mainController;
	
	static ExecutorService executorService;
	private static String message;
	final static String NEWCHAT = "NEWGROUPCHAT:";
	final static String JOINCHAT = "JOIN:";
	final static String ERRORCHAT = "ERRORCHAT";
	final static String LEAVECHAT = "OUT:";
	final static String CREATEGAME = "CREATEGAME";
	final static String IDGK = "IDGK";	// Unique name

	@Override
	public void start(Stage primaryStage) {
	
		
		try {			
			setUpScenes();	
			
			primaryStage.setScene(loginScene);
			
			primaryStage.setTitle("Ludo");
			
			primaryStage.show();
			
			currentStage = primaryStage;
			
			connect();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static void connect() {
		try {
			connection = new Socket("127.0.0.1", 12344);
			
			output = new BufferedWriter(new OutputStreamWriter(
                    connection.getOutputStream()));
			input = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showAlert("Server down", "The server is currently down for maintenance");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setUpScenes() {
		try {
			Parent root = (Parent)FXMLLoader.load(getClass().getResource("ClientLoginUI.fxml"));
			loginScene = new Scene(root);
			loginScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			root = (Parent)FXMLLoader.load(getClass().getResource("ClientRegisterUI.fxml"));
			registerScene = new Scene(root);
			registerScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			FXMLLoader loader = new FXMLLoader();
			StackPane mainRoot = (StackPane)loader.load(getClass().getResource("ClientMainUI.fxml").openStream());
			mainScene = new Scene(mainRoot);
			mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			mainController = (ClientMainUIController) loader.getController();
			
			chatTabs = (TabPane) ((AnchorPane) ((BorderPane) 
					mainRoot.getChildren().get(0)).getChildren().get(0)).getChildren().get(1);
					
			gameTabs = (TabPane) ((AnchorPane) ((BorderPane) 
					mainRoot.getChildren().get(0)).getChildren().get(0)).getChildren().get(0);
		
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}
	/**
	 * Method for creating a new ChatHandler,
	 * and start to listen for messages.
	 */
	public static void startChatHandler() {
		cHandler = new ChatHandler(chatTabs, gameTabs);
		
		executorService = Executors.newCachedThreadPool(); // Lager et pool av threads for bruk
		processConnection(); // Starter en ny evighets tråd som tar seg av meldinger fra server
		executorService.shutdown();	// Dreper tråden når klassen dør
	}

	public static void startGameServer() {
		gameServer = new GameServer(serverPort);
	}
	/**
	 * Method for showing alerts to the user.
	 * Just for simple error messages.
	 * @param title The title of the alert
	 * @param content the content in the alert
	 */
	public static void showAlert(String title, String content) {
	   	Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
	/**
	 * Method for changing the scene.
	 * Called from the Controllers / scenes.
	 * @param newScene The new scene to load.
	 */
	public static void changeScene(Scene newScene) {
		currentStage.setScene(newScene);
	}

	public static void sendLogin(String code, String username, String password) {
		try {
	        Main.output.write(code + username);
	        Main.output.newLine();
	        Main.output.flush();
	        Main.output.write(code + password);
	        Main.output.newLine();
	        Main.output.flush(); 
	    } catch (IOException ioe) {
	    	showAlert("Error sending message", ioe.toString());		
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
	
	@Override
	public void stop() {
		sendText(">>>LOGOUT<<<");	//Sender melding til serveren om logout
		close();
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
	/**
	 * Kopiert mer eller mindre fra den vi hadde på forrige prosjekt.
	 * Ser mer eller mindre ut til å fungere.
	 */
	private static void processConnection() {
		executorService.execute(() -> {
			while (true) {
				try {
	                message = Main.input.readLine();
	
	                if (message.equals("HOST")) {
	                	Platform.runLater(new Runnable() {
	                		@Override
	                		public void run() {
	                			//gameHandler.newHostGameLobby();	
	                		}
	                	});
	               
	                }
	                else if (message.equals(CREATEGAME)) {
	                	GameHandler gh = new GameHandler(serverPort, 1);
	                	gameHandler.add(gh);
	                }
	                else if (message.equals("JOIN")) {
	                	int port = Main.input.read();
	                	//GameLobby gameLobby = new GameLobby(port);	                	
	                }
	                
	                if (!message.equals(null)) {
                			if (message.startsWith(NEWCHAT)) { //Legger til ny chatTab
                				mainController.addChatToList(message.substring(13));
        	                }
        	                else if (message.equals(ERRORCHAT)) {	// Forteller at chaten finnes allerede
        	                	Main.showAlert("Chat-room already exists", "Chat-room already exits");
        	                }
        	                else cHandler.handleChatMessage(message);
	                }

	            } catch (Exception e) {
	             //   Main.showAlert("Error", "Error receiving message from server");
	            }
				
				try {
					Thread.sleep(250);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
