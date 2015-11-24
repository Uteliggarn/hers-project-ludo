package no.hig.hers.ludoclient;

import java.util.logging.Level;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import no.hig.hers.ludoclient.Main;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ClientRegisterUIController {
    @FXML
    private PasswordField passwordTextField;

    @FXML
    private PasswordField confirmTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    void cancelRegistration(ActionEvent event) {
    	Main.changeScene(Main.loginScene);
    }

    @FXML
    void registerUser() {
    	String username = usernameTextField.getText();
    	String password = passwordTextField.getText();
    	String confirm = confirmTextField.getText();
    	
    	if (password.length() >= 6 && password.length() <= 10) {
			if (password.equals(confirm)) {
				try {
					Main.sendLogin("SENDREGISTER:", username, password);
					if("ACCEPTED".equals(Main.input.readLine())) {
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
