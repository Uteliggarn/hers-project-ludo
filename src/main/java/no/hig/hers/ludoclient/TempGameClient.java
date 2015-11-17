package no.hig.hers.ludoclient;

import java.awt.BorderLayout;

import javax.swing.SwingUtilities;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.*;
import javafx.embed.swing.SwingNode;

public class TempGameClient extends Application {

	LudoBoard board; 
	
	public static void main(String[] args) {
		launch(args);
	
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		//final SwingNode swingNode = new SwingNode();
		
		Parent root = (Parent)FXMLLoader.load(getClass().getResource("GameClient.fxml"));
		BorderPane mainScene = new BorderPane(); 
		mainScene.setCenter(root);;
				
		Scene scene = new Scene(mainScene);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}

	 private void createSwingContent(final SwingNode swingNode) {
	        SwingUtilities.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	            	board = new LudoBoard();
	                swingNode.setContent(board);
	            }
	        });
	    }
}
