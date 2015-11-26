package no.hig.hers.ludoclient;
	
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import no.hig.hers.ludoshared.Constants;
import no.hig.hers.ludoshared.MyLogger;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	private static Stage currentStage;
	static Scene loginScene;
	static Scene registerScene;
	static Scene tempScene;
	static Scene mainScene;

	static ChatHandler cHandler; 
	static ArrayList<String> playerList = new ArrayList<>();
	private static GameServer gameServer;
	static ArrayList<GameHandler> gameHandler = new ArrayList<>();
	
	static String LudoClientHost;
	static Socket connection;
	static BufferedWriter output;
	static BufferedReader input;

	static int playerID;
	static String userName;
	static int serverPort = 10000;
	
	static TabPane gameTabs;
	private static TabPane chatTabs;
	private static ClientMainUIController mainController;
	
	static ExecutorService executorService;
	private static String message;
	
	static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	public void start(Stage primaryStage) {
		try {
			MyLogger.setupLogger();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Couldn't create log files", e);
		}
		setUpScenes();	
		primaryStage.setScene(loginScene);
		primaryStage.setTitle("Ludo");
		primaryStage.show();
		currentStage = primaryStage;
		
		connect();
	}
	
	public static void getPlayers() {
		for(int i = 0; i < playerList.size(); i++) {
			System.out.println(playerList.get(i));
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	/**
	 * Method for connecting to the server,
	 * with a Socket, and setting up BufferedReader
	 * and BufferedWriter for use.
	 */
	public static void connect() {
		try {
			//connection = new Socket("128.39.83.87", 12344);	// Henrik
			//connection = new Socket("128.39.80.117", 12344);	// Petter
			connection = new Socket("127.0.0.1", 12344);
			
			output = new BufferedWriter(new OutputStreamWriter(
                    connection.getOutputStream()));
			input = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
			
		} catch (UnknownHostException e) {
			Main.LOGGER.log(Level.SEVERE, "Error connecting to server", e);
			showAlert("Server down", "The server is currently down for maintenance");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Could not connect to server", e);
		}
	}
	/**
	 * Method for setting up the scenes and tabpanes,
	 * saving them and making them available.
	 */
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
			LOGGER.log(Level.SEVERE, "Unable to load scene files", e);
		}	
	}
	/**
	 * Method for creating a new ChatHandler,
	 * and start to listen for messages.
	 */
	public static void startChatHandler() {
		cHandler = new ChatHandler(chatTabs);
		
		executorService = Executors.newCachedThreadPool(); // Lager et pool av threads for bruk
		processConnection(); // Starter en ny evighets tr�d som tar seg av meldinger fra server
		executorService.shutdown();	// Dreper tr�den n�r klassen d�r
	}

	public static void startGameServer() {
		gameServer = new GameServer(serverPort);
		
		gameTabs.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
			if (newTab.getId().equals("main"))  
				requestTopTen();
		});
	}
	
	public static void requestTopTen() {
		sendText(Constants.PLAYERMESSAGE + Constants.TOP);
		// + userName + 
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
		
		if (newScene.equals(mainScene)) requestTopTen();
	}

	/**
	 * Method for sending the login info to the server.
	 * Run from the login screen (ClientLoginUIController)
	 * @param code the LoginCode.
	 * @param username the username to send
	 * @param password the password to send
	 */
	public static void sendLogin(String code, String username, String password) {
		try {
			mainController.setLabelUserName(username);
			
			Main.output.write(code);
			Main.output.newLine();
	        Main.output.flush();
	        
	        Main.output.write(username);
	        Main.output.newLine();
	        Main.output.flush();
	        Main.output.write(password);
	        Main.output.newLine();
	        Main.output.flush(); 
	    } catch (IOException ioe) {
	    	LOGGER.log(Level.WARNING, "Unable to send login", ioe);		
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
        	System.out.println("SENT " + textToSend);
            output.write(textToSend);
            output.newLine();
            output.flush();
        } catch (IOException ioe) {
        	LOGGER.log(Level.WARNING, "Unable to send message to server", ioe);	
        }
    }
	/**
	 * Method for stopping the client,
	 * sending a message of logout to the server.
	 */
	@Override
	public void stop() {
		sendText(">>>LOGOUT<<<");	//Sender melding til serveren om logout
		close();
	}
	
	public void close() {
		System.exit(0);
	}
	/**
	 * Method for processing messages from the server,	
	 * sending the messages to the GameHandler or ChatHandler
	 */
	private static void processConnection() {
		executorService.execute(() -> {
			boolean serverOnline = true;
			while (serverOnline) {
				try {
	                message = Main.input.readLine();
	                
	                if (message != null) {
	                	if (message.startsWith(Constants.CREATEGAME)) {
	                		
		                	GameHandler gh = new GameHandler(serverPort, message.substring(11), 1, Constants.IDGK + Main.userName);
		                	gameHandler.add(gh);
		                }
		                else if (message.startsWith(Constants.HOST)) {
		                	GameHandler gh = new GameHandler(serverPort, message.substring(5), 2, Constants.IDGK + Main.userName);
		                	gameHandler.add(gh);
		                }
		                else if (message.startsWith(Constants.HOTJOIN)) {
		                	String tmp = Main.input.readLine();
		                	int port = Integer.valueOf(tmp.substring(0, 5));
		                	String ip = tmp.substring(5);
		                	GameHandler gh = new GameHandler(port, ip, 3, Constants.IDGK + message.substring(8));
		                	gameHandler.add(gh);

		                	Platform.runLater(() -> {
		                		mainController.openQueue();
		                	});
		                }
		                else if (message.startsWith(Constants.JOIN)) {
		                	String tmp = Main.input.readLine();
		                	int port = Integer.valueOf(tmp.substring(0, 5));
		                	String ip = tmp.substring(5);
		                	Platform.runLater(() -> {
		                		inviteAccept(port, ip);
		                	});
		                }
		                else if (message.startsWith(Constants.TOPPLAYED)) {
		                	String[][] played = new String[10][2];
		                	played[0][0] = message.substring(message.lastIndexOf(":") + 1, message.lastIndexOf(","));
		                	played[0][1] = message.substring(message.lastIndexOf(",") + 1, message.length());
		                	for (int i = 1; i < 10; i++) {
		                		message = Main.input.readLine();
		                		played[i][0] = message.substring(message.lastIndexOf(":") + 1, message.lastIndexOf(","));
		                		played[i][1] = message.substring(message.lastIndexOf(",") + 1, message.length());
		                	}
		                	mainController.setTopTenPlayed(played);
		                	
		                	System.out.println("Received top ten");
		                	
		                }
		                else if (message.startsWith(Constants.TOPWON)) {
		                	String[][] won = new String[10][2];
		                	won[0][0] = message.substring(message.lastIndexOf(":") + 1, message.lastIndexOf(","));
		                	won[0][1] = message.substring(message.lastIndexOf(",") + 1, message.length());
		                	for (int i = 1; i < 10; i++) {
		                		message = Main.input.readLine();
		                		won[i][0] = message.substring(message.lastIndexOf(":") + 1, message.lastIndexOf(","));
		                		won[i][1] = message.substring(message.lastIndexOf(",") + 1, message.length());
		                	}
		                	mainController.setTopTenWon(won);
		                }
	                	
		                else if (message.startsWith(Constants.CHATMESSAGE)) {
		                	String msg = message.substring(Constants.CHATMESSAGE.length());
		                	if (msg.startsWith(Constants.NEWCHAT)) 
		                		mainController.addChatToList(msg.substring(Constants.NEWCHAT.length()));
		                	else cHandler.handleChatMessage2(msg);
		                }
		                else if (message.startsWith(Constants.PLAYERMESSAGE)) {
		                	
		                }

    	                 else if (message.equals(Constants.ERRORCHAT)) 	// Forteller at chaten finnes allerede
    	                	Main.showAlert("Chat-room already exists", "Chat-room already exits");
    	                 else cHandler.handleChatMessage(message);
	                }
	            } catch (Exception e) {
	            	LOGGER.log(Level.SEVERE, "Unable to receive message, server down?", e);
	            	serverOnline = false;
	            	Platform.runLater(() -> {
	            		showAlert("Server is down", "The server is currently down.\nPlease try again later");
	            		System.exit(1);
	            	});
	            	
	            }
			}
		});
	}
	
	private static void inviteAccept(int port, String ip) {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Game invite");
		alert.setHeaderText(null);
		alert.setContentText("Accept the invite or decline it");
		
		ButtonType buttonTypeAccept = new ButtonType("Accept");
		ButtonType buttonTypeDecline = new ButtonType("Decline", ButtonData.CANCEL_CLOSE);
		
		alert.getButtonTypes().setAll(buttonTypeAccept, buttonTypeDecline);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeAccept){
			GameHandler gh = new GameHandler(port, ip, 3, Constants.IDGK + message.substring(5));
        	gameHandler.add(gh);
		}
	}
	

}
