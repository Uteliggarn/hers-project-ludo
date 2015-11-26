package no.hig.hers.ludoclient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;

public class TempGameClient extends Application {

	
	LudoBoardFX board; 
	
	public static void main(String[] args) {
		launch(args);
	
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
	
		
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
