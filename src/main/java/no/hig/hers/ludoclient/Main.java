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
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
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
	private static ArrayList<GameHandler> gameHandler = new ArrayList<>();
	
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
	final static String NEWCHAT = "NEWGROUPCHAT:";
	final static String JOINCHAT = "JOIN:";
	final static String ERRORCHAT = "ERRORCHAT";
	final static String LEAVECHAT = "LEAVE:";
	final static String CREATEGAME = "CREATEGAME";
	final static String IDGK = "IDGK";	// Unique name
	final static String INVITE = "INVITE:";	// Unique name
	final static String HOST = "HOST";	// Unique name
	final static String QUEUE = "QUEUE";	// Unique name
	final static String JOIN = "JOIN:";	// Unique name
	static final String QUITGAME = "LOGOUT:";
	
	static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);;

	@Override
	public void start(Stage primaryStage) {
		try {
			setupLogger();
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
			//connection = new Socket("128.39.83.87", 12344);

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
		processConnection(); // Starter en ny evighets tråd som tar seg av meldinger fra server
		executorService.shutdown();	// Dreper tråden når klassen dør
	}

	public static void startGameServer() {
		gameServer = new GameServer(serverPort);
		
		gameTabs.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
			if (newTab.getId().equals("main"))  
				requestTopTen();
		});
	}
	
	public static void requestTopTen() {
		//sendText("TOPWON");
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
	        Main.output.write(code + username);
	        Main.output.newLine();
	        Main.output.flush();
	        Main.output.write(code + password);
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
			while (true) {
				try {
	                message = Main.input.readLine();

	                if (message != null) {
		                if (message.equals(CREATEGAME)) {
		                	GameHandler gh = new GameHandler(serverPort, 1, Main.IDGK + Main.userName);
		                	gameHandler.add(gh);
		                } else if (message.equals(HOST)) {
		                	GameHandler gh = new GameHandler(serverPort, 2, Main.IDGK + Main.userName);
		                	gameHandler.add(gh);
		                } else if (message.startsWith(JOIN)) {
		                	int port = Integer.parseInt(Main.input.readLine());

		                	Platform.runLater(() -> {
		                		inviteAccept(port);
		                	});
		                } else if (message.startsWith(NEWCHAT))  //Legger til ny chatTab
            				mainController.addChatToList(message.substring(13));
    	                  else if (message.equals(ERRORCHAT)) 	// Forteller at chaten finnes allerede
    	                	Main.showAlert("Chat-room already exists", "Chat-room already exits");
    	                  else cHandler.handleChatMessage(message);
	                }
	                
	                Thread.sleep(250);
	            } catch (Exception e) {
	            	LOGGER.log(Level.WARNING, "Unable to receive message", e);	
	            }
			}
		});
	}
	
	private static void inviteAccept(int port) {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Game invite");
		alert.setHeaderText(null);
		alert.setContentText("Accept the invite or decline it");
		
		ButtonType buttonTypeAccept = new ButtonType("Accept");
		ButtonType buttonTypeDecline = new ButtonType("Decline", ButtonData.CANCEL_CLOSE);
		
		alert.getButtonTypes().setAll(buttonTypeAccept, buttonTypeDecline);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeAccept.OK){
			GameHandler gh = new GameHandler(port, 3, Main.IDGK + message.substring(5));
        	gameHandler.add(gh);
		}
	}
	
	private static void setupLogger() throws IOException {	
		LOGGER.setLevel(Level.WARNING);
		
		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		if (handlers[0] instanceof ConsoleHandler) {
			rootLogger.removeHandler(handlers[0]);
		}
		
		LOGGER.setLevel(Level.INFO);
		FileHandler fileTxt = new FileHandler("Logging.txt");

	    // create a TXT formatter
	    SimpleFormatter formatterTxt = new SimpleFormatter();
	    fileTxt.setFormatter(formatterTxt);
	    LOGGER.addHandler(fileTxt);
	}
}
