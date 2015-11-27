package no.hig.hers.ludoclient;

import java.util.logging.Level;

import javafx.fxml.FXML;
import no.hig.hers.ludoclient.Main;
import no.hig.hers.ludoshared.Constants;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Handles the controls of the register GUI
 * Where the new username and passord is sent
 * and the stage change back to login GUI
 */
public class ClientRegisterUIController {
    @FXML
    private PasswordField passwordTextField;

    @FXML
    private PasswordField confirmTextField;

    @FXML
    private TextField usernameTextField;

    /**
     * Sends the user back to the login stage GUI
     */
    @FXML
    void cancelRegistration() {
    	Main.changeScene(Main.loginScene);
    }

    /**
     * promts the user to type in a username and passord and sends
     * it to the server to be confirmed
     * and return the user back to the login GUI if the server
     * accpets the new user request
     */
    @FXML
    void registerUser() {
    	String username = usernameTextField.getText();
    	String password = passwordTextField.getText();
    	String confirm = confirmTextField.getText();
    	
    	if (password.length() >= 6 && password.length() <= 10) {
			if (password.equals(confirm)) {
				try {
					Main.sendLogin(Constants.SENDREGISTER, username, password);
					if(Main.input.readLine().equals(Constants.ACCEPTED)) {
						Main.showAlert("User successfully created", "Congratulations, you have successfully created a new user.");
						Main.changeScene(Main.loginScene);
						Main.connect();
					}	
					else Main.showAlert("User already exists", "Sorry, that username is already taken.\nPlease select another.");
				} catch (Exception e) {
					Main.LOGGER.log(Level.SEVERE, "Can't connect to server", e);
				}
			} else Main.showAlert("Password mistyped", "The passwords do not match.\nPlease try again.");
    	} else Main.showAlert("Too short/long password", "Passwords must be between 6 and 10 characters long.");
    }
    	
}
