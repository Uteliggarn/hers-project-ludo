package no.hig.hers.ludoclient.UIControllers;

import java.io.IOException;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import no.hig.hers.ludoclient.Main;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ClientRegisterUIController {

    @FXML
    private Button cancelButton;

    @FXML
    private Button registerButton;

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
    void registerUser(ActionEvent event) {
    	String username = usernameTextField.getText();
    	String password = passwordTextField.getText();
    	String confirm = confirmTextField.getText();
    	
    	if (password.equals(confirm)) {
			try {
				Main.sendLogin("SENDREGISTER:", username, password);
				if(Main.input.readLine().equals("ACCEPTED")) {
					Main.showAlert("User successfully created", "Congratulations, you have successfully created a new user.");
					Main.changeScene(Main.loginScene);
				}	
				else Main.showAlert("User already exists", "Sorry, that username is already taken.\nPlease select another.");
			} catch (Exception e1) {
				/** TODO FIX THIS! */
			}
    	} else Main.showAlert("Password mistyped", "The passwords do not match.\nPlease try again.");
    }
    	
}
