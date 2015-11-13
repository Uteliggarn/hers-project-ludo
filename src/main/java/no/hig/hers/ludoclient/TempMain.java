package no.hig.hers.ludoclient;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

// fx:controller="no.hig.hers.ludoclient.MainWindowController"

public class TempMain extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		BorderPane root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
		
		Scene scene = new Scene(root);
		stage.setTitle("Main");
		stage.setScene(scene);
		stage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
