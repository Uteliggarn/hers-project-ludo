package no.hig.hers.ludoclient;
	
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javafx.scene.layout.HBox;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	public static Stage currentStage;
	public static Scene loginScene;
	public static Scene registerScene;
	public static Scene tempScene;
	public static Scene mainScene;
	
	static boolean connected = false;

	
	static String LudoClientHost;
	static Socket connection;
	public static BufferedWriter output;
	public static BufferedReader input;
	
	public static int playerID;
	
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
			connection = new Socket("127.0.0.1", 12347);
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
			
			
			
			
			HBox mainRoot = (HBox)FXMLLoader.load(getClass().getResource("ClientMainUI.fxml"));
			
			TabPane chatTabs = ((TabPane) ((AnchorPane) ((StackPane)mainRoot.getChildren().get(0)).getChildren().get(0)).getChildren().get(1));
		
			//TabPane chatTabs = ((TabPane) (((AnchorPane) (StackPane) mainRoot.getChildren().get(0)).getChildren().get(0)).getChildren().get(1));
			
			Tab globalChatTab = new Tab("Global");					
			
			chatTabs.getTabs().add(globalChatTab);
			globalChatTab.setContent((Node) FXMLLoader.load(this.getClass().getResource("ClientChatOverLay.fxml")));
			
			
			mainScene = new Scene(mainRoot);
			mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			
			
		//	TabPane chatHolder = new TabPane();
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
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
}
