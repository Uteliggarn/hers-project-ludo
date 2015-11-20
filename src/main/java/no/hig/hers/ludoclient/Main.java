package no.hig.hers.ludoclient;
	
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Application;
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
	
	private static ChatHandler cHandler; 
	private static GameServer gameServer;
	
	static String LudoClientHost;
	static Socket connection;
	public static BufferedWriter output;
	public static BufferedReader input;

	
	
	public static int playerID;
	public static String userName;
	public static int serverPort = 10000;
	
	private static TabPane chatTabs;
	public static TabPane gameTabs;
	
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
			
			root = (Parent)FXMLLoader.load(getClass().getResource("ClientTempUI.fxml"));
			tempScene = new Scene(root);
			tempScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			StackPane mainRoot = (StackPane)FXMLLoader.load(getClass().getResource("ClientMainUI.fxml"));
			mainScene = new Scene(mainRoot);
			mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			chatTabs = (TabPane) ((AnchorPane) ((BorderPane) 
					mainRoot.getChildren().get(0)).getChildren().get(0)).getChildren().get(1);
					
			gameTabs = (TabPane) ((AnchorPane) ((BorderPane) 
					mainRoot.getChildren().get(0)).getChildren().get(0)).getChildren().get(0);
		
			
			
			
			//FXMLLoader loader = new FXMLLoader();
			
			//loader = loader.load(getClass().getResource("ClientMainUI.fxml").openStream());
			
			//clientMainUIController = (ClientMainUIController) loader.getController();
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static void startChatHandler() {
		// Making a new chathandler, which should handle the chats.
		cHandler = new ChatHandler(chatTabs, gameTabs);
	}

	public static void startGameServer() {
		gameServer = new GameServer(serverPort);
	}
	
	public static void showAlert(String title, String content) {
	   	Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
	
	
	
	public static void changeScene(Scene newScene) {
		currentStage.setScene(newScene);
	//	currentStage.setFullScreen(true);
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
}
