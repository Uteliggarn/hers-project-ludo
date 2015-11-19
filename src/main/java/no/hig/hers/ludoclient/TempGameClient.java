package no.hig.hers.ludoclient;

import java.awt.BorderLayout;

import javax.swing.SwingUtilities;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.*;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;

public class TempGameClient extends Application {

	
	LudoBoardFX board; 
	
	public static void main(String[] args) {
		launch(args);
	
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		final SwingNode swingNode = new SwingNode();
		
		Parent root = (Parent)FXMLLoader.load(getClass().getResource("GameClient.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}

	 @FXML
	 void goToMain(ActionEvent event) {
	   	Main.changeScene(Main.mainScene);
    }
}
