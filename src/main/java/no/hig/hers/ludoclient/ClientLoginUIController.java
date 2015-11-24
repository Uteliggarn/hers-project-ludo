package no.hig.hers.ludoclient;

import java.io.IOException;

import javax.swing.JOptionPane;

import no.hig.hers.ludoclient.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import no.hig.hers.ludoclient.Main;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ClientLoginUIController {

    @FXML
    private Button registerButton;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private TextField usernameTextField;
    
    @FXML
    private Label labelConnectionStatus;
    
    /**
     * Method that logs in the user, and stores the ID
     * and username for further use.
     * If successful, starts the connection, chat handler,
     * and prepares a gameserver. 
     * @param event
     */
    @FXML
    void userLogin(ActionEvent event) {
    	String username = usernameTextField.getText();
    	String password = passwordTextField.getText();

    	int ID;
    	String port = null;
 
    	Main.sendLogin("SENDLOGIN:", username, password);
    	try {
    		ID = Integer.valueOf(Main.input.readLine());
    		
    		if (ID == 0)
    			Main.connect();
    		
			if (ID > 0) {	
				Main.serverPort = Integer.valueOf(Main.input.readLine());
				
				Main.userName = username;
				Main.playerID = ID;
				Main.startChatHandler();
				Main.startGameServer();
				Main.changeScene(Main.mainScene);
			}
			else Main.showAlert("User not found", 
						"Wrong username and/or password.\nPlease try again, or register a new user.");
			
			//Main.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML
    void userRegister(ActionEvent event) {
    	Main.changeScene(Main.registerScene);	
    }
    
    

}
